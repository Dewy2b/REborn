package dev.dewy.reborn;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.sasha.eventsys.SimpleEventManager;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import dev.dewy.reborn.api.REbornPlugin;
import dev.dewy.reborn.api.REbornPluginLoader;
import dev.dewy.reborn.api.event.MojangAuthenticateEvent;
import dev.dewy.reborn.api.event.ServerPingEvent;
import dev.dewy.reborn.client.Child;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.command.game.AboutCommand;
import dev.dewy.reborn.command.game.PluginsCommand;
import dev.dewy.reborn.command.terminal.ExitCommand;
import dev.dewy.reborn.command.terminal.LoginCommand;
import dev.dewy.reborn.command.terminal.RelaunchCommand;
import dev.dewy.reborn.command.terminal.ReloadCommand;
import dev.dewy.reborn.logging.ILogger;
import dev.dewy.reborn.logging.Logger;
import dev.dewy.reborn.util.AwaitThread;
import dev.dewy.reborn.util.PingStatus;
import dev.dewy.reborn.util.ServerPinger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The main REborn class, where the majority of essential startup functions will be stored.
 */
public class REborn implements IREborn {

    public static final String DATA_FILE = "REborn";
    /**
     * Current software version of REborn
     */
    public static String VERSION = "3.0.1";
    /**
     * The command line command processor
     */
    public static final SimpleCommandProcessor TERMINAL_CMD_PROCESSOR = new SimpleCommandProcessor("");
    /**
     * The in-game command processor
     */
    public static final SimpleCommandProcessor INGAME_CMD_PROCESSOR = new SimpleCommandProcessor("\\");
    /**
     * Singleton of this REborn
     */
    public static REborn INSTANCE;
    /**
     * The JLine terminal instance
     */
    public static Terminal terminal;
    /**
     * The Jline reader instance
     */
    public static LineReader reader;

    public static ILogger LOGGER;
    /**
     * The args from when we first started the program
     */
    public static String[] args = new String[]{};

    private static final Thread shutdownThread = new Thread(() -> REborn.INSTANCE.stopSoft());
    /**
     * The event manager for REborn
     */
    public final SimpleEventManager EVENT_BUS = new SimpleEventManager();
    public List<Config> configs = new ArrayList<>();
    public Config MAIN_CONFIG = new Config(DATA_FILE);

    public Client minecraftClient = null;
    public Server minecraftServer = null;
    public MinecraftProtocol protocol;
    public List<Child> childClients = new ArrayList<>();
    public LinkedHashMap<Child, SessionListener> childAdapters = new LinkedHashMap<>();
    private static REbornPluginLoader loader;
    private boolean isShuttingDownCompletely = false;
    private boolean isRelaunching = false;

    /**
     * Launch REborn and and setup the console command system.
     */
    public static void main(String[] args) throws IOException {
        REborn.args = args;
        terminal = TerminalBuilder.builder().name("REborn").system(true).build();
        reader = LineReaderBuilder.builder().terminal(terminal).build();
        LOGGER = new Logger("REborn " + VERSION);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
        loader = new REbornPluginLoader();
        loader.preparePlugins(loader.findPlugins());
        loader.loadPlugins();
        Thread thread = new Thread(() -> {
            new REborn().start(args, false);
        });

        thread.start();

        while (true) {
            try {
                String cmd = reader.readLine(null, null, "> ");
                TERMINAL_CMD_PROCESSOR.processCommand(cmd.trim().replace("> ", ""));
            } catch (UserInterruptException | IllegalStateException | EndOfFileException ex) {
                REborn.INSTANCE.stop();
                return;
            }
        }
    }

    @Override
    public void sendFromClient(Packet pck) {
        if (minecraftClient == null ||
                !minecraftClient.getSession().isConnected() ||
                ((MinecraftProtocol)minecraftClient.getSession().getPacketProtocol()).getSubProtocol() != SubProtocol.GAME) {
            return;
        }
        minecraftClient.getSession().send(pck);
    }

    @Override
    public void sendToChildren(Packet pck) {
        INSTANCE.childClients.stream()
                .filter(Child::isPlaying)
                .forEach(client -> client.getSession().send(pck));
    }

    /**
     * Launch (or relaunch) REborn
     */
    @Override
    public void start(String[] args, boolean restart) {
        isRelaunching = false;
        isShuttingDownCompletely = false;
        try {
            INSTANCE = this;
            new REbornClient.REbornClientCache();
            LOGGER.log("Starting REborn " + VERSION + " for Minecraft " + MinecraftConstants.GAME_VERSION);
            this.registerCommands();
            this.registerConfigurations();
            configs.forEach(Config::configure); // set config vars
            if (!restart) REbornPluginLoader.initPlugins();
            Proxy proxy = Proxy.NO_PROXY;
            if (MAIN_CONFIG.socksProxy != null && !MAIN_CONFIG.socksProxy.equalsIgnoreCase("[no default]") && MAIN_CONFIG.socksPort != -1) {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(InetAddress.getByName(MAIN_CONFIG.socksProxy), MAIN_CONFIG.socksPort));
            }
            if (!canPing()) {
                this.reLaunch();
                return;
            }
            AuthenticationService service = authenticate(MAIN_CONFIG.authNoProxy ? Proxy.NO_PROXY : proxy);// log into mc
            if (service != null) {
                minecraftClient = new Client(MAIN_CONFIG.serverIp,
                        MAIN_CONFIG.serverPort,
                        protocol,
                        new TcpSessionFactory(proxy));
                minecraftClient.getSession().addListener(new REbornClient());
                LOGGER.log("Connecting...");
                minecraftClient.getSession().connect(true); // connect to the remote server
                LOGGER.log("Connected!");
                REbornPluginLoader.enablePlugins();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.logError("A SEVERE EXCEPTION OCCURRED WHILST STARTING REborn");
        }
    }

    public boolean canPing() {
        ServerPingEvent.Pre event = new ServerPingEvent.Pre();
        EVENT_BUS.invokeEvent(event);
        // if the event is cancelled, skip pinging the server.
        if (!event.isCancelled()) {
            ServerPinger pinger = new ServerPinger(MAIN_CONFIG.serverIp, MAIN_CONFIG.serverPort);
            pinger.status(LOGGER);
            int time = 0;
            while (pinger.pinged == PingStatus.PINGING) {
                try {
                    Thread.sleep(1000L);
                    time++;
                    if (MAIN_CONFIG.pingTimeout > 0 && time > MAIN_CONFIG.pingTimeout) {
                        pinger.pinged = PingStatus.PINGED;
                        LOGGER.logWarning("Ping timeout. Trying to connect anyways.");
                        break;
                    }
                } catch (InterruptedException ignored) {}
            }
            ServerPingEvent.Post post = new ServerPingEvent.Post(pinger.ms, pinger.pinged);
            EVENT_BUS.invokeEvent(post);
            PingStatus status = post.getStatus();
            if (status == PingStatus.DEAD) {
                LOGGER.logError("Server offline. Will relaunch until it's online.");
                this.reLaunch();
                return false;
            }
        }
        return true;
    }

    /**
     * Authenticate with Mojang, first via session token, then via email/password
     */
    @Override
    public AuthenticationService authenticate(Proxy proxy) {
        if (!MAIN_CONFIG.sessionId.equalsIgnoreCase("[no default]")) {
            try {
                MojangAuthenticateEvent.Pre event = new MojangAuthenticateEvent.Pre(MojangAuthenticateEvent.Method.SESSIONID);
                this.EVENT_BUS.invokeEvent(event);
                if (event.isCancelled()) {
                    return null;
                }
                // try authing with session id first, since it [appears] to be present
                REborn.LOGGER.log("Attempting to log in with session token");
                AuthenticationService authServ = new AuthenticationService(MAIN_CONFIG.clientId, proxy);
                authServ.setUsername(MAIN_CONFIG.email);
                authServ.setAccessToken(MAIN_CONFIG.sessionId);
                authServ.login();
                protocol = new MinecraftProtocol(authServ.getSelectedProfile(), MAIN_CONFIG.clientId, authServ.getAccessToken());
                updateToken(authServ.getAccessToken());
                MojangAuthenticateEvent.Post postEvent = new MojangAuthenticateEvent.Post(MojangAuthenticateEvent.Method.SESSIONID, true);
                this.EVENT_BUS.invokeEvent(postEvent);
                REborn.LOGGER.log("Logged in as " + authServ.getSelectedProfile().getName());
                REbornClient.REbornClientCache.INSTANCE.playerName = authServ.getSelectedProfile().getName();
                REbornClient.REbornClientCache.INSTANCE.playerUuid = authServ.getSelectedProfile().getId();
                return authServ;
            } catch (RequestException ex) {
                // the session token is invalid
                MojangAuthenticateEvent.Post postEvent = new MojangAuthenticateEvent.Post(MojangAuthenticateEvent.Method.SESSIONID, false);
                this.EVENT_BUS.invokeEvent(postEvent);
                REborn.LOGGER.logError("Session token was invalid!");
            }
        }
        // log in normally w username and password
        REborn.LOGGER.log("Attemping to log in with email and password");
        try {
            MojangAuthenticateEvent.Pre event = new MojangAuthenticateEvent.Pre(MojangAuthenticateEvent.Method.EMAILPASS);
            this.EVENT_BUS.invokeEvent(event);
            if (event.isCancelled()) return null;
            AuthenticationService authServ = new AuthenticationService(MAIN_CONFIG.clientId, proxy);
            authServ.setUsername(MAIN_CONFIG.email);
            authServ.setPassword(MAIN_CONFIG.password);
            authServ.login();
            protocol = new MinecraftProtocol(authServ.getSelectedProfile(), MAIN_CONFIG.clientId, authServ.getAccessToken());
            updateToken(authServ.getAccessToken());
            REborn.LOGGER.log("Logged in as " + authServ.getSelectedProfile().getName());
            REbornClient.REbornClientCache.INSTANCE.playerName = authServ.getSelectedProfile().getName();
            REbornClient.REbornClientCache.INSTANCE.playerUuid = authServ.getSelectedProfile().getId();
            MojangAuthenticateEvent.Post postEvent = new MojangAuthenticateEvent.Post(MojangAuthenticateEvent.Method.EMAILPASS, true);
            this.EVENT_BUS.invokeEvent(postEvent);
            return authServ;
        } catch (RequestException e) {
            // login completely failed
            MojangAuthenticateEvent.Post postEvent = new MojangAuthenticateEvent.Post(MojangAuthenticateEvent.Method.EMAILPASS, false);
            this.EVENT_BUS.invokeEvent(postEvent);
            REborn.LOGGER.logError(e.getMessage());
            REborn.LOGGER.logError("Could not login with Mojang.");
            if (postEvent.isCancelled()) {
                return null;
            }
            this.reLaunch();
        }
        return null;
    }

    /**
     * Update the session token inside REborn.yml
     */
    @Override
    public void updateToken(String token) {
        MAIN_CONFIG.sessionId = token;
    }

    @Override
    public boolean areChildrenConnected() {
        return !childClients.isEmpty();
    }

    @Override
    public void registerCommands() {
        TERMINAL_CMD_PROCESSOR.getCommandRegistry().clear();
        INGAME_CMD_PROCESSOR.getCommandRegistry().clear();
        try {
            TERMINAL_CMD_PROCESSOR.register(ExitCommand.class);
            TERMINAL_CMD_PROCESSOR.register(RelaunchCommand.class);
            INGAME_CMD_PROCESSOR.register(PluginsCommand.class);
            INGAME_CMD_PROCESSOR.register(AboutCommand.class);
            TERMINAL_CMD_PROCESSOR.register(LoginCommand.class);
            TERMINAL_CMD_PROCESSOR.register(ReloadCommand.class);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        REbornPluginLoader.getPluginList().forEach(REbornPlugin::registerCommands);
    }

    @Override
    public boolean processInGameCommand(String s) {
        if (!s.startsWith("\\")) {
            return false;
        }
        INGAME_CMD_PROCESSOR.processCommand(s);
        return true;
    }

    @Override
    public void registerConfigurations() {
        configs.add(MAIN_CONFIG);
        REbornPluginLoader.getPluginList().forEach(REbornPlugin::registerConfig);
    }

    /**
     * Stop and close REborn
     */
    @Override
    public void stop() {
        if (isShuttingDownCompletely) return;
        isShuttingDownCompletely = true;
        configs.forEach(Config::save);
        Runtime.getRuntime().removeShutdownHook(shutdownThread);
        LOGGER.log("Stopping REborn...");
        REbornPluginLoader.shutdownPlugins();
        REbornPluginLoader.getPluginList().clear();
        if (minecraftServer != null) {
            minecraftServer.getSessions().forEach(session -> session.disconnect("REborn is shutting down!", true));
            minecraftServer.close(true);
        }
        if (minecraftClient != null && minecraftClient.getSession().isConnected())
            minecraftClient.getSession().disconnect("REborn is shutting down...", true);
        LOGGER.log("Stopped REborn...");
        System.exit(0);
    }

    @Override
    public void stopSoft() {
        if (isShuttingDownCompletely) return;
        isShuttingDownCompletely = true;
        configs.forEach(Config::save);
        LOGGER.log("Stopping REborn...");
        REbornPluginLoader.shutdownPlugins();
        REbornPluginLoader.getPluginList().clear();
        if (minecraftServer != null) {
            minecraftServer.getSessions().forEach(session -> session.disconnect("REborn is shutting down!", true));
            minecraftServer.close(true);
        }
        if (minecraftClient != null && minecraftClient.getSession().isConnected())
            minecraftClient.getSession().disconnect("REborn is shutting down...", true);
        LOGGER.log("Stopped REborn...");
    }

    /**
     * Invoked if the player gets kicked from the remote server
     */
    @Override
    public void reLaunch() {
        if (isShuttingDownCompletely) return;
        if (isRelaunching) return;
        isRelaunching = true;
        configs.forEach(Config::save);
        REbornPluginLoader.disablePlugins();
        if (minecraftClient != null && minecraftClient.getSession().isConnected())
            minecraftClient.getSession().disconnect("REborn is restarting!");
        if (minecraftServer != null) {
            minecraftServer.getSessions().forEach(session -> session.disconnect("REborn is restarting!", true));
            minecraftServer.close(true);
        }
        REbornClient.REbornClientCache.INSTANCE.chunkCache.clear();
        REbornClient.REbornClientCache.INSTANCE.entityCache.clear();
        final int[] i = {-1};
        CountDownLatch latch = new CountDownLatch(1);
        AwaitThread thread = new AwaitThread(latch) {
            @Override
            public void run() {
                for (i[0] = MAIN_CONFIG.reconnectDelay; i[0] >= 0; i[0]--) {
                    REborn.LOGGER.logWarning("Reconnecting in " + i[0] + " seconds");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.finish();
            }
        };
        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().removeShutdownHook(shutdownThread);
        REborn.INSTANCE.start(REborn.args, true);
    }
}
