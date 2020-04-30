package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityPlayer;
import dev.dewy.reborn.util.entity.EntityType;

public class ServerJoinGameReaction implements IPacketReactor<ServerJoinGamePacket> {
    @Override
    public boolean takeAction(ServerJoinGamePacket packet) {
        REbornClient.REbornClientCache.INSTANCE.dimension = packet.getDimension();
        REbornClient.REbornClientCache.INSTANCE.entityId = packet.getEntityId();
        REbornClient.REbornClientCache.INSTANCE.gameMode = packet.getGameMode();
        EntityPlayer player = new EntityPlayer();
        player.type = EntityType.REAL_PLAYER;
        player.entityId = REbornClient.REbornClientCache.INSTANCE.entityId;
        player.uuid = REbornClient.REbornClientCache.INSTANCE.uuid;
        REbornClient.REbornClientCache.INSTANCE.player = player;
        REbornClient.REbornClientCache.INSTANCE.entityCache.put(player.entityId, player);
        return true;
    }
}
