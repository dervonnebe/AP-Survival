package de.dervonnebe.aps.commands.essential;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;
    private final List<String> availableLanguages;

    public LanguageCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.availableLanguages = loadAvailableLanguages();
    }

    private List<String> loadAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        File langFolder = new File(plugin.getDataFolder(), "lang");
        
        if (langFolder.exists() && langFolder.isDirectory()) {
            File[] langFiles = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (langFiles != null) {
                for (File file : langFiles) {
                    // Entferne die .yml Endung um den Sprachcode zu erhalten
                    String langCode = file.getName().replace(".yml", "");
                    languages.add(langCode);
                }
            }
        }
        
        return languages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        if (args.length == 0) {
            String currentLang = plugin.getDataManager().getStringData(player, "language");
            if (currentLang == null) currentLang = plugin.getConfig().getString("lang");
            
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.language.current")
                    .replace("%language%", currentLang));
            return true;
        }

        String requestedLang = args[0].toLowerCase();
        if (!availableLanguages.contains(requestedLang)) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.language.invalid")
                    .replace("%language%", requestedLang));
            return true;
        }

        plugin.getDataManager().setStringData(player, "language", requestedLang);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.language.changed")
                .replace("%language%", requestedLang));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return availableLanguages.stream()
                    .filter(lang -> lang.startsWith(input))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
} 