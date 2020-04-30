package dev.dewy.reborn.command.terminal;

import dev.dewy.reborn.REborn;
import dev.dewy.reborn.api.REbornPluginLoader;
import com.sasha.simplecmdsys.SimpleCommand;

import java.io.IOException;

public class ReloadCommand extends SimpleCommand {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void onCommand() {
        REborn.LOGGER.logWarning("Reloading all plugins. This can cause plugins to break, or cause RE:Minecraf to break.");
        REborn.LOGGER.logWarning("Please fully relaunch REborn as soon it is convenient");
        //
        REborn.LOGGER.log("Disabling all loaded plugins...");
        REbornPluginLoader.disablePlugins();
        REborn.LOGGER.log("Shutting down all plugins...");
        REbornPluginLoader.shutdownPlugins();
        REborn.LOGGER.log("Reloading all JAR files...");
        REbornPluginLoader loader = new REbornPluginLoader();
        try {
            loader.preparePlugins(loader.findPlugins());
        } catch (IOException e) {
            REborn.LOGGER.logError("Failure loading jar files...");
            e.printStackTrace();
            return;
        }
        REborn.LOGGER.log("Initialising all loaded plugins...");
        REbornPluginLoader.initPlugins();
        REborn.LOGGER.log("Enabling all loaded plugins...");
    }
}
