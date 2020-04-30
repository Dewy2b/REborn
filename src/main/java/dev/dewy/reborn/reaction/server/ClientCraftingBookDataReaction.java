package dev.dewy.reborn.reaction.server;

import com.github.steveice10.mc.protocol.data.game.window.CraftingBookDataType;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCraftingBookDataPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ClientCraftingBookDataReaction implements IPacketReactor<ClientCraftingBookDataPacket> {


    @Override
    public boolean takeAction(ClientCraftingBookDataPacket packet) {
        if (packet.getType() == CraftingBookDataType.CRAFTING_BOOK_STATUS) {
            REbornClient.REbornClientCache.INSTANCE.wasFilteringRecipes = packet.isFilterActive();
            REbornClient.REbornClientCache.INSTANCE.wasRecipeBookOpened = packet.isCraftingBookOpen();
        }
        return true;
    }
}
