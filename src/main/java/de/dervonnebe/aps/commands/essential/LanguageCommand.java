package de.dervonnebe.aps.commands.essential;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import de.dervonnebe.aps.utils.PersistentDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LanguageCommand implements CommandExecutor, TabCompleter {

    APSurvival plugin;
    Messages msg;
    PersistentDataManager dataManager;

    public LanguageCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-player"));
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("aps.command.language")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.language"));
            return true;
        }

        if (strings.length == 0) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.language.current").replace("%language%", dataManager.getStringData(player, "language")));
            return true;
        }

        if (strings.length == 1) {
            String lang = strings[0].toLowerCase();
            if (lang.equals("de") || lang.equals("en")) { //TODO: Remove hardcoded languages
                dataManager.setStringData(player, "language", lang);
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.language.changed").replace("%language%", lang));
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.language.invalid"));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("aps.command.language")) {
            if (strings.length == 1) {
                return List.of("de", "en"); //TODO: Remove hardcoded languages
            }
        }
        return null;
    }
}
