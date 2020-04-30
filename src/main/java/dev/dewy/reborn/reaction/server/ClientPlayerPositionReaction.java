package dev.dewy.reborn.reaction.server;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ClientPlayerPositionReaction implements IPacketReactor<ClientPlayerPositionPacket> {
    @Override
    public boolean takeAction(ClientPlayerPositionPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.posX = packet.getX();
        REbornClient.REbornClientCache.INSTANCE.player.posX = packet.getX();
        REbornClient.REbornClientCache.INSTANCE.posY = packet.getY();
        REbornClient.REbornClientCache.INSTANCE.player.posY = packet.getY();
        REbornClient.REbornClientCache.INSTANCE.posZ = packet.getZ();
        REbornClient.REbornClientCache.INSTANCE.player.posZ = packet.getZ();
        REbornClient.REbornClientCache.INSTANCE.onGround = packet.isOnGround();
        return true;
    }

}
