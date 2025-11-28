package net.mcoasis.mcohexroyale.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigUtil {

    private final Plugin plugin;
    private final String fileName;
    private File file;
    private FileConfiguration config;

    public ConfigUtil(Plugin plugin, String fileName) {
        this.plugin = plugin;

        // auto-append .yml extension
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";

        createFile();
        reload();
    }

    // --- Create or copy default file ---
    private void createFile() {
        file = new File(plugin.getDataFolder(), fileName);

        // Make sure plugin folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // If file doesn’t exist, create or copy it
        if (!file.exists()) {
            saveDefault();
        }
    }

    // --- Copies /fileName from plugin resources if present ---
    public void saveDefault() {
        try {
            InputStream in = plugin.getResource(fileName); // inside jar

            if (in != null) {
                // Copy default config from inside plugin.jar
                Files.copy(in, file.toPath());
                in.close();
            } else {
                // Create an empty file
                file.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Reload config from disk ---
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    // --- Save config back to disk ---
    public boolean save() {
        try {
            config.save(file);
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
