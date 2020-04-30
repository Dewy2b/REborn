package dev.dewy.reborn.command.game;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.REbornPluginLoader;
import dev.dewy.reborn.util.TextMessageColoured;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.util.concurrent.atomic.AtomicInteger;

@SimpleCommandInfo(description = "Return a list of loaded plugins", syntax = {""})
public class PluginsCommand extends SimpleCommand {

    public PluginsCommand() {
        super("plugins");
    }

    @Override
    public void onCommand() {
        if (REbornPluginLoader.getPluginList().isEmpty()) {
            REborn.INSTANCE.sendToChildren(new ServerChatPacket(TextMessageColoured.from("&4There are no plugins loaded.")));
            return;
        }
        StringBuilder builder = new StringBuilder("&7");
        AtomicInteger c = new AtomicInteger();
        REbornPluginLoader.getPluginList().forEach(pl -> {
            if (c.get() == 0) {
                builder.append(pl.pluginName);
                c.getAndIncrement();
                return;
            }
            builder.append(", ").append(pl.pluginName);
            c.getAndIncrement();
        });

        int i = c.get();
        REborn.INSTANCE.sendToChildren(new ServerChatPacket(TextMessageColoured.from(builder.toString())));
        REborn.INSTANCE.sendToChildren(new ServerChatPacket(TextMessageColoured.from("&e" + c.get() + " plugin" + (i == 1 ? "" : "s") + " loaded")));
    }
}
