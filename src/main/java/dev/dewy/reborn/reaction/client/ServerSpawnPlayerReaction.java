package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.EntityInRangeEvent;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityPlayer;
import dev.dewy.reborn.util.entity.EntityType;

public class ServerSpawnPlayerReaction implements IPacketReactor<ServerSpawnPlayerPacket> {
    @Override
    public boolean takeAction(ServerSpawnPlayerPacket packet) {
        EntityPlayer e = new EntityPlayer();
        e.type = EntityType.PLAYER;
        e.entityId = packet.getEntityId();
        e.uuid = packet.getUUID();
        e.posX = packet.getX();
        e.posY = packet.getY();
        e.posZ = packet.getZ();
        e.pitch = packet.getPitch();
        e.yaw = packet.getYaw();
        e.metadata = packet.getMetadata();
        REbornClient.REbornClientCache.INSTANCE.entityCache.put(e.entityId, e);
        System.out.println(REbornClient.REbornClientCache.INSTANCE.getGameProfileByUuid(e.uuid).getName());
        EntityInRangeEvent.Player event = new EntityInRangeEvent.Player(e.uuid, e.entityId);
        REborn.INSTANCE.EVENT_BUS.invokeEvent(event);
        return true;
    }
}
