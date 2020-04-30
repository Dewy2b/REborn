package dev.dewy.reborn.reaction.server;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ClientPlayerPositionRotationReaction implements IPacketReactor<ClientPlayerPositionRotationPacket> {

    @Override
    public boolean takeAction(ClientPlayerPositionRotationPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.posX = packet.getX();
        REbornClient.REbornClientCache.INSTANCE.player.posX = packet.getX();
        REbornClient.REbornClientCache.INSTANCE.posY = packet.getY();
        REbornClient.REbornClientCache.INSTANCE.player.posY = packet.getY();
        REbornClient.REbornClientCache.INSTANCE.posZ = packet.getZ();
        REbornClient.REbornClientCache.INSTANCE.player.posZ = packet.getZ();
        REbornClient.REbornClientCache.INSTANCE.yaw = (float) packet.getYaw();
        REbornClient.REbornClientCache.INSTANCE.player.yaw = (float) packet.getYaw();
        REbornClient.REbornClientCache.INSTANCE.pitch = (float) packet.getPitch();
        REbornClient.REbornClientCache.INSTANCE.player.pitch = (float) packet.getPitch();
        REbornClient.REbornClientCache.INSTANCE.onGround = packet.isOnGround();
        return true;
    }

}
