package dev.dewy.reborn.api;

import dev.dewy.reborn.REborn;

public abstract class REbornPlugin {

    public String pluginName;
    public String pluginDescription;
    public String[] pluginAuthors;
    public String pluginVersion;

    /**
     * Called before the client is connected to the server
     */
    public abstract void onPluginInit();

    /**
     * Called when the client connects to the server
     */
    public abstract void onPluginEnable();

    /**
     * Called when the client is relaunching
     */
    public abstract void onPluginDisable();

    /**
     * Called when the client is completely shutting down
     */
    public abstract void onPluginShutdown();

    /**
     * Called when the client registers it's stock commands to it's command processor's
     */
    public abstract void registerCommands();

    /**
     * Called when the client fills it's default configuration values
     */
    public abstract void registerConfig();

    public REborn getREborn() {
        return REborn.INSTANCE;
    }
}
