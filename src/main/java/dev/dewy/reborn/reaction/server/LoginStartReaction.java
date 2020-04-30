package dev.dewy.reborn.reaction.server;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.reaction.AbstractChildPacketReactor;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.server.REbornServer;

public class LoginStartReaction extends AbstractChildPacketReactor implements IPacketReactor<LoginStartPacket> {


    @Override
    public boolean takeAction(LoginStartPacket packet) {
        if (((MinecraftProtocol) this.getChild().getSession().getPacketProtocol()).getSubProtocol() != SubProtocol.LOGIN) {
            return false;
        }
        REborn.LOGGER.log("Child user %s connecting!".replace("%s", packet.getUsername()));
        REbornServer.runWhitelist(packet.getUsername(), this.getChild());
        return false;
    }
}
