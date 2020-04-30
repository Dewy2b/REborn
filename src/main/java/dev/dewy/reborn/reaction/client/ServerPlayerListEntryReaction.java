package dev.dewy.reborn.reaction.client;

import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.event.ServerOtherPlayerJoinEvent;
import dev.dewy.reborn.api.event.ServerOtherPlayerQuitEvent;
import dev.dewy.reborn.client.REbornClient;
import dev.dewy.reborn.reaction.IPacketReactor;

import java.lang.reflect.Field;
import java.util.*;

public class ServerPlayerListEntryReaction implements IPacketReactor<ServerPlayerListEntryPacket> {
    @Override
    public boolean takeAction(ServerPlayerListEntryPacket packet) {
        switch (packet.getAction()) {
            case ADD_PLAYER:
                Arrays.stream(packet.getEntries())
                        .filter(e -> !REbornClient.REbornClientCache.INSTANCE.playerListEntries.contains(e))
                        .forEach(entry -> {
                            REbornClient.REbornClientCache.INSTANCE.playerListEntries.add(entry);
                            ServerOtherPlayerJoinEvent event = new ServerOtherPlayerJoinEvent(entry.getProfile());
                            REborn.INSTANCE.EVENT_BUS.invokeEvent(event);
                        });
                break;
            case REMOVE_PLAYER:
                List<String> toRemove = new ArrayList<>();
                List<Integer> removalIndexes = new ArrayList<>();
                Arrays.stream(packet.getEntries()).forEach(entry -> toRemove.add(entry.getProfile().getId().toString()));
                REbornClient.REbornClientCache.INSTANCE.playerListEntries.forEach(entry -> {
                    if (toRemove.contains(entry.getProfile().getId().toString())) {
                        removalIndexes.add(REbornClient.REbornClientCache.INSTANCE.playerListEntries.indexOf(entry));
                    }
                });
                removalIndexes.forEach(index -> {
                    ServerOtherPlayerQuitEvent event = new ServerOtherPlayerQuitEvent(REbornClient.REbornClientCache.INSTANCE.playerListEntries.get(index).getProfile());
                    REborn.INSTANCE.EVENT_BUS.invokeEvent(event);
                    REbornClient.REbornClientCache.INSTANCE.playerListEntries.remove((int) index);
                });
                break;
            case UPDATE_DISPLAY_NAME:
                LinkedHashMap<UUID, Message> changeMap = new LinkedHashMap<>();
                for (PlayerListEntry entry : packet.getEntries()) {
                    changeMap.put(entry.getProfile().getId(), entry.getDisplayName());
                }
                changeMap.forEach((id, msg) -> {
                    for (PlayerListEntry playerListEntry : REbornClient.REbornClientCache.INSTANCE.playerListEntries) {
                        if (playerListEntry.getProfile().getId().equals(id)) {
                            try {
                                Field field = playerListEntry.getClass().getDeclaredField("displayName");
                                field.setAccessible(true);
                                field.set(playerListEntry, msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            case UPDATE_LATENCY:
                LinkedHashMap<UUID, Integer> pingMap = new LinkedHashMap<>();
                for (PlayerListEntry entry : packet.getEntries()) {
                    pingMap.put(entry.getProfile().getId(), entry.getPing());
                }
                pingMap.forEach((id, ping) -> {
                    for (PlayerListEntry playerListEntry : REbornClient.REbornClientCache.INSTANCE.playerListEntries) {
                        if (playerListEntry.getProfile().getId().equals(id)) {
                            try {
                                Field field = playerListEntry.getClass().getDeclaredField("ping");
                                field.setAccessible(true);
                                field.set(playerListEntry, ping);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            case UPDATE_GAMEMODE:
                LinkedHashMap<UUID, GameMode> gamemodeMap = new LinkedHashMap<>();
                for (PlayerListEntry entry : packet.getEntries()) {
                    gamemodeMap.put(entry.getProfile().getId(), entry.getGameMode());
                }
                gamemodeMap.forEach((id, gm) -> {
                    for (PlayerListEntry playerListEntry : REbornClient.REbornClientCache.INSTANCE.playerListEntries) {
                        if (playerListEntry.getProfile().getId().equals(id)) {
                            try {
                                Field field = playerListEntry.getClass().getDeclaredField("gameMode");
                                field.setAccessible(true);
                                field.set(playerListEntry, gm);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            default:
                REborn.LOGGER.logError("Unsupported tablist action!");

        }
        return true;
    }
}
