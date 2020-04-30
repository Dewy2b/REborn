package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.server.REbornServerManager;

public class ServerUpdateTimeReaction implements IPacketReactor<ServerUpdateTimePacket> {
    @Override
    public boolean takeAction(ServerUpdateTimePacket packet) {
        if (!REbornClient.REbornClientCache.INSTANCE.serverTicked) {
            REbornClient.REbornClientCache.INSTANCE.serverTicked = true;
            REborn.LOGGER.log("Starting server on " + REborn.INSTANCE.MAIN_CONFIG.rebornServerIp + ":" +
                    REborn.INSTANCE.MAIN_CONFIG.rebornServerPort);
            try {
                REborn.INSTANCE.minecraftServer = REbornServerManager.prepareServer();
                REborn.INSTANCE.minecraftServer.addListener(new REbornServerManager());
                REborn.INSTANCE.minecraftServer.bind(true);
            } catch (Exception e) {
                e.printStackTrace();
                REborn.LOGGER.logError("A severe exception occurred whilst creating the server! Maybe there's already a server running on the port?");
            }
            REborn.LOGGER.log("Server started!");
        }
        return true;
    }
}
