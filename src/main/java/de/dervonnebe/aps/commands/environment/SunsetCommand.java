package de.dervonnebe.aps.commands.environment;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SunsetCommand implements CommandExecutor {
    APSurvival plugin;
    Messages msg;

    public SunsetCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (player.hasPermission("aps.command.sunset")) {
            player.getWorld().setTime(13000);
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.environment.sunset"));
            return true;
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.sunset"));
        }

        return true;
    }
}
