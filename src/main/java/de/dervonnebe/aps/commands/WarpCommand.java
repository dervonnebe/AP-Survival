package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WarpCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public WarpCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            if (!player.hasPermission("aps.command.warp.list")) {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm")
                    .replace("%perm%", "aps.command.warp.list"));
                return true;
            }
            listWarps(player);
            return true;
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String warpName = args[1].toLowerCase();

            switch (subCommand) {
                case "set":
                    if (!player.hasPermission("aps.command.warp.set")) {
                        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm")
                            .replace("%perm%", "aps.command.warp.set"));
                        return true;
                    }
                    setWarp(player, warpName);
                    break;
                case "delete":
                    if (!player.hasPermission("aps.command.warp.delete")) {
                        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm")
                            .replace("%perm%", "aps.command.warp.delete"));
                        return true;
                    }
                    deleteWarp(player, warpName);
                    break;
                default:
                    teleportToWarp(player, args[0]);
                    break;
            }
            return true;
        }

        if (args.length == 1) {
            if (!player.hasPermission("aps.command.warp")) {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm")
                    .replace("%perm%", "aps.command.warp"));
                return true;
            }
            teleportToWarp(player, args[0]);
            return true;
        }

        return false;
    }

    private void listWarps(Player player) {
        Set<String> warps = plugin.getLocationManager().getWarps();
        if (warps.isEmpty()) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.no-warps"));
            return;
        }
        String warpList = String.join("§7, §f", warps);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.list")
            .replace("%warps%", warpList));
    }

    private void setWarp(Player player, String warpName) {
        if (plugin.getLocationManager().exists("warp." + warpName)) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.already-exists")
                .replace("%warp%", warpName));
            return;
        }
        plugin.getLocationManager().saveLocation("warp." + warpName, player.getLocation());
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.set")
            .replace("%warp%", warpName));
    }

    private void deleteWarp(Player player, String warpName) {
        if (!plugin.getLocationManager().exists("warp." + warpName)) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.not-found")
                .replace("%warp%", warpName));
            return;
        }
        plugin.getLocationManager().removeLocation("warp." + warpName);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.deleted")
            .replace("%warp%", warpName));
    }

    private void teleportToWarp(Player player, String warpName) {
        Location warpLoc = plugin.getLocationManager().getLocation("warp." + warpName);
        if (warpLoc == null) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.not-found")
                .replace("%warp%", warpName));
            return;
        }
        player.teleport(warpLoc);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.warp.teleported")
            .replace("%warp%", warpName));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (sender.hasPermission("aps.command.warp.set")) 
                completions.add("set");
            if (sender.hasPermission("aps.command.warp.delete")) 
                completions.add("delete");
            if (sender.hasPermission("aps.command.warp.list")) 
                completions.add("list");
            
            // Füge existierende Warps zur Tab-Completion hinzu
            completions.addAll(plugin.getLocationManager().getWarps());
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            completions.addAll(plugin.getLocationManager().getWarps());
        }
        
        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
            .collect(Collectors.toList());
    }
} 