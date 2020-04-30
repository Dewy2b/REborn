package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerPlayerChangeHeldItemReaction implements IPacketReactor<ServerPlayerChangeHeldItemPacket> {
    @Override
    public boolean takeAction(ServerPlayerChangeHeldItemPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.heldItem = packet.getSlot();
        return true;
    }
}
