package dev.dewy.reborn.reaction.server;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ClientChatReaction implements IPacketReactor<ClientChatPacket> {

    @Override
    public boolean takeAction(ClientChatPacket packet) {
        return !REborn.INSTANCE.processInGameCommand(packet.getMessage());
    }
}
