package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.ServerResetPlayerPositionEvent;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerPlayerPositionRotationReaction implements IPacketReactor<ServerPlayerPositionRotationPacket> {
    @Override
    public boolean takeAction(ServerPlayerPositionRotationPacket packet) {
        ServerResetPlayerPositionEvent resetEvent = new ServerResetPlayerPositionEvent(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
        REborn.INSTANCE.EVENT_BUS.invokeEvent(resetEvent);
        REbornClient.REbornClientCache.INSTANCE.posX = packet.getX();
        REbornClient.REbornClientCache.INSTANCE.posY = packet.getY();
        REbornClient.REbornClientCache.INSTANCE.posZ = packet.getZ();
        REbornClient.REbornClientCache.INSTANCE.pitch = packet.getPitch();
        REbornClient.REbornClientCache.INSTANCE.yaw = packet.getYaw();
        if (!REborn.INSTANCE.areChildrenConnected()) {
            // the notchian client will do this for us, if one is connected
            REborn.INSTANCE.minecraftClient.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
        }
        return true;
    }
}
