package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

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
}
