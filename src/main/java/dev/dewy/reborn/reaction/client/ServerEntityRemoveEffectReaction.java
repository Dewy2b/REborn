package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityEquipment;

public class ServerEntityRemoveEffectReaction implements IPacketReactor<ServerEntityRemoveEffectPacket> {
    @Override
    public boolean takeAction(ServerEntityRemoveEffectPacket packet) {
        EntityEquipment e = (EntityEquipment) REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        e.potionEffects.remove(packet.getEffect());
        return true;
    }
}
