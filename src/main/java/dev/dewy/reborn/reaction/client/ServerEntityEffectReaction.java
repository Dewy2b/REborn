package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.entity.EntityEquipment;
import dev.dewy.reborn.util.entity.PotionEffect;

public class ServerEntityEffectReaction implements IPacketReactor<ServerEntityEffectPacket> {
    @Override
    public boolean takeAction(ServerEntityEffectPacket packet) {
        PotionEffect effect = new PotionEffect();
        effect.effect = packet.getEffect();
        effect.amplifier = packet.getAmplifier();
        effect.duration = packet.getDuration();
        effect.ambient = packet.isAmbient();
        effect.showParticles = packet.getShowParticles();
        ((EntityEquipment) REbornClient.REbornClientCache.INSTANCE.entityCache.get(packet.getEntityId())).potionEffects.add(effect);
        return true;
    }
}
