package de.dervonnebe.aps.commands.tpa;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAAutoCommand implements CommandExecutor {

    private final APSurvival plugin;
    private final Messages msg;

    public TPAAutoCommand(APSurvival plugin) {
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

        if (!player.hasPermission("aps.command.tpauto")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.tpauto"));
            return true;
        }

        if (plugin.getDataManager().getBooleanData(player, "tpauto")) {
            plugin.getDataManager().setBooleanData(player, "tpauto", false);
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.tpauto.disabled"));
            return true;
        }

        plugin.getDataManager().setBooleanData(player, "tpauto", true);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.tpauto.enabled"));
        return true;
    }
}
