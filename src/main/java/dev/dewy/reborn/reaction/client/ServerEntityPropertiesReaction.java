package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityRotation;

public class ServerEntityPropertiesReaction implements IPacketReactor<ServerEntityPropertiesPacket> {
    @Override
    public boolean takeAction(ServerEntityPropertiesPacket packet) {
        EntityRotation rotation = (EntityRotation) REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        if (rotation == null) {
            REborn.INSTANCE.sendToChildren(packet);
            return false;
        }
        rotation.properties.addAll(packet.getAttributes());
        return true;
    }
}
