package net.mcoasis.mcohexroyale.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigUtil {

    private File file;
    private FileConfiguration config;

    public ConfigUtil(Plugin plugin, String name) {
        this(plugin.getDataFolder().getAbsolutePath() + "/" + name);
    }

    private ConfigUtil(String path) {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean save() {
        try {
            this.config.save(this.file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
