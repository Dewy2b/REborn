package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.Child;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.Entity;
import dev.dewy.reborn.util.entity.EntityRotation;

public class ServerEntityTeleportReaction implements IPacketReactor<ServerEntityTeleportPacket> {
    @Override
    public boolean takeAction(ServerEntityTeleportPacket packet) {
        Entity entity = REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        if (entity == null) {
            REborn.LOGGER.logDebug
                    ("Null entity with entity id " + packet.getEntityId());
            REborn.INSTANCE.childClients.stream()
                    .filter(Child::isPlaying)
                    .forEach(client -> client.getSession().send(packet));
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
