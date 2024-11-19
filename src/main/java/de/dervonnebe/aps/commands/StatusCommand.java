package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatusCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;
    private final Set<String> usedStatuses;

    public StatusCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.usedStatuses = new HashSet<>();
        usedStatuses.addAll(plugin.getConfigManager().getStringList("status.defaults"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        if (args.length == 0) {
            String currentStatus = plugin.getDataManager().getStringData(player, "status");
            if (currentStatus == null || currentStatus.isEmpty()) currentStatus = "§7Keiner";
            
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.status.current")
                    .replace("%status%", currentStatus));
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            plugin.getDataManager().setStringData(player, "status", "");
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.status.reset"));
            plugin.getStatusManager().updatePlayerListName(player);
            return true;
        }

        String status = String.join(" ", args);
        status = ChatColor.translateAlternateColorCodes('&', status);
        
        plugin.getDataManager().setStringData(player, "status", status);
        usedStatuses.add(ChatColor.stripColor(status));
        
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.status.set")
                .replace("%status%", status));
        plugin.getStatusManager().updatePlayerListName(player);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("reset");
            completions.addAll(plugin.getConfigManager().getStringList("status.defaults").stream()
                    .map(status -> status.replaceAll("§[0-9a-fk-or]", ""))
                    .collect(Collectors.toList()));
        }
        
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
} 