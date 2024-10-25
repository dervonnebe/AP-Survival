package de.dervonnebe.aps.commands.environment;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimeCommand implements CommandExecutor {
    APSurvival plugin;
    Messages msg;

    public TimeCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (player.hasPermission("aps.command.time")) {
            if (strings.length == 1) {

                strings[0] = strings[0].toLowerCase();

                switch (strings[0]) {
                    case "day":
                        if (player.hasPermission("aps.command.time.day")) {
                            player.getWorld().setTime(1000);
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.day"));
                        } else {
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.time.day"));
                        }
                        return true;
                    case "night":
                        if (player.hasPermission("aps.command.time.night")) {
                            player.getWorld().setTime(13000);
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.night"));
                        } else {
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.time.night"));
                        }
                        return true;
                    case "sunset":
                        if (player.hasPermission("aps.command.time.sunset")) {
                            player.getWorld().setTime(13000);
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.sunset"));
                        } else {
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.time.sunset"));
                        }
                        return true;
                    case "sunrise":
                        if (player.hasPermission("aps.command.time.sunrise")) {
                            player.getWorld().setTime(0);
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.sunrise"));
                        } else {
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.time.sunrise"));
                        }
                        return true;
                    default:
                        if (!player.hasPermission("aps.command.time.set")) {
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.time.set"));
                            return true;
                        }
                        try {
                            long time = Long.parseLong(strings[0]);
                            player.getWorld().setTime(time);
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.time.set").replace("%time%", String.valueOf(time)));
                        } catch (NumberFormatException e) {
                            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid").replace("%command%", "/time <day|night|sunset|sunrise|<time>>"));
                        }
                }
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.time.current").replace("%time%", String.valueOf(player.getWorld().getTime())));
                return true;
            }
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.time"));
        }
        return true;
    }
}
