package dev.dewy.reborn.reaction.server;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ClientKeepAliveReaction implements IPacketReactor<ClientKeepAlivePacket> {


    @Override
    public boolean takeAction(ClientKeepAlivePacket packet) {
        return false;
    }
}
