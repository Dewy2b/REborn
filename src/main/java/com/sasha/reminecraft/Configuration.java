package com.sasha.reminecraft;

import com.sasha.reminecraft.api.exception.ReMinecraftPluginConfigurationException;
import com.sasha.reminecraft.util.YML;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds all of the configuration values
 */
public class Configuration {
    private String configName;

    /**
     * The global configuration vars
     */
    @ConfigSetting
    public String sessionId = null;

    @ConfigSetting
    public String clientId = UUID.randomUUID().toString();

    @ConfigSetting
    public String email = null;

    @ConfigSetting
    public String password = null;

    @ConfigSetting
    public boolean cracked = false;

    @ConfigSetting
    public String serverIp = "constantiam.net";

    @ConfigSetting
    public String rebornServerIp = "0.0.0.0";

    @ConfigSetting
    public int serverPort = 25565;

    @ConfigSetting
    public int rebornServerPort = 25565;

    @ConfigSetting
    public boolean onlineMode = true;

    @ConfigSetting
    public int pingTimeout = 30;

    @ConfigSetting
    public boolean useWhitelist = false;

    @ConfigSetting
    public ArrayList<String> whitelist = new ArrayList<>();

    @ConfigSetting
    public String motd = "&dRE:Minecraft &7" + ReMinecraft.VERSION;

    @ConfigSetting
    public int reconnectDelay = 5;

    @ConfigSetting
    public boolean authNoProxy = true;

    @ConfigSetting
    public String socksProxy = null;

    @ConfigSetting
    public int socksPort = -1;

    {
        whitelist.add("iBuyMountainDew");
        whitelist.add("shumbo");
        whitelist.add("yandev");
        whitelist.add("nouwuwshereugh");
    }

    public Configuration(String configName) {
        this.configName = configName;
    }

    /**
     * Fill the above fields and version the config.
     */
    protected final void configure() {
        try {
            File file = getDataFile(configName);
            YML yml = new YML(file);
            for (Field declaredField : this.getClass().getDeclaredFields()) {
                if (declaredField.getAnnotation(ConfigSetting.class) == null) continue;
                declaredField.setAccessible(true);
                if (!yml.exists("config-version")) {
                    yml.set("config-version", 0);
                }
                String target = declaredField.getName();
                if (!yml.exists(target)) {
                    yml.set(target, declaredField.get(this) == null ? "[no default]" : declaredField.get(this));
                    declaredField.set(this, declaredField.get(this) == null ? "[no default]" : declaredField.get(this));
                    ReMinecraft.LOGGER.log("Created " + target);
                    continue;
                }
                if (declaredField.getType() == float.class) {
                    declaredField.set(this, yml.getFloat(target));
                } else {
                    declaredField.set(this, yml.get(target));
                }
                ReMinecraft.LOGGER.logDebug("Set " + target);
            }
            yml.save();
        } catch (IllegalAccessException ex) {
            ReMinecraftPluginConfigurationException exc = new ReMinecraftPluginConfigurationException("Configuration error while reading " + this.getClass().getSimpleName());
            exc.setStackTrace(ex.getStackTrace());
            throw exc;
        }
    }

    protected final void save() {
        try {
            File file = getDataFile(configName);
            YML yml = new YML(file);
            for (Field declaredField : this.getClass().getDeclaredFields()) {
                if (declaredField.getAnnotation(ConfigSetting.class) == null) continue;
                declaredField.setAccessible(true);
                if (!yml.exists("config-version")) {
                    yml.set("config-version", 0);
                }
                String target = declaredField.getName();
                yml.set(target, declaredField.get(this));
                ReMinecraft.LOGGER.logDebug("Saved " + target);
            }
            yml.save();
        } catch (IllegalAccessException ex) {
            ReMinecraftPluginConfigurationException exc = new ReMinecraftPluginConfigurationException("Configuration error while writing " + this.getClass().getSimpleName());
            exc.setStackTrace(ex.getStackTrace());
            throw exc;
        }
    }

    public String getConfigName() {
        return configName;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ConfigSetting {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Removed {

    }

    public static File getDataFile(String s) {
        File file = new File(s + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
