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
import java.util.Random;
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
            Player target = resolvePlayer(args[0], sender);
            
            if (target == null) {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found")
                        .replace("%player%", args[0]));
                return true;
            }

            player.teleport(target.getLocation());
            return true;
        }

        // /tp <x> <y> <z>
        if (args.length == 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
                return true;
            }
            Player player = (Player) sender;
            
            try {
                double x = parseCoordinate(args[0], player.getLocation().getX());
                double y = parseCoordinate(args[1], player.getLocation().getY());
                double z = parseCoordinate(args[2], player.getLocation().getZ());
                
                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-int")
                        .replace("%int%", String.join(" ", args[0], args[1], args[2])));
                return true;
            }
        }

        // /tp <spieler> <zielspieler>
        if (args.length == 2) {
            Player player = resolvePlayer(args[0], sender);
            Player target = resolvePlayer(args[1], sender);

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
            Player player = resolvePlayer(args[0], sender);
            
            if (player == null) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found")
                        .replace("%player%", args[0]));
                return true;
            }
            
            try {
                double x = parseCoordinate(args[1], player.getLocation().getX());
                double y = parseCoordinate(args[2], player.getLocation().getY());
                double z = parseCoordinate(args[3], player.getLocation().getZ());
                
                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-int")
                        .replace("%int%", String.join(" ", args[1], args[2], args[3])));
                return true;
            }
        }

        sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid")
                .replace("%command%", "/tp <spieler|x y z> [zielspieler|spieler x y z]"));
        return true;
    }

    private Player resolvePlayer(String selector, CommandSender sender) {
        if (selector.startsWith("@")) {
            switch (selector) {
                case "@p":
                    if (sender instanceof Player) {
                        return getNearestPlayer((Player) sender);
                    }
                    return null;
                case "@r":
                    return getRandomPlayer();
                case "@s":
                    return sender instanceof Player ? (Player) sender : null;
                case "@a":
                    if (sender instanceof Player) {
                        return (Player) sender; // Für Sicherheit nur sich selbst
                    }
                    return null;
                default:
                    return null;
            }
        }
        return Bukkit.getPlayer(selector);
    }

    private Player getNearestPlayer(Player source) {
        double closestDistance = Double.MAX_VALUE;
        Player closestPlayer = null;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != source && player.getWorld() == source.getWorld()) {
                double distance = player.getLocation().distance(source.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }
        }
        
        return closestPlayer;
    }

    private Player getRandomPlayer() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) return null;
        return players.get(new Random().nextInt(players.size()));
    }

    private double parseCoordinate(String coord, double current) {
        if (coord.startsWith("~")) {
            if (coord.length() > 1) {
                return current + Double.parseDouble(coord.substring(1));
            }
            return current;
        }
        return Double.parseDouble(coord);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("aps.command.teleport")) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("@p");
            completions.add("@r");
            completions.add("@s");
            if (sender.hasPermission("aps.command.teleport.all")) {
                completions.add("@a");
            }
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            
            // Füge Koordinaten-Beispiele hinzu, wenn der Sender ein Spieler ist
            if (sender instanceof Player) {
                completions.add("~");
                completions.add("~ ~");
            }
        } else if (args.length == 2) {
            // Wenn der erste Parameter ein Spieler ist
            if (Bukkit.getPlayer(args[0]) != null || args[0].startsWith("@")) {
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()));
                completions.add("~");
            }
            // Wenn der erste Parameter eine Koordinate war
            else {
                completions.add("~");
            }
        } else if (args.length == 3 || args.length == 4) {
            completions.add("~");
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
