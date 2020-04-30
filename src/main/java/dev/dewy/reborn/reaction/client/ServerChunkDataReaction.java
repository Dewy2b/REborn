package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.ChunkUtil;

public class ServerChunkDataReaction implements IPacketReactor<ServerChunkDataPacket> {
    @Override
    public boolean takeAction(ServerChunkDataPacket packet) {
        // VERY IMPORTANT: Chunks will NOT RENDER correctly and be invisible on notchian clients if we
        // do not actually push them correctly. This is apparent with big chunks and newly generated ones
        // that need to be dispersed over multiple packets. Trust me, it's really gay.
        // btw i love phi <33333333333333333 hes like a super nice bf
        Column column = packet.getColumn();
        long hash = ChunkUtil.getChunkHashFromXZ(column.getX(), column.getZ());
        if (!column.hasBiomeData()) {
            // if the chunk is thicc or newly generated
            if (REbornClient.REbornClientCache.INSTANCE.chunkCache.containsKey(hash)) {
                Column chunkToAddTo = REbornClient.REbornClientCache.INSTANCE.chunkCache.get(hash);
                REborn.INSTANCE.sendToChildren(new ServerUnloadChunkPacket(chunkToAddTo.getX(), chunkToAddTo.getZ()));
                for (int i = 0; i <= 15; i++) {
                    if (column.getChunks()[i] != null) {
                        chunkToAddTo.getChunks()[i] = column.getChunks()[i];
                    }
                }
                REbornClient.REbornClientCache.INSTANCE.chunkCache.put(hash, chunkToAddTo);
                REborn.INSTANCE.sendToChildren(new ServerChunkDataPacket(chunkToAddTo));
            }
        } else {
            REbornClient.REbornClientCache.INSTANCE.chunkCache.put(hash, packet.getColumn());
        }
        return true;
    }
}
