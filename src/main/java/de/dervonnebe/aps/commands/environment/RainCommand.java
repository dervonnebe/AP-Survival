package de.dervonnebe.aps.commands.environment;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RainCommand implements CommandExecutor {
    APSurvival plugin;
    Messages msg;

    public RainCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (player.hasPermission("aps.command.rain")) {
            player.getWorld().setStorm(true);
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.rain"));
            return true;
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.rain"));
        }

        return true;
    }
}
