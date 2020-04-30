package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerEntityCollectItemReaction implements IPacketReactor<ServerEntityCollectItemPacket> {
    @Override
    public boolean takeAction(ServerEntityCollectItemPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.entityCache.remove(packet.getCollectedEntityId());
        return true;
    }
}
