package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.ConfigManager;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TeleportCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final ConfigManager configManager;
    private final Messages msg;

    public TeleportCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Berechtigungen prüfen
        if (args.length == 1 && !sender.hasPermission("apsurvival.teleport.self")) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm").replace("%perm%", "apsurvival.teleport.self"));
            return true;
        } else if (args.length > 1 && !sender.hasPermission("apsurvival.teleport.others")) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm").replace("%perm%", "apsurvival.teleport.others"));
            return true;
        }

        // Überprüfen der Argumentanzahl
        if (args.length == 0 || args.length > 4) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.usage"));
            return true;
        }

        // Spieler teleportieren
        if (args.length == 1) {
            Player targetPlayer = resolveTargetPlayer(sender, args[0]);
            if (targetPlayer != null) {
                teleport(sender, targetPlayer, targetPlayer.getLocation());
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.success").replace("%target%", targetPlayer.getName()));
            } else {
                Location location = parseLocation(sender, args[0]);
                if (location != null) {
                    teleport(sender, (Player) sender, location);
                    sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.success.location").replace("%location%", locationToString(location)));
                } else {
                    sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-location"));
                }
            }
            return true;
        } else if (args.length == 2) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer != null) {
                Location location = parseLocation(sender, args[1]);
                if (location != null) {
                    teleport(sender, targetPlayer, location);
                    sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.success.other").replace("%target%", targetPlayer.getName()));
                } else {
                    sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-location"));
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.player-not-found"));
            }
            return true;
        } else if (args.length == 3) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            Location location = parseLocation(sender, args[1], args[2]);
            if (targetPlayer != null && location != null) {
                teleport(sender, targetPlayer, location);
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.success.other").replace("%target%", targetPlayer.getName()));
            } else {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-location"));
            }
            return true;
        }

        sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.usage"));
        return true;
    }

    private void teleport(CommandSender sender, Player target, Location location) {
        target.teleport(location);
        if (sender instanceof Player && sender != target) {
            target.sendMessage(plugin.getPrefix() + msg.getMessage("command.teleport.success").replace("%target%", target.getName()));
        }
    }

    private Player resolveTargetPlayer(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("@a")) {
            return null; // Alle Spieler - kein spezifischer Spieler
        } else if (arg.equalsIgnoreCase("@r")) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            return players.isEmpty() ? null : players.get(new Random().nextInt(players.size()));
        } else if (arg.equalsIgnoreCase("@p")) {
            if (sender instanceof Player) {
                Player commandSender = (Player) sender;
                return Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p != commandSender)
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

    private Location parseLocation(CommandSender sender, String... args) {
        if (args.length < 3) {
            return null;
        }

        World world = (sender instanceof Player) ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0);

        // Prüfen, ob das Flag -w für die Welt gesetzt ist
        if (args.length == 4 && args[0].equalsIgnoreCase("-w")) {
            if (!sender.hasPermission("apsurvival.teleport.world")) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm").replace("%perm%", "apsurvival.teleport.world"));
                return null;
            }
            world = Bukkit.getWorld(args[1]);
            if (world == null) {
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.world-not-found"));
                return null;
            }
            args = Arrays.copyOfRange(args, 2, args.length); // X, Y, Z anpassen
        }

        try {
            double x = parseCoordinate(args[0], world.getSpawnLocation().getX());
            double y = parseCoordinate(args[1], world.getSpawnLocation().getY());
            double z = parseCoordinate(args[2], world.getSpawnLocation().getZ());
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double parseCoordinate(String arg, double currentValue) {
        if (arg.startsWith("~")) {
            String value = arg.substring(1);
            double offset = value.isEmpty() ? 0 : Double.parseDouble(value);
            return currentValue + offset; // Relativ zur aktuellen Position
        } else {
            return Double.parseDouble(arg); // Absolut
        }
    }

    private String locationToString(Location location) {
        return String.format("%s, %s, %s", location.getX(), location.getY(), location.getZ());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            completions.add("@s");
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        } else if (args.length == 2) {
            completions.add("~"); // Relativer X
            completions.add("0");
            completions.add("1");
            completions.add("2");
        } else if (args.length == 3) {
            completions.add("~"); // Relativer Y
            completions.add("0");
            completions.add("1");
            completions.add("2");
        } else if (args.length == 4) {
            completions.add("-w");
            completions.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
