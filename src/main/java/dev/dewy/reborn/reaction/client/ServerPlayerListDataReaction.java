package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerPlayerListDataReaction implements IPacketReactor<ServerPlayerListDataPacket> {
    @Override
    public boolean takeAction(ServerPlayerListDataPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.tabHeader = packet.getHeader();
        REbornClient.REbornClientCache.INSTANCE.tabFooter = packet.getFooter();
        return true;
    }
}
