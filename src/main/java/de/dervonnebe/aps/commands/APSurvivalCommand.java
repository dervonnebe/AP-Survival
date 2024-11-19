package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class APSurvivalCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public APSurvivalCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("aps.command.admin")) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm").replace("%perm%", "aps.command.admin"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadPlugin(sender);
                break;
            case "version":
                showVersion(sender);
                break;
            case "config":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getPrefix() + "§cVerwendung: /aps config <reset>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("reset")) {
                    resetConfig(sender);
                }
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void reloadPlugin(CommandSender sender) {
        sender.sendMessage(plugin.getPrefix() + "§7Starte Reload-Prozess...");
        
        try {
            // Config neu laden
            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(plugin.getPrefix() + "§7- Configs neu geladen");

            // Sprachen neu laden
            plugin.getLanguageManager().reloadLanguages();
            sender.sendMessage(plugin.getPrefix() + "§7- Sprachdateien neu geladen");

            // Locations neu laden
            plugin.getLocationManager().reloadLocations();
            sender.sendMessage(plugin.getPrefix() + "§7- Locations neu geladen");

            // Server Links neu laden
            plugin.loadServerLinks();
            sender.sendMessage(plugin.getPrefix() + "§7- Server Links neu geladen");

            // Prefix aktualisieren
            plugin.updatePrefix();
            sender.sendMessage(plugin.getPrefix() + "§7- Prefix aktualisiert");

            sender.sendMessage(plugin.getPrefix() + "§aPlugin erfolgreich neu geladen!");
        } catch (Exception e) {
            sender.sendMessage(plugin.getPrefix() + "§cFehler beim Neuladen des Plugins:");
            sender.sendMessage(plugin.getPrefix() + "§c" + e.getMessage());
            plugin.log("Error while reloading plugin: " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }

    private void showVersion(CommandSender sender) {
        sender.sendMessage(plugin.getPrefix() + "§7Version: §f" + plugin.getDescription().getVersion());
        sender.sendMessage(plugin.getPrefix() + "§7Author: §f" + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(plugin.getPrefix() + "§7Website: §f" + plugin.getDescription().getWebsite());
    }

    private void resetConfig(CommandSender sender) {
        try {
            // Sichere die alte Config
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (configFile.exists()) {
                // Erstelle Backup mit Zeitstempel
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                File backupFile = new File(plugin.getDataFolder(), "config_backup_" + timestamp + ".yml");
                Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Lösche alte Config
                configFile.delete();
            }
            
            // Speichere Default-Config
            plugin.saveDefaultConfig();
            // Lade neue Config
            plugin.reloadConfig();

            if (sender instanceof Player player) {
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.aps.config.reset.success"));
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.aps.config.reset.backup")
                        .replace("%file%", "config_backup_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".yml"));
            } else {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.aps.config.reset.success"));
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.aps.config.reset.backup")
                        .replace("%file%", "config_backup_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".yml"));
            }

            // Lade Plugin neu um alle Änderungen zu übernehmen
            reloadPlugin(sender);
            
        } catch (Exception e) {
            if (sender instanceof Player player) {
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.aps.config.reset.error")
                        .replace("%error%", e.getMessage()));
            } else {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.aps.config.reset.error")
                        .replace("%error%", e.getMessage()));
            }
            plugin.log("Error while resetting config: " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getPrefix() + "§7Verfügbare Befehle:");
        sender.sendMessage(plugin.getPrefix() + "§f/aps reload §8- §7Lädt das Plugin neu");
        sender.sendMessage(plugin.getPrefix() + "§f/aps version §8- §7Zeigt die Plugin-Version");
        sender.sendMessage(plugin.getPrefix() + "§f/aps config reset §8- §7Setzt die Config auf Werkszustand zurück");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("aps.admin")) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("reload");
            completions.add("version");
            completions.add("config");
            
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        // Subcommands für "config"
        if (args.length == 2 && args[0].equalsIgnoreCase("config")) {
            completions.add("reset");
            
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return completions;
    }
}
