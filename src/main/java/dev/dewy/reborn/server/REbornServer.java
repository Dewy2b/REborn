package dev.dewy.reborn.server;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.UnlockRecipesAction;
import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCraftingBookDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.ChildDisconnectEvent;
import dev.dewy.reborn.api.event.ChildJoinEvent;
import dev.dewy.reborn.api.event.ChildServerPacketRecieveEvent;
import dev.dewy.reborn.api.event.ChildServerPacketSendEvent;
import dev.dewy.reborn.client.Child;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.AbstractChildPacketReactor;
import dev.dewy.reborn.reaction.IPacketReactor;
import dev.dewy.reborn.util.TextMessageColoured;
import dev.dewy.reborn.reaction.server.*;
import dev.dewy.reborn.util.entity.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class REbornServer extends SessionAdapter {

    private LinkedHashMap<Class<? extends Packet>, IPacketReactor<?>> reactionRegistry = new LinkedHashMap<>();
    private Child child;

    public REbornServer(Child child) {
        this.child = child;
        this.reactionRegistry.put(ClientChatPacket.class, new ClientChatReaction());
        this.reactionRegistry.put(ClientCraftingBookDataPacket.class, new ClientCraftingBookDataReaction());
        this.reactionRegistry.put(ClientKeepAlivePacket.class, new ClientKeepAliveReaction());
        this.reactionRegistry.put(ClientPlayerPositionPacket.class, new ClientPlayerPositionReaction());
        this.reactionRegistry.put(ClientPlayerPositionRotationPacket.class, new ClientPlayerPositionRotationReaction());
        this.reactionRegistry.put(LoginStartPacket.class, new LoginStartReaction());
    }

    /**
     * Invoked when the child sends us a packet
     */
    @Override
    public void packetReceived(PacketReceivedEvent ev) {
        ChildServerPacketRecieveEvent event = new ChildServerPacketRecieveEvent(this.child, ev.getPacket());
        REborn.INSTANCE.EVENT_BUS.invokeEvent(event);
        try {
            if (!reactionRegistry.containsKey(ev.getPacket().getClass())) { // so we aren't blocking packets that dont need special processing
                if (((MinecraftProtocol) this.child.getSession().getPacketProtocol()).getSubProtocol() != SubProtocol.GAME)
                    return;
                REborn.INSTANCE.minecraftClient.getSession().send(event.getRecievedPacket());
                return;
            }
            this.reactionRegistry.forEach((pck, reactor) -> { // iterate over the registered reactions
                if (pck == ev.getPacket().getClass()) { // if the reaction is paired with pck's clas
                    if (reactor instanceof AbstractChildPacketReactor)
                        ((AbstractChildPacketReactor) reactor).setChild(this.child);
                    boolean flag = reactor.takeAction(ev.getPacket());
                    if (flag
                            && REborn.INSTANCE.minecraftClient != null
                            && REborn.INSTANCE.minecraftClient.getSession().isConnected()
                            && ((MinecraftProtocol) this.child.getSession().getPacketProtocol()).getSubProtocol() == SubProtocol.GAME) // perform the action
                        REborn.INSTANCE.minecraftClient.getSession().send(event.getRecievedPacket()); // send the packet to server if true
                } //ez
            });
        } catch (Exception e) {
            System.err.println("A severe error occured during a recieved child client packet's procesing!");
            e.printStackTrace();
        }
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
    }

    /**
     * Invoked when WE send a packet to a CHILD
     */
    @Override
    public void packetSent(PacketSentEvent ev) {
        ChildServerPacketSendEvent event = new ChildServerPacketSendEvent(this.child, ev.getPacket());
        REborn.INSTANCE.EVENT_BUS.invokeEvent(event);
        if (event.isCancelled()) return;
        if (event.getSendingPacket() instanceof LoginSuccessPacket) {
            LoginSuccessPacket pck = (LoginSuccessPacket) event.getSendingPacket();
            REborn.LOGGER.log("Child user " + pck.getProfile().getName() + " authenticated!");
            runWhitelist(pck.getProfile().getName(), this.child);
            ChildJoinEvent joinEvent = new ChildJoinEvent(pck.getProfile(), ev.getSession().getRemoteAddress());
            REborn.INSTANCE.EVENT_BUS.invokeEvent(joinEvent);
            if (joinEvent.isCancelled()) {
                this.child.getSession().send(new ServerDisconnectPacket(TextMessageColoured.from(joinEvent.getCancelledKickMessage())));
                this.child.getSession().disconnect(joinEvent.getCancelledKickMessage());
                return;
            }
        }
        if (event.getSendingPacket() instanceof ServerJoinGamePacket) {
            REbornClient.REbornClientCache.INSTANCE.chunkCache.forEach((hash, chunk) -> {
                this.child.getSession().send(new ServerChunkDataPacket(chunk));
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            REborn.LOGGER.log("Sent " + REbornClient.REbornClientCache.INSTANCE.chunkCache.size() + " chunks");
            this.child.getSession().send(new ServerPluginMessagePacket("MC|Brand", ServerBranding.BRAND_ENCODED));
            this.child.getSession().send(new ServerPlayerChangeHeldItemPacket(REbornClient.REbornClientCache.INSTANCE.heldItem));
            this.child.getSession().send(new ServerPlayerPositionRotationPacket(REbornClient.REbornClientCache.INSTANCE.posX, REbornClient.REbornClientCache.INSTANCE.posY, REbornClient.REbornClientCache.INSTANCE.posZ, REbornClient.REbornClientCache.INSTANCE.yaw, REbornClient.REbornClientCache.INSTANCE.pitch, new Random().nextInt(1000) + 10));
            this.child.getSession().send(new ServerWindowItemsPacket(0, REbornClient.REbornClientCache.INSTANCE.playerInventory));
            REbornClient.REbornClientCache.INSTANCE.playerListEntries.stream()
                    .filter(entry -> entry.getProfile() != null)
                    .forEach(entry -> {
                                try {
                                    Field field = PlayerListEntry.class.getDeclaredField("displayName");
                                    field.setAccessible(true);
                                    field.set(entry, TextMessageColoured.from(entry.getProfile().getName()));
                                    if (entry.getProfile().getName() == null) {
                                        Field f = GameProfile.class.getDeclaredField("name");
                                        f.setAccessible(true);
                                        f.set(entry.getProfile(), "???");
                                    }
                                    this.child.getSession().send(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{entry}));
                                } catch (IllegalAccessException | NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
            //this.child.getSession().send(ReClient.ReClientCache.INSTANCE.playerInventory);
            this.child.getSession().send(new ServerPlayerListDataPacket(REbornClient.REbornClientCache.INSTANCE.tabHeader, REbornClient.REbornClientCache.INSTANCE.tabFooter));
            this.child.getSession().send(new ServerPlayerHealthPacket(REbornClient.REbornClientCache.INSTANCE.health, REbornClient.REbornClientCache.INSTANCE.food, REbornClient.REbornClientCache.INSTANCE.saturation));
            try {
                for (Entity entity : REbornClient.REbornClientCache.INSTANCE.entityCache.values()) {
                    if (entity == null) continue;
                    if (entity.type == EntityType.MOB && entity instanceof EntityMob) {
                        EntityMob mob = (EntityMob) entity;
                        this.child.getSession().send(
                                new ServerSpawnMobPacket
                                        (mob.entityId,
                                                mob.uuid,
                                                mob.mobType,
                                                mob.posX,
                                                mob.posY,
                                                mob.posZ,
                                                mob.yaw,
                                                mob.pitch,
                                                mob.headYaw,
                                                mob.motionX,
                                                mob.motionY,
                                                mob.motionZ,
                                                mob.metadata));
                        for (PotionEffect potionEffect : mob.potionEffects) {
                            this.child.getSession().send(new ServerEntityEffectPacket(
                                    mob.entityId,
                                    potionEffect.effect,
                                    potionEffect.amplifier,
                                    potionEffect.duration,
                                    potionEffect.ambient,
                                    potionEffect.showParticles
                            ));
                        }
                        for (Map.Entry<EquipmentSlot, ItemStack> entry : mob.equipment.entrySet()) {
                            this.child.getSession().send(new ServerEntityEquipmentPacket(entity.entityId,
                                    entry.getKey(),
                                    entry.getValue()));
                        }
                        if (mob.properties.size() > 0) {
                            this.child.getSession().send(new ServerEntityPropertiesPacket(entity.entityId,
                                    mob.properties));
                        }
                        continue;
                    }
                    if (entity.type == EntityType.PLAYER && entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) entity;
                        this.child.getSession().send(new ServerSpawnPlayerPacket(
                                player.entityId,
                                player.uuid,
                                player.posX,
                                player.posY,
                                player.posZ,
                                player.yaw,
                                player.pitch,
                                player.metadata
                        ));
                        for (PotionEffect effect : player.potionEffects) {
                            this.child.getSession().send(new ServerEntityEffectPacket(player.entityId,
                                    effect.effect,
                                    effect.amplifier,
                                    effect.duration,
                                    effect.ambient,
                                    effect.showParticles));
                        }
                        for (Map.Entry<EquipmentSlot, ItemStack> entry : player.equipment.entrySet()) {
                            this.child.getSession().send(new ServerEntityEquipmentPacket(player.entityId,
                                    entry.getKey(),
                                    entry.getValue()));
                        }
                        if (player.properties.size() > 0) {
                            this.child.getSession().send(new ServerEntityPropertiesPacket(player.entityId,
                                    player.properties));
                        }
                        continue;
                    }
                    if (entity.type == EntityType.OBJECT && entity instanceof EntityObject) {
                        EntityObject object = (EntityObject) entity;
                        // hello entityobject my old friend ;-;
                        if (object.data == null) {
                            this.child.getSession().send(new ServerSpawnObjectPacket(
                                    entity.entityId,
                                    entity.uuid,
                                    object.objectType,
                                    entity.posX,
                                    entity.posY,
                                    entity.posZ,
                                    object.yaw,
                                    object.pitch,
                                    object.motionX,
                                    object.motionY,
                                    object.motionZ));
                            continue;
                        }
                        this.child.getSession().send(new ServerSpawnObjectPacket(
                                entity.entityId,
                                entity.uuid,
                                object.objectType,
                                object.data,
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                object.yaw,
                                object.pitch,
                                object.motionX,
                                object.motionY,
                                object.motionZ));
                        continue;
                    }
                    REborn.LOGGER.logDebug("???");
                }
                for (Entity entity : REbornClient.REbornClientCache.INSTANCE.entityCache.values()) {
                    if (entity.passengerIds.size() > 0) {
                        this.child.getSession().send(new ServerEntitySetPassengersPacket(entity.entityId, entity.passengersAsArray()));
                    }
                    if (entity instanceof EntityRotation) {
                        EntityRotation rotation = (EntityRotation) entity;
                        if (rotation.isLeashed) {
                            this.child.getSession().send(new ServerEntityAttachPacket(entity.entityId,
                                    rotation.leashedID));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.child.getSession().send(new ServerUnlockRecipesPacket(REbornClient.REbornClientCache.INSTANCE.wasRecipeBookOpened, REbornClient.REbornClientCache.INSTANCE.wasFilteringRecipes, REbornClient.REbornClientCache.INSTANCE.recipeCache, UnlockRecipesAction.ADD));
            this.child.setPlaying(true);
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        event.getCause().printStackTrace();
        ChildDisconnectEvent event1 = new ChildDisconnectEvent(event.getSession().getRemoteAddress());
        REborn.LOGGER.log("Child disconnected due to " + event.getReason());
    }

    public static void runWhitelist(String name, Child child) {
        boolean flag = REborn.INSTANCE.MAIN_CONFIG.useWhitelist && !REborn.INSTANCE.MAIN_CONFIG.whitelist
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList())
                .contains(name.toLowerCase());
        if (flag) {
            REborn.LOGGER.logWarning(name + " isn't whitelisted.");
            SubProtocol proto = ((MinecraftProtocol) child.getSession().getPacketProtocol()).getSubProtocol();
            switch (proto) {
                case LOGIN:
                    child.getSession().send(new LoginDisconnectPacket(TextMessageColoured.from("&cYou are not whitelisted on this server!\nIf you believe that this is an error, please contact the server administrator")));
                    break;
                case GAME:
                    child.getSession().send(new ServerDisconnectPacket(TextMessageColoured.from("&cYou are not whitelisted on this server!\nIf you believe that this is an error, please contact the server administrator")));
                    break;
            }
            child.getSession().disconnect("Not whitelisted!");
            return;
        }
    }
}

class ServerBranding {
    private static final String BRAND = "RE_Minecraft " + REborn.VERSION;
    protected static byte[] BRAND_ENCODED;

    static {
        ByteBuf buf = Unpooled.buffer(5 + BRAND.length());
        try {
            writeUTF8(buf, BRAND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BRAND_ENCODED = buf.array();
    }

    private static void writeUTF8(ByteBuf buf, String value) throws IOException {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= Short.MAX_VALUE) {
            throw new IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuf!");
        }
        // Write the string's length
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    private static void writeVarInt(ByteBuf buf, int value) {
        byte part;
        while (true) {
            part = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            buf.writeByte(part);
            if (value == 0) {
                break;
            }
        }
    }


}
