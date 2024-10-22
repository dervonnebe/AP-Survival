package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messages {
    private String defaultLanguage = "en";
    private String language;
    private APSurvival plugin;

    public Messages(APSurvival plugin, String... lang) {
        if (lang.length > 0) {
            this.language = lang[0];
        } else {
            if (plugin.getConfigManager().getString("lang") != null) {
                this.language = plugin.getConfigManager().getString("lang");
            } else {
                this.language = defaultLanguage;
            }
        }
        this.plugin = plugin;
    }

    public String getPlayerMessage(Player player, String key) {
        String pLang = plugin.getDataManager().getStringData(player, "language");

        if (pLang == null) {
            plugin.getDataManager().setStringData(player, "language", language);
            pLang = language;
        }

        return getMessage(key, pLang);
    }

    private String getMessage(String key, String lang) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

        if (!langFile.exists()) {
            plugin.log("Language file not found: " + langFile.getName(), "WARNING");
            langConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/" + defaultLanguage + ".yml"));
        }

        String message = langConfig.getString(key);

        if (message == null) {
            plugin.log("Key not found: " + key, "WARNING");
            return "§4Message not Found §8(§7" + key + "§8)";
        }

        return message;
    }

    public String getMessages(String key) {
        return getMessage(key, language);
    }
}
