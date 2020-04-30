package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.world.notify.ClientNotification;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerNotifyClientReaction implements IPacketReactor<ServerNotifyClientPacket> {
    @Override
    public boolean takeAction(ServerNotifyClientPacket packet) {
        if (packet.getNotification() == ClientNotification.CHANGE_GAMEMODE) {
            REbornClient.REbornClientCache.INSTANCE.gameMode = (GameMode) packet.getValue();
        }
        return true;
    }
}
