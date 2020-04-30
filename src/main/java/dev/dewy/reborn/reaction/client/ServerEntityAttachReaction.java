package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityRotation;

public class ServerEntityAttachReaction implements IPacketReactor<ServerEntityAttachPacket> {
    @Override
    public boolean takeAction(ServerEntityAttachPacket packet) {
        EntityRotation entityRotation = (EntityRotation) REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        if (packet.getAttachedToId() == -1) {
            entityRotation.isLeashed = false;
            entityRotation.leashedID = packet.getAttachedToId();
        } else {
            entityRotation.isLeashed = true;
            entityRotation.leashedID = packet.getAttachedToId();
        }
        return true;
    }
}
