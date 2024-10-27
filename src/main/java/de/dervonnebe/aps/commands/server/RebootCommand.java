package de.dervonnebe.aps.commands.server;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RebootCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;
    private final int maxTime = 600; // in seconds (10 min)
    private final int minTime = 0;   // in seconds (0 sec)

    public RebootCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int time = parseTimeArgument(sender, args);
        if (time == -1) time = 0;

        if (!isValidTime(sender, time)) return true;

        rebootServer(time);
        return true;
    }

    private int parseTimeArgument(CommandSender sender, String[] args) {
        if (args.length == 1) {
            try {
                int time = Integer.parseInt(args[0]);
                if (time >= minTime) {
                    return time;
                }
            } catch (NumberFormatException ignored) {
            }
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage((Player) sender, "command.reboot.invalid-time"));
            } else {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.reboot.invalid-time"));
            }
        }
        return -1;
    }

    private boolean isValidTime(CommandSender sender, int time) {
        if (time < minTime || time > maxTime) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage((Player) sender, "command.reboot.time-out-of-range")
                        .replace("%min-time%", String.valueOf(minTime))
                        .replace("%max-time%", String.valueOf(maxTime)));
            } else {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.reboot.time-out-of-range")
                        .replace("%min-time%", String.valueOf(minTime))
                        .replace("%max-time%", String.valueOf(maxTime)));
            }
            return false;
        }
        return true;
    }

    private void rebootServer(int time) {
        if (time == 0) {
            initiateImmediateReboot();
        } else {
            scheduleReboot(time);
        }
    }

    private void initiateImmediateReboot() {
        msg.broadcastIndividualMessage("command.server.reboot.now", new String[][] {
                {"%time%", "%time% is not nessesary for this message to be displayed"},
                {"%test%", "§a§lThis is an Secret §k§4HACKER§7"}
        });
        Bukkit.getOnlinePlayers().forEach(player ->
                player.kickPlayer(msg.getPlayerMessage(player, "command.server.reboot.kick")));
        Bukkit.shutdown();
    }

    private void scheduleReboot(int time) {
        msg.broadcastIndividualMessage("command.server.reboot.timer", new String[][] {
                {"%time%", String.valueOf(time)},
                {"%test%", "§a§lThis is an Secret §k§4HACKER§7"}
        });

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                Bukkit.getOnlinePlayers().forEach(player ->
                        player.kickPlayer(msg.getPlayerMessage(player, "command.server.reboot.kick"))), time * 20L);

        Bukkit.getScheduler().runTaskLater(plugin, Bukkit::shutdown, time * 20L);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return args.length == 1 && sender.hasPermission("aps.command.reboot") ?
                Arrays.asList("10", "30", "60", "300") : Collections.emptyList();
    }
}
