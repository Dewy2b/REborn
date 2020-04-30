package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerVehicleMovePacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.Entity;
import dev.dewy.reborn.util.entity.EntityRotation;

public class ServerVehicleMoveReaction implements IPacketReactor<ServerVehicleMovePacket> {
    @Override
    public boolean takeAction(ServerVehicleMovePacket packet) {
        Entity entity = Entity.getEntityBeingRiddenBy(REbornClient.REbornClientCache.INSTANCE.entityId);
        if (entity == null) {
            return false;
        }
        entity.posX = packet.getX();
        entity.posY = packet.getY();
        entity.posZ = packet.getZ();
        if (entity instanceof EntityRotation) {
            ((EntityRotation) entity).yaw = packet.getYaw();
            ((EntityRotation) entity).pitch = packet.getPitch();
        }
        return true;
    }
}
