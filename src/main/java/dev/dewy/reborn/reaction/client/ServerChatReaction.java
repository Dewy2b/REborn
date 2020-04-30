package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.ChatReceivedEvent;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerChatReaction implements IPacketReactor<ServerChatPacket> {
    @Override
    public boolean takeAction(ServerChatPacket packet) {

        ChatReceivedEvent chatEvent = new ChatReceivedEvent(packet.getMessage().getFullText(), System.currentTimeMillis());
        REborn.INSTANCE.EVENT_BUS.invokeEvent(chatEvent);
        REborn.LOGGER.log("(CHAT) " + packet.getMessage().getFullText());
        REborn.INSTANCE.sendToChildren(new ServerChatPacket(packet.getMessage().getFullText(), packet.getType()));

        return false;
    }
}
