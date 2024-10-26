package de.dervonnebe.aps.commands.essential;

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
import java.util.List;

public class FeedCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public FeedCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) commandSender;

        if (args.length == 0) {
            if (player.hasPermission("aps.command.feed")) {
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.essential.feed"));
                return true;
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.feed"));
            }
        } else {
            if (player.hasPermission("aps.command.feed.other")) {
                Player target = plugin.getServer().getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-player-found").replace("%player%", args[0]));
                    return true;
                }

                target.setFoodLevel(20);
                target.setSaturation(20);
                target.sendMessage(plugin.getPrefix() + msg.getMessage("command.essential.feed-from").replace("%player%", player.getName()));
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.essential.feed-other").replace("%player%", target.getName()));
                return true;
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.feed.other"));
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("aps.command.feed.other")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }

        return completions;
    }
}
