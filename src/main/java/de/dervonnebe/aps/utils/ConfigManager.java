package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final APSurvival plugin;

    @Getter
    private FileConfiguration config;
    private final String configFileName = "config.yml";

    public ConfigManager(APSurvival plugin) {
        this.plugin = plugin;
        this.loadConfig();
        this.updateConfig();
    }

    private void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), configFileName);
        if (!configFile.exists()) {
            plugin.saveResource(configFileName, false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void updateConfig() {
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource(configFileName))));
        for (String key : newConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, newConfig.get(key));
            } else {
                config.set(key, newConfig.get(key));
            }
        }

        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(new File(plugin.getDataFolder(), configFileName));
        } catch (IOException e) {
            plugin.log("Could not save config.yml!", "ERROR");
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public void setLocation(String path, Location location) {
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
        saveConfig();
    }

    public Location getLocation(String path) {
        String worldName = config.getString(path + ".world");
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        if (worldName != null && plugin.getServer().getWorld(worldName) != null) {
            return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }
}
