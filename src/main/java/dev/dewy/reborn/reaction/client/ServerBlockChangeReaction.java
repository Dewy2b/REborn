package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.ChunkUtil;

public class ServerBlockChangeReaction implements IPacketReactor<ServerBlockChangePacket> {
    @Override
    public boolean takeAction(ServerBlockChangePacket packet) {
        int chunkX = packet.getRecord().getPosition().getX() >> 4;
        int chunkZ = packet.getRecord().getPosition().getZ() >> 4;
        int cubeY = ChunkUtil.clamp(packet.getRecord().getPosition().getY() >> 4, 0, 15);
        Column column = REbornClient.REbornClientCache.INSTANCE.chunkCache
                .getOrDefault(ChunkUtil.getChunkHashFromXZ(chunkX, chunkZ), null);
        if (column == null) {
            // not ignoring this can leak memory in the notchian client
            REborn.LOGGER.logDebug("Ignoring server request to change blocks in an unloaded chunk, is the remote server running a modified Minecraft server jar? This could cause issues.");
            return false;
        }
        Chunk subChunk = column.getChunks()[cubeY];
        int cubeRelY = Math.abs(packet.getRecord().getPosition().getY() - 16 * cubeY);
        try {
            subChunk.getBlocks().set(Math.abs(Math.abs(packet.getRecord().getPosition().getX()) - (Math.abs(Math.abs(packet.getRecord().getPosition().getX() >> 4)) * 16)), ChunkUtil.clamp(cubeRelY, 0, 15), Math.abs(Math.abs(packet.getRecord().getPosition().getZ()) - (Math.abs(Math.abs(packet.getRecord().getPosition().getZ() >> 4)) * 16)), packet.getRecord().getBlock());
            column.getChunks()[cubeY] = subChunk;
            REbornClient.REbornClientCache.INSTANCE.chunkCache.put(ChunkUtil.getChunkHashFromXZ(chunkX, chunkZ), column);
        } catch (Exception e) {
            //
        }
        REbornClient.REbornClientCache.INSTANCE.chunkCache.put(ChunkUtil.getChunkHashFromXZ(chunkX, chunkZ), column);
        return true;
    }
}
