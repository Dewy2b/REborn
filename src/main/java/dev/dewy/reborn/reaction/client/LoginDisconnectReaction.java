package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.login.server.LoginDisconnectPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.reaction.IPacketReactor;

public class LoginDisconnectReaction implements IPacketReactor<LoginDisconnectPacket> {
    @Override
    public boolean takeAction(LoginDisconnectPacket packet) {
        REborn.LOGGER.logError("Kicked whilst logging in: " + packet.getReason().getFullText());
        REborn.INSTANCE.minecraftClient.getSession().disconnect(packet.getReason().getFullText(), true);
        return true;
    }
}
