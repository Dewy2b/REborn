package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.ChunkUtil;

public class ServerMultiBlockChangeReaction implements IPacketReactor<ServerMultiBlockChangePacket> {
    @Override
    public boolean takeAction(ServerMultiBlockChangePacket packet) {
        int chunkX = packet.getRecords()[0].getPosition().getX() >> 4;
        int chunkZ = packet.getRecords()[0].getPosition().getZ() >> 4;
        Column column = REbornClient.REbornClientCache.INSTANCE.chunkCache.getOrDefault(ChunkUtil.getChunkHashFromXZ(chunkX, chunkZ), null);
        if (column == null) {
            // not ignoring this can leak memory in the notchian client
            REborn.LOGGER.logDebug("Ignoring server request to change blocks in an unloaded chunk, is the remote server running a modified Minecraft server jar? This could cause issues.");
            return false;
        }
        for (BlockChangeRecord record : packet.getRecords()) {
            int relativeChunkX = Math.abs(Math.abs(record.getPosition().getX()) - (Math.abs(Math.abs(record.getPosition().getX() >> 4)) * 16));
            int relativeChunkZ = Math.abs(Math.abs(record.getPosition().getZ()) - (Math.abs(Math.abs(record.getPosition().getZ() >> 4)) * 16));
            int cubeY = ChunkUtil.clamp(record.getPosition().getY() >> 4, 0, 15);
            Chunk cube = column.getChunks()[cubeY];
            int cubeRelativeY = Math.abs(record.getPosition().getY() - 16 * cubeY);
            try {
                cube.getBlocks().set(relativeChunkX, ChunkUtil.clamp(cubeRelativeY, 0, 15), relativeChunkZ, record.getBlock());
                column.getChunks()[cubeY] = cube;
            } catch (Exception e) {
                System.out.println(relativeChunkX + " " + cubeRelativeY + " " + relativeChunkZ + " " + (cubeRelativeY << 8 | relativeChunkZ << 4 | relativeChunkX));
            }
        }
        REbornClient.REbornClientCache.INSTANCE.chunkCache.put(ChunkUtil.getChunkHashFromXZ(chunkX, chunkZ), column);
        return true;
    }
}
