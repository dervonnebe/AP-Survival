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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GodModeCommand implements CommandExecutor, TabCompleter {
    APSurvival plugin;
    Messages msg;

    public GodModeCommand(APSurvival plugin) {
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
            if (player.hasPermission("aps.command.god")) {
                toggleGodMode(player, player);
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.no-perm").replace("%perm%", "aps.command.god"));
            }
        } else if (args.length == 1) {
            if (player.hasPermission("aps.command.god.others")) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.no-player-found").replace("%player%", args[0]));
                    return true;
                }
                toggleGodMode(player, target);
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.no-perm").replace("%perm%", "aps.command.god.others"));
            }
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/godmode [player]"));
        }
        return true;
    }

    private void toggleGodMode(Player sender, Player target) {
        boolean isGodModeEnabled = target.isInvulnerable();
        target.setInvulnerable(!isGodModeEnabled);

        if (isGodModeEnabled) {
            target.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(target, "command.essential.godmode-off.self"));
            if (!sender.equals(target)) {
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(sender, "command.essential.godmode-off.other").replace("%target%", target.getName()));
            }
        } else {
            target.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(target, "command.essential.godmode-on.self"));
            if (!sender.equals(target)) {
                sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(sender, "command.essential.godmode-on.other").replace("%target%", target.getName()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("aps.command.god.others")) {
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            return playerNames;
        }
        return Collections.emptyList();
    }
}
