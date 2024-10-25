package de.dervonnebe.aps.commands.environment;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ThunderCommand implements CommandExecutor {
    APSurvival plugin;
    Messages msg;

    public ThunderCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (player.hasPermission("aps.command.thunder") || player.hasPermission("aps.command.storm")) {
            player.getWorld().setThundering(true);
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.thunder"));
            return true;
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.thunder / aps.command.storm"));
        }

        return true;
    }
}
