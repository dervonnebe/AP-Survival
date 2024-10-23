package de.dervonnebe.aps.commands.tpa;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TPACancelCommand implements CommandExecutor {

    private final APSurvival plugin;
    private final Messages msg;
    private TPA tpa;

    public TPACancelCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.tpa = plugin.getTpa();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("aps.command.tpacancel")) {
            player.sendMessage(msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.tpacancel"));
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !plugin.getTpa().getTeleportRequests().containsKey(target)) {
                player.sendMessage(msg.getPlayerMessage(player, "command.tpacancel.no-request"));
                return true;
            }

            tpa.removeRequest(target);
            player.sendMessage(msg.getPlayerMessage(player, "command.tpacancel.success").replace("%target%", target.getName()));
            return true;
        }

        tpa.getTeleportRequests().clear();
        player.sendMessage(msg.getPlayerMessage(player, "command.tpacancel.all-cancelled"));
        return true;
    }
}
