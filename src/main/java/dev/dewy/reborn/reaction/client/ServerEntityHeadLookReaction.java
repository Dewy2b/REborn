package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityRotation;

public class ServerEntityHeadLookReaction implements IPacketReactor<ServerEntityHeadLookPacket> {
    @Override
    public boolean takeAction(ServerEntityHeadLookPacket packet) {
        EntityRotation e = (EntityRotation) REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId());
        if (e == null) {
            REborn.LOGGER.logDebug
                    ("Null entity with entity id " + packet.getEntityId());
            REborn.INSTANCE.sendToChildren(packet);
            return false;
        }
        e.headYaw = packet.getHeadYaw();
        return true;
    }
}
