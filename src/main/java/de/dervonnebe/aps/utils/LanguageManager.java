package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LanguageManager {
    private APSurvival plugin;
    private String[] languages;

    public LanguageManager(APSurvival plugin, Boolean reset, String[] languages) {
        this.plugin = plugin;
        this.languages = languages;
        if (languages.length == 0) {
            plugin.log("No languages defined in the LanguageManager", "ERROR");
            return;
        }
        if (reset) {
            deleteServerLangFiles();
        }
        ensureLanguagesExist();
    }

    private void ensureLanguagesExist() {
        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        for (String langFile : languages) {
            File fileOnServer = new File(langFolder, langFile);

            if (!fileOnServer.exists()) {
                try (InputStream in = plugin.getResource("lang/" + langFile)) {
                    if (in != null) {
                        Files.copy(in, fileOnServer.toPath());
                        plugin.log("Copying language file: " + langFile);
                    } else {
                        plugin.log("Language file not found in the plugin package: " + langFile, "ERROR");
                    }
                } catch (IOException e) {
                    plugin.log("Error copying language file " + langFile + ": " + e, "ERROR");
                }
            } else {
                plugin.log("Language file already exists: " + langFile);
            }
        }
    }

    private void deleteServerLangFiles() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        File[] files = langFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".yml")) {
                    file.delete();
                }
            }
        }
    }
}
