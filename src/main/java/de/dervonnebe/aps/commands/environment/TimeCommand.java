package de.dervonnebe.aps.commands.environment;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TimeCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public TimeCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("aps.command.time")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.time"));
            return true;
        }

        if (args.length == 0) {
            sendCurrentTime(player);
            return true;
        }

        String timeOption = args[0].toLowerCase();
        switch (timeOption) {
            case "day":
                setTimeIfPermitted(player, 1000, "aps.command.time.day", "command.environment.day");
                break;
            case "night":
                setTimeIfPermitted(player, 13000, "aps.command.time.night", "command.environment.night");
                break;
            case "sunset":
                setTimeIfPermitted(player, 13000, "aps.command.time.sunset", "command.environment.sunset");
                break;
            case "sunrise":
                setTimeIfPermitted(player, 0, "aps.command.time.sunrise", "command.environment.sunrise");
                break;
            default:
                setCustomTime(player, timeOption);
                break;
        }
        return true;
    }

    private void setTimeIfPermitted(Player player, long time, String perm, String messageKey) {
        if (player.hasPermission(perm)) {
            player.getWorld().setTime(time);
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, messageKey));
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", perm));
        }
    }

    private void setCustomTime(Player player, String timeArg) {
        if (!player.hasPermission("aps.command.time.set")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.time.set"));
            return;
        }
        try {
            long time = Long.parseLong(timeArg);
            player.getWorld().setTime(time);
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.environment.time.set").replace("%time%", String.valueOf(time)));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/time <day|night|sunset|sunrise|<time>>"));
        }
    }

    private void sendCurrentTime(Player player) {
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.environment.time.current").replace("%time%", String.valueOf(player.getWorld().getTime())));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("day");
            completions.add("night");
            completions.add("sunset");
            completions.add("sunrise");
        }

        return completions;
    }
}
