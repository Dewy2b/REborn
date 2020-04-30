package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerUnlockRecipesPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerUnlockRecipesReaction implements IPacketReactor<ServerUnlockRecipesPacket> {
    @Override
    public boolean takeAction(ServerUnlockRecipesPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.wasRecipeBookOpened = packet.getOpenCraftingBook();
        REbornClient.REbornClientCache.INSTANCE.wasFilteringRecipes = packet.getActivateFiltering();
        switch (packet.getAction()) {
            case ADD:
                for (Integer recipe : packet.getRecipes()) {
                    if (REbornClient.REbornClientCache.INSTANCE.recipeCache.contains(recipe)) continue;
                    REbornClient.REbornClientCache.INSTANCE.recipeCache.add(recipe);
                }
                break;
            case REMOVE:
                for (Integer recipe : packet.getRecipes()) {
                    if (!REbornClient.REbornClientCache.INSTANCE.recipeCache.contains(recipe)) continue;
                    REbornClient.REbornClientCache.INSTANCE.recipeCache.remove(recipe);
                }
                break;
            case INIT:
                for (Integer alreadyKnownRecipe : packet.getAlreadyKnownRecipes()) {
                    if (REbornClient.REbornClientCache.INSTANCE.recipeCache.contains(alreadyKnownRecipe)) continue;
                    REbornClient.REbornClientCache.INSTANCE.recipeCache.add(alreadyKnownRecipe);
                }
                for (Integer recipe : packet.getRecipes()) {
                    if (!REbornClient.REbornClientCache.INSTANCE.recipeCache.contains(recipe)) continue;
                    REbornClient.REbornClientCache.INSTANCE.recipeCache.remove(recipe);
                }
                break;
            default:
                for (Integer alreadyKnownRecipe : packet.getAlreadyKnownRecipes()) {
                    if (REbornClient.REbornClientCache.INSTANCE.recipeCache.contains(alreadyKnownRecipe)) continue;
                    REbornClient.REbornClientCache.INSTANCE.recipeCache.add(alreadyKnownRecipe);
                }
                for (Integer recipe : packet.getRecipes()) {
                    if (!REbornClient.REbornClientCache.INSTANCE.recipeCache.contains(recipe)) continue;
                    REbornClient.REbornClientCache.INSTANCE.recipeCache.remove(recipe);
                }
        }
        return true;
    }
}
