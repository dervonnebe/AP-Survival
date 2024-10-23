package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSGCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;
    private final Map<Player, Player> lastMessaged = new HashMap<>();

    public MSGCommand(APSurvival plugin) {
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

        // Prüfen, ob der Spieler die Berechtigung hat, Nachrichten zu senden
        if (!player.hasPermission("aps.command.msg.send")) {
            player.sendMessage(msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.msg.send"));
            return true;
        }

        if (label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("r")) {
            if (player.hasPermission("aps.command.reply")) {
                player.sendMessage(msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.reply"));
                return true;
            }
            if (!lastMessaged.containsKey(player)) {
                player.sendMessage(msg.getPlayerMessage(player, "command.msg.no-reply"));
                return true;
            }

            Player target = lastMessaged.get(player);
            if (args.length == 0) {
                player.sendMessage(msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/reply <message>"));
                return true;
            }

            if (!target.hasPermission("aps.command.msg.receive")) {
                player.sendMessage(msg.getPlayerMessage(player, "command.msg.cannot-receive"));
                return true;
            }

            String message = String.join(" ", args);
            sendMessage(player, target, message);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/msg <player> <message>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(msg.getPlayerMessage(player, "command.player-not-found"));
            return true;
        }

        // Prüfen, ob der Zielspieler Nachrichten empfangen darf
        if (!target.hasPermission("aps.command.msg.receive")) {
            player.sendMessage(msg.getPlayerMessage(player, "command.msg.cannot-receive"));
            return true;
        }

        String message = String.join(" ", args).substring(args[0].length()).trim();
        sendMessage(player, target, message);
        lastMessaged.put(player, target);
        lastMessaged.put(target, player);
        return true;
    }

    private void sendMessage(Player sender, Player target, String message) {
        String formatSender = msg.getPlayerMessage(sender, "command.msg.format.sender")
                .replace("%target%", target.getName())
                .replace("%message%", message);
        String formatTarget = msg.getPlayerMessage(target, "command.msg.format.receiver")
                .replace("%sender%", sender.getName())
                .replace("%message%", message);

        sender.sendMessage(formatSender);
        target.sendMessage(formatTarget);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }
        return null;
    }
}
