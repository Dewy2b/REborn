package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.Entity;
import dev.dewy.reborn.util.entity.EntityRotation;

import java.lang.reflect.Field;

public class ServerEntityMovementReaction implements IPacketReactor<ServerEntityMovementPacket> {
    @Override
    public boolean takeAction(ServerEntityMovementPacket packet) {
        try {
            Entity e = REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
            if (e == null) {
                REborn.LOGGER.logDebug
                        ("Null entity with entity id " + packet.getEntityId());
                REborn.INSTANCE.sendToChildren(packet);
                return false;
            }
            e.posX += packet.getMovementX() / 4096d;
            e.posY += packet.getMovementY() / 4096d;
            e.posZ += packet.getMovementZ() / 4096d;
            boolean flag;
            Field field = ServerEntityMovementPacket.class.getDeclaredField("rot");
            field.setAccessible(true); // leet hax
            flag = (boolean) field.get(packet);
            if (flag && e instanceof EntityRotation) {
                ((EntityRotation) e).yaw = packet.getYaw();
                ((EntityRotation) e).pitch = packet.getPitch();
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            //
        }
        return true;
    }
}
