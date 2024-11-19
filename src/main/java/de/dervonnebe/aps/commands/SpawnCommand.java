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

public class SpawnCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public SpawnCommand(APSurvival plugin) {
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

        if (args.length == 0) {
            if (!player.hasPermission("aps.command.spawn")) {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm")
                    .replace("%perm%", "aps.command.spawn"));
                return true;
            }
            teleportToSpawn(player);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("aps.command.spawn.set")) {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm")
                    .replace("%perm%", "aps.command.spawn.set"));
                return true;
            }
            setSpawn(player);
            return true;
        }

        return false;
    }

    private void teleportToSpawn(Player player) {
        Location spawnLoc = plugin.getLocationManager().getLocation("spawn");
        if (spawnLoc != null) {
            player.teleport(spawnLoc);
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.spawn.teleported"));
        } else {
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.spawn.not-set"));
        }
    }

    private void setSpawn(Player player) {
        plugin.getLocationManager().saveLocation("spawn", player.getLocation());
        player.getWorld().setSpawnLocation(player.getLocation());
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.spawn.set"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("aps.command.spawn.set")) {
            completions.add("set");
        }
        return completions;
    }
} 