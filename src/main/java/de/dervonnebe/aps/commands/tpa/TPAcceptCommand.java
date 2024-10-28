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

public class TPAcceptCommand implements CommandExecutor {

    private final APSurvival plugin;
    private final Messages msg;
    private TPA tpa;

    public TPAcceptCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.tpa = plugin.getTpa();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("aps.command.tpaccept")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.tpaccept"));
            return true;
        }

        if (plugin.getTpa().getTeleportRequests().containsKey(player)) {
            Player requester = tpa.getRequest(player).getRequester();
            player.teleport(requester.getLocation());
            requester.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(requester, "command.tpaccept.success").replace("%player%", player.getName()));
            tpa.removeRequest(player);
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.tpaccept.no-request"));
        }
        return true;
    }
}
