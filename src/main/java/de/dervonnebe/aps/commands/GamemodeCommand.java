package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.ConfigManager;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamemodeCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final ConfigManager configManager;
    private final Messages msg;

    public GamemodeCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player targetPlayer = resolveTargetPlayer(sender, args[1]);
                if (targetPlayer != null) {
                    try {
                        GameMode newGameMode = GameMode.valueOf(args[0].toUpperCase());
                        targetPlayer.setGameMode(newGameMode);
                        sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.gamemode.change-other")
                                .replace("%target%", targetPlayer.getName())
                                .replace("%gamemode%", newGameMode.name()));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.gamemode.invalid-gamemode"));
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found"));
                }
                return true;
            } else {
                sender.sendMessage(msg.getMessage("command.only-players"));
                return true;
            }
        }

        Player player = (Player) sender;

        if (!player.hasPermission("aps.gamemode")) {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm").replace("%perm%", "aps.gamemode"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.gamemode.current-gamemode")
                    .replace("%gamemode%", player.getGameMode().name()));
            return true;
        }

        GameMode newGameMode;

        try {
            newGameMode = GameMode.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.gamemode.invalid-gamemode"));
            return false;
        }

        if (args.length == 2) {
            if (!player.hasPermission("aps.gamemode.other")) {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm").replace("%perm%", "aps.gamemode.other"));
                return true;
            }

            Player targetPlayer = resolveTargetPlayer(sender, args[1]);
            if (targetPlayer != null) {
                targetPlayer.setGameMode(newGameMode);
                targetPlayer.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(targetPlayer, "command.gamemode.change")
                        .replace("%gamemode%", newGameMode.name()));
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.gamemode.change-other")
                        .replace("%target%", targetPlayer.getName())
                        .replace("%gamemode%", newGameMode.name()));
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.player-not-found"));
            }
            return true;
        }

        player.setGameMode(newGameMode);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.gamemode.change")
                .replace("%gamemode%", newGameMode.name()));
        return true;
    }

    private Player resolveTargetPlayer(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("@a")) {
            return null;
        } else if (arg.equalsIgnoreCase("@r")) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            if (!players.isEmpty()) {
                return players.get(new Random().nextInt(players.size()));
            }
        } else if (arg.equalsIgnoreCase("@p")) {
            if (sender instanceof Player) {
                Player commandSender = (Player) sender;
                return Bukkit.getOnlinePlayers().stream()
                        .min((p1, p2) -> Double.compare(p1.getLocation().distance(commandSender.getLocation()), p2.getLocation().distance(commandSender.getLocation())))
                        .orElse(null);
            }
        } else if (arg.equalsIgnoreCase("@s")) {
            return sender instanceof Player ? (Player) sender : null;
        } else {
            return Bukkit.getPlayer(arg);
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            completions.add("@s");
        }

        if (args.length >= 1) {
            String prefix = args[args.length - 1].toLowerCase();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(prefix)) {
                    completions.add(onlinePlayer.getName());
                }
            }
        }

        return completions;
    }
}
