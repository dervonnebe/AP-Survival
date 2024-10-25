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

public class HealCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public HealCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-player"));
            return true;
        }

        Player player = (Player) commandSender;

        if (strings.length == 0) {
            if (player.hasPermission("aps.command.heal")) {
                player.setHealth(20);
                player.setFoodLevel(20);
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.essential.heal"));
                return true;
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.heal"));
            }
        } else {
            if (player.hasPermission("aps.command.heal.other")) {
                Player target = plugin.getServer().getPlayer(strings[0]);

                if (target == null) {
                    player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-player-found").replace("%player%", strings[0]));
                    return true;
                }

                target.setHealth(20);
                target.setFoodLevel(20);
                target.sendMessage(plugin.getPrefix() + msg.getMessage("command.essential.heal-from").replace("%player%", player.getName()));
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.essential.heal-other").replace("%player%", target.getName()));
                return true;
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.heal.other"));
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("aps.command.heal.other")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }

        return completions;
    }
}
