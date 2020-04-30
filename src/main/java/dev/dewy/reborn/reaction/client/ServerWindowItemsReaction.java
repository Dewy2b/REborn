package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerWindowItemsReaction implements IPacketReactor<ServerWindowItemsPacket> {
    @Override
    public boolean takeAction(ServerWindowItemsPacket packet) {
        if (packet.getWindowId() == 0) {
            REbornClient.REbornClientCache.INSTANCE.playerInventory = packet.getItems();
        }
        return true;
    }
}
