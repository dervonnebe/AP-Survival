package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LayCommand implements CommandExecutor {
    private final APSurvival plugin;

    public LayCommand(APSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getMessages().getMessage("command.only-players"));
            return true;
        }

        if (!plugin.getConfigManager().getBoolean("sit-and-lay.enabled") || 
            !plugin.getConfigManager().getBoolean("sit-and-lay.lay.enabled")) {
            return true;
        }

        if (plugin.getConfigManager().getBoolean("sit-and-lay.require-permission") && 
            !player.hasPermission("aps.lay")) {
            player.sendMessage(plugin.getPrefix() + plugin.getMessages().getMessage("no-perm")
                    .replace("%perm%", "aps.lay"));
            return true;
        }

        if (!plugin.getDataManager().getBooleanData(player, "sitlay.lay.enabled", true)) {
            return true;
        }

        plugin.getSitLaySystem().attemptLay(player);
        return true;
    }
} 