package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.ChunkUtil;

public class ServerUnloadChunkReaction implements IPacketReactor<ServerUnloadChunkPacket> {
    @Override
    public boolean takeAction(ServerUnloadChunkPacket packet) {
        long hash = ChunkUtil.getChunkHashFromXZ(packet.getX(), packet.getZ());
        REbornClient.REbornClientCache.INSTANCE.chunkCache.remove(hash);
        return true;
    }
}
