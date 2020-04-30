package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.Entity;
import dev.dewy.reborn.util.entity.EntityEquipment;
import dev.dewy.reborn.util.entity.EntityObject;

public class ServerEntityEquipmentReaction implements IPacketReactor<ServerEntityEquipmentPacket> {
    @Override
    public boolean takeAction(ServerEntityEquipmentPacket packet) {
        Entity entity = REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        if (entity instanceof EntityObject) {
            REborn.LOGGER.logError("Server tried adding equipment to an EntityObject! Ignoring.");
            return false;
        }
        EntityEquipment equipment = (EntityEquipment) entity;
        equipment.equipment.put(packet.getSlot(), packet.getItem());
        return true;
    }
}
