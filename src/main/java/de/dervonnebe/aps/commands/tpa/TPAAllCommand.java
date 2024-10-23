package de.dervonnebe.aps.commands.tpa;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAAllCommand implements CommandExecutor {

    private final APSurvival plugin;
    private final Messages msg;
    private TPA tpa;

    public TPAAllCommand(APSurvival plugin) {
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

        if (!player.hasPermission("aps.command.tpaall")) {
            player.sendMessage(msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.tpaall"));
            return true;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.hasPermission("aps.command.tpaccept")) {
                target.sendMessage(msg.getPlayerMessage(target, "command.tpa.request").replace("%player%", player.getName()));
                player.sendMessage(msg.getPlayerMessage(player, "command.tpaall.sent").replace("%target%", target.getName()));
                tpa.addTeleportRequest(player, target);
            }
        }

        return true;
    }
}
