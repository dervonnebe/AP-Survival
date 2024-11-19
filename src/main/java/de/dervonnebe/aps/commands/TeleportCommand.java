package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeleportCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public TeleportCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("aps.command.teleport")) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm")
                    .replace("%perm%", "aps.command.teleport"));
            return true;
        }

        // /tp <spieler>
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
                return true;
            }
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);
            
            if (target == null) {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found")
                        .replace("%player%", args[0]));
                return true;
            }

            player.teleport(target.getLocation());
            return true;
        }

        // /tp <spieler> <zielspieler>
        if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[0]);
            Player target = Bukkit.getPlayer(args[1]);

            if (player == null) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found")
                        .replace("%player%", args[0]));
                return true;
            }

            if (target == null) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found")
                        .replace("%player%", args[1]));
                return true;
            }

            player.teleport(target.getLocation());
            return true;
        }

        // /tp <spieler> <x> <y> <z>
        if (args.length == 4) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
                return true;
            }
            Player player = (Player) sender;
            
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                
                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-int")
                        .replace("%int%", String.join(" ", args[1], args[2], args[3])));
                return true;
            }
        }

        // /tp <spieler> <x> <y> <z>
        if (args.length == 5) {
            Player player = Bukkit.getPlayer(args[0]);
            
            if (player == null) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found")
                        .replace("%player%", args[0]));
                return true;
            }
            
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                
                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-int")
                        .replace("%int%", String.join(" ", args[1], args[2], args[3])));
                return true;
            }
        }

        sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid")
                .replace("%command%", "/tp <spieler> [zielspieler|x y z]"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("aps.command.teleport")) {
            return completions;
        }

        if (args.length == 1 || args.length == 2) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
