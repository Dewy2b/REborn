package dev.dewy.reborn.server;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.Child;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.util.TextMessageColoured;

import java.net.Proxy;

public class REbornServerManager extends ServerAdapter {

    public void sessionAdded(SessionAddedEvent event) {
        Child cli = new Child(event.getSession());
        REbornServer adapter = new REbornServer(cli);
        REborn.INSTANCE.childClients.add(cli);
        REborn.INSTANCE.childAdapters.put(cli, adapter);
        event.getSession().addListener(adapter);
    }

    public void sessionRemoved(SessionRemovedEvent event) {
        getClientBySession(event.getSession()).setPlaying(false);
        REborn.INSTANCE.childClients.remove(getClientBySession(event.getSession()));
        event.getSession().removeListener(REborn.INSTANCE.childAdapters.get(getClientBySession(event.getSession())));
    }

    public static Child getClientBySession(Session session) {
        for (Child childClient : REborn.INSTANCE.childClients) {
            if (childClient.getSession().getHost().equals(session.getHost())
                    && childClient.getSession().getPort() == session.getPort()) {
                return childClient;
            }
        }
        return null;
    }

    /**
     * Build an instance of the server
     *
     * @return the built server
     */
    public static Server prepareServer() {
        Server server = new Server(REborn.INSTANCE.MAIN_CONFIG.rebornServerIp, REborn.INSTANCE.MAIN_CONFIG.rebornServerPort, MinecraftProtocol.class, new TcpSessionFactory());
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, REborn.INSTANCE.MAIN_CONFIG.onlineMode);
        server.setGlobalFlag
                (MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoBuilder() {
                    @Override
                    public ServerStatusInfo buildInfo(Session session) {
                        return new ServerStatusInfo(
                                new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                                new PlayerInfo(420, (int) REborn.INSTANCE.childClients.stream().filter(e -> ((MinecraftProtocol) e.getSession().getPacketProtocol()).getSubProtocol() != SubProtocol.STATUS).count(), new GameProfile[]{}),
                                TextMessageColoured.from(REborn.INSTANCE.MAIN_CONFIG.motd),
                                null);
                    }
                });
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> session.send(new ServerJoinGamePacket(
                REbornClient.REbornClientCache.INSTANCE.entityId,
                false,
                REbornClient.REbornClientCache.INSTANCE.gameMode,
                REbornClient.REbornClientCache.INSTANCE.dimension,
                Difficulty.NORMAL,
                1,
                WorldType.DEFAULT,
                false)));
        return server;
    }

}
