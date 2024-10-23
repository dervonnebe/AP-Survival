package de.dervonnebe.aps.commands.tpa;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAHereCommand implements CommandExecutor {

    private final APSurvival plugin;
    private final Messages msg;
    private TPA tpa;

    public TPAHereCommand(APSurvival plugin) {
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

        if (!player.hasPermission("aps.command.tpahere")) {
            player.sendMessage(msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.tpahere"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/tpahere <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(msg.getPlayerMessage(player, "command.player-not-found"));
            return true;
        }

        target.sendMessage(msg.getPlayerMessage(target, "command.tpahere.request").replace("%player%", player.getName()));
        player.sendMessage(msg.getPlayerMessage(player, "command.tpahere.sent").replace("%target%", target.getName()));

        tpa.addTeleportRequest(player, target);
        return true;
    }
}
