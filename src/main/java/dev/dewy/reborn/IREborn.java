package dev.dewy.reborn;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.packetlib.packet.Packet;

import java.net.Proxy;

public interface IREborn {

    void sendFromClient(Packet pck);

    /**
     * Send a packet to all of the currently connected and set up children
     *
     * @param pck The packet to send
     */
    void sendToChildren(Packet pck);

    /**
     * Load REborn any any plugins
     */
    void start(String[] args, boolean restart);

    /**
     * Authenticate with Mojang
     *
     * @param proxy the socks proxy to authenticate against
     * @return A non-null object if successful
     */
    AuthenticationService authenticate(Proxy proxy);

    /**
     * Update the session token inside of the config.
     *
     * @param token The new token
     */
    void updateToken(String token);

    /**
     * Decide if any children are connected to REborn
     *
     * @return bool
     */
    boolean areChildrenConnected();

    /**
     * Register all of REborn's commands
     */
    void registerCommands();

    /**
     * Process a command sent in-game
     *
     * @param s A string beginning with the command prefix (backlash by default
     * @return whether it was processed or not
     */
    boolean processInGameCommand(String s);

    /**
     * Register all of the configurations and read them
     */
    void registerConfigurations();

    /**
     * Stop REborn
     */
    void stop();

    void stopSoft();

    /**
     * Stop and restart REborn
     */
    void reLaunch();
}
