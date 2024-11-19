package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LanguageManager {
    private final APSurvival plugin;
    private List<String> languages;

    public LanguageManager(APSurvival plugin, Boolean reset, String[] languageFiles) {
        this.plugin = plugin;
        this.languages = new ArrayList<>();
        for (String lang : languageFiles) {
            languages.add(lang);
        }
        
        if (languages.isEmpty()) {
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

    public void reloadLanguages() {
        deleteServerLangFiles();
        ensureLanguagesExist();
    }
}
