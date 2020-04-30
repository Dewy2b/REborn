package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerRespawnReaction implements IPacketReactor<ServerRespawnPacket> {
    @Override
    public boolean takeAction(ServerRespawnPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.dimension = packet.getDimension();
        REbornClient.REbornClientCache.INSTANCE.gameMode = packet.getGameMode();
        REbornClient.REbornClientCache.INSTANCE.chunkCache.clear();
        REbornClient.REbornClientCache.INSTANCE.entityCache.entrySet().removeIf(integerEntityEntry -> integerEntityEntry.getKey() != REbornClient.REbornClientCache.INSTANCE.entityId);
        REbornClient.REbornClientCache.INSTANCE.cachedBossBars.clear();
        REbornClient.REbornClientCache.INSTANCE.player.potionEffects.clear();
        return true;
    }
}
