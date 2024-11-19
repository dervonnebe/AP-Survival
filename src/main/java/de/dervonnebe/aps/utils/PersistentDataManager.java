package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataManager {
    private final APSurvival plugin;

    public PersistentDataManager(APSurvival plugin) {
        this.plugin = plugin;
    }

    public void setStringData(Player player, String key, String value) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        data.set(namespacedKey, PersistentDataType.STRING, value);
    }

    public String getStringData(Player player, String key) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return data.get(namespacedKey, PersistentDataType.STRING);
    }

    public void setBooleanData(Player player, String key, boolean value) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        data.set(namespacedKey, PersistentDataType.BYTE, value ? (byte) 1 : (byte) 0);
    }

    public boolean getBooleanData(Player player, String key, boolean defaultValue) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        Byte value = data.get(namespacedKey, PersistentDataType.BYTE);
        return value != null ? value == 1 : defaultValue;
    }
}