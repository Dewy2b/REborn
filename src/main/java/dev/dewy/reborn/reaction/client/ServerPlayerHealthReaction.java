package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.PlayerDamagedEvent;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

public class ServerPlayerHealthReaction implements IPacketReactor<ServerPlayerHealthPacket> {
    @Override
    public boolean takeAction(ServerPlayerHealthPacket packet) {
        PlayerDamagedEvent damagedEvent = new PlayerDamagedEvent(REbornClient.REbornClientCache.INSTANCE.health, packet.getHealth());
        REborn.INSTANCE.EVENT_BUS.invokeEvent(damagedEvent);
        REbornClient.REbornClientCache.INSTANCE.health = packet.getHealth();
        REbornClient.REbornClientCache.INSTANCE.food = packet.getFood();
        REbornClient.REbornClientCache.INSTANCE.saturation = packet.getSaturation();
        if (REbornClient.REbornClientCache.INSTANCE.health <= 0.0f) {
            REborn.INSTANCE.minecraftClient.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
        }
        return true;
    }
}
