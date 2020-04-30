package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerEntityDestroyReaction implements IPacketReactor<ServerEntityDestroyPacket> {
    @Override
    public boolean takeAction(ServerEntityDestroyPacket packet) {
        for (int entityId : packet.getEntityIds()) {
            REbornClient.REbornClientCache.INSTANCE.entityCache.remove(entityId);
        }
        return true;
    }
}
