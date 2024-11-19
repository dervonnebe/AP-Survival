package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SitCommand implements CommandExecutor {
    private final APSurvival plugin;

    public SitCommand(APSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessages().getMessage("command.only-players"));
            return true;
        }

        if (!plugin.getConfigManager().getBoolean("sit-and-lay.enabled") || 
            !plugin.getConfigManager().getBoolean("sit-and-lay.sit.enabled")) {
            return true;
        }

        if (plugin.getConfigManager().getBoolean("sit-and-lay.require-permission") && 
            !player.hasPermission("aps.sit")) {
            player.sendMessage(plugin.getPrefix() + plugin.getMessages().getMessage("no-perm")
                    .replace("%perm%", "aps.sit"));
            return true;
        }

        if (!plugin.getDataManager().getBooleanData(player, "sitlay.sit.enabled", true)) {
            return true;
        }

        plugin.getSitLaySystem().attemptSit(player);
        return true;
    }
} 