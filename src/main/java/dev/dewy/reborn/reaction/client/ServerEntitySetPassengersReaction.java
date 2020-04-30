package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.Entity;

public class ServerEntitySetPassengersReaction implements IPacketReactor<ServerEntitySetPassengersPacket> {
    @Override
    public boolean takeAction(ServerEntitySetPassengersPacket packet) {
        Entity equipment = REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        if (packet.getPassengerIds().length == 0) {
            equipment.clearEntity();
        } else {
            equipment.setEntity(packet.getPassengerIds());
        }
        return true;
    }
}
