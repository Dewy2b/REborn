package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class LoginSuccessReaction implements IPacketReactor<LoginSuccessPacket> {
    @Override
    public boolean takeAction(LoginSuccessPacket packet) {
        REbornClient.REbornClientCache.INSTANCE.uuid = packet.getProfile().getId();
        return true;
    }
}
