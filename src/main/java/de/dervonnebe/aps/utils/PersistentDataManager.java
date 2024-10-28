package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.HashSet;
import java.util.Set;

public class PersistentDataManager {
    private final APSurvival plugin;

    public PersistentDataManager(APSurvival plugin) {
        this.plugin = plugin;
    }

    public void setStringData(Player player, String key, String value) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        dataContainer.set(namespacedKey, PersistentDataType.STRING, value);
    }

    public void addStringData(Player player, String key, String value) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        String oldValue = dataContainer.get(namespacedKey, PersistentDataType.STRING);
        if (oldValue == null) {
            oldValue = "";
        }
        dataContainer.set(namespacedKey, PersistentDataType.STRING, oldValue + value);
    }

    public String getStringData(Player player, String key) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        return dataContainer.get(namespacedKey, PersistentDataType.STRING);
    }

    public void setIntData(Player player, String key, int value) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        dataContainer.set(namespacedKey, PersistentDataType.INTEGER, value);
    }

    public void addIntData(Player player, String key, int value) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        int oldValue = dataContainer.getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0);
        dataContainer.set(namespacedKey, PersistentDataType.INTEGER, oldValue + value);
    }

    public int getIntData(Player player, String key) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        return dataContainer.getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0);
    }

    public void setBooleanData(Player player, String key, boolean value) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        dataContainer.set(namespacedKey, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public boolean getBooleanData(Player player, String key) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = getKey(key);
        Byte value = dataContainer.get(namespacedKey, PersistentDataType.BYTE);
        return value != null && value == 1;
    }

    private NamespacedKey getKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public void setLocation(Player player, String key, Location location) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, key + "_world"), PersistentDataType.STRING, location.getWorld().getName());
        dataContainer.set(new NamespacedKey(plugin, key + "_x"), PersistentDataType.DOUBLE, location.getX());
        dataContainer.set(new NamespacedKey(plugin, key + "_y"), PersistentDataType.DOUBLE, location.getY());
        dataContainer.set(new NamespacedKey(plugin, key + "_z"), PersistentDataType.DOUBLE, location.getZ());
        dataContainer.set(new NamespacedKey(plugin, key + "_yaw"), PersistentDataType.FLOAT, location.getYaw());
        dataContainer.set(new NamespacedKey(plugin, key + "_pitch"), PersistentDataType.FLOAT, location.getPitch());
    }

    public Location getLocation(Player player, String key) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        String worldName = dataContainer.get(new NamespacedKey(plugin, key + "_world"), PersistentDataType.STRING);
        Double x = dataContainer.get(new NamespacedKey(plugin, key + "_x"), PersistentDataType.DOUBLE);
        Double y = dataContainer.get(new NamespacedKey(plugin, key + "_y"), PersistentDataType.DOUBLE);
        Double z = dataContainer.get(new NamespacedKey(plugin, key + "_z"), PersistentDataType.DOUBLE);
        Float yaw = dataContainer.get(new NamespacedKey(plugin, key + "_yaw"), PersistentDataType.FLOAT);
        Float pitch = dataContainer.get(new NamespacedKey(plugin, key + "_pitch"), PersistentDataType.FLOAT);

        if (worldName == null || x == null || y == null || z == null || yaw == null || pitch == null) {
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void removeLocation(Player player, String key) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.remove(new NamespacedKey(plugin, key + "_world"));
        dataContainer.remove(new NamespacedKey(plugin, key + "_x"));
        dataContainer.remove(new NamespacedKey(plugin, key + "_y"));
        dataContainer.remove(new NamespacedKey(plugin, key + "_z"));
        dataContainer.remove(new NamespacedKey(plugin, key + "_yaw"));
        dataContainer.remove(new NamespacedKey(plugin, key + "_pitch"));
    }
}
