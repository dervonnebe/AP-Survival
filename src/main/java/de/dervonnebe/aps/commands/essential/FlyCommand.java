package de.dervonnebe.aps.commands.essential;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlyCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public FlyCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-player"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            if (player.hasPermission("aps.command.fly")) {
                toggleFlight(player, player);
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.fly"));
            }
            return true;
        }

        if (args.length == 1 && player.hasPermission("aps.command.fly.other")) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.no-player-found").replace("%player%", args[0]));
                return true;
            }
            toggleFlight(player, target);
            return true;
        }

        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/fly [player]"));
        return true;
    }

    private void toggleFlight(Player executor, Player target) {
        if (target.getAllowFlight()) {
            target.setAllowFlight(false);
            target.setFlying(false);
            target.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(target,"command.essential.fly-off.self"));
            if (!executor.equals(target)) {
                executor.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(executor,"command.essential.fly-off.to").replace("%target%", target.getName()));
            }
        } else {
            target.setAllowFlight(true);
            Location checkAirLocation = target.getLocation().add(0, -2, 0);
            if (checkAirLocation.getBlock().getType() == Material.AIR) {
                target.setFlying(true);
            }
            target.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(target, "command.essential.fly-on.self"));
            if (!executor.equals(target)) {
                executor.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(executor, "command.essential.fly-on.to").replace("%target%", target.getName()));
            }
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("aps.command.fly.other")) {
            List<String> players = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                players.add(onlinePlayer.getName());
            }
            return players;
        }
        return Collections.emptyList();
    }
}
