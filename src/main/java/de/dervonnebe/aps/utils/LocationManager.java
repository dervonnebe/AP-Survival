package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationManager {
    private final APSurvival plugin;
    private final File locationFile;
    private FileConfiguration locationConfig;
    private final Map<String, Location> locationCache;

    public LocationManager(APSurvival plugin) {
        this.plugin = plugin;
        this.locationFile = new File(plugin.getDataFolder(), "locations.yml");
        this.locationCache = new HashMap<>();
        loadLocations();
    }

    private void loadLocations() {
        if (!locationFile.exists()) {
            plugin.saveResource("locations.yml", false);
        }
        locationConfig = YamlConfiguration.loadConfiguration(locationFile);
        
        // Cache alle gespeicherten Locations
        for (String key : locationConfig.getKeys(false)) {
            locationCache.put(key, getLocationFromConfig(key));
        }
    }

    private Location getLocationFromConfig(String name) {
        String path = name + ".";
        String worldName = locationConfig.getString(path + "world");
        if (worldName == null) return null;
        
        return new Location(
            Bukkit.getWorld(worldName),
            locationConfig.getDouble(path + "x"),
            locationConfig.getDouble(path + "y"),
            locationConfig.getDouble(path + "z"),
            (float) locationConfig.getDouble(path + "yaw"),
            (float) locationConfig.getDouble(path + "pitch")
        );
    }

    public void saveLocation(String name, Location location) {
        String path = name + ".";
        locationConfig.set(path + "world", location.getWorld().getName());
        locationConfig.set(path + "x", location.getX());
        locationConfig.set(path + "y", location.getY());
        locationConfig.set(path + "z", location.getZ());
        locationConfig.set(path + "yaw", location.getYaw());
        locationConfig.set(path + "pitch", location.getPitch());

        try {
            locationConfig.save(locationFile);
            locationCache.put(name, location);
        } catch (IOException e) {
            plugin.log("Error saving location " + name + ": " + e.getMessage(), "ERROR");
        }
    }

    public Location getLocation(String name) {
        return locationCache.getOrDefault(name, null);
    }

    public void removeLocation(String name) {
        locationConfig.set(name, null);
        try {
            locationConfig.save(locationFile);
            locationCache.remove(name);
        } catch (IOException e) {
            plugin.log("Error removing location " + name + ": " + e.getMessage(), "ERROR");
        }
    }

    public boolean exists(String name) {
        return locationCache.containsKey(name);
    }

    public Set<String> getWarps() {
        return locationCache.keySet().stream()
            .filter(key -> key.startsWith("warp."))
            .map(key -> key.substring(5)) // Entferne "warp." Präfix
            .collect(Collectors.toSet());
    }

    public void reloadLocations() {
        locationCache.clear();
        loadLocations();
    }
} 