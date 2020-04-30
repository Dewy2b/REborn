package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.EntityInRangeEvent;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityMob;
import dev.dewy.reborn.util.entity.EntityType;

public class ServerSpawnMobReaction implements IPacketReactor<ServerSpawnMobPacket> {
    @Override
    public boolean takeAction(ServerSpawnMobPacket packet) {
        EntityMob e = new EntityMob();
        e.type = EntityType.MOB;
        e.entityId = packet.getEntityId();
        e.uuid = packet.getUUID();
        e.mobType = packet.getType();
        e.posX = packet.getX();
        e.posY = packet.getY();
        e.posZ = packet.getZ();
        e.pitch = packet.getPitch();
        e.yaw = packet.getYaw();
        e.headYaw = packet.getHeadYaw();
        e.motionX = packet.getMotionX();
        e.motionY = packet.getMotionY();
        e.motionZ = packet.getMotionZ();
        e.metadata = packet.getMetadata();
        REbornClient.REbornClientCache.INSTANCE.entityCache.put(e.entityId, e);
        EntityInRangeEvent.Entity event = new EntityInRangeEvent.Entity(e.uuid, e.entityId);
        REborn.INSTANCE.EVENT_BUS.invokeEvent(event);
        return true;
    }
}
