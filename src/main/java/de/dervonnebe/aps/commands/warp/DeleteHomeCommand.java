package de.dervonnebe.aps.commands.warp;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import de.dervonnebe.aps.utils.PersistentDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteHomeCommand implements CommandExecutor {
    private final APSurvival plugin;
    private final Messages msg;
    private final PersistentDataManager dataManager;

    public DeleteHomeCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String homeName = args[0];
            String homeKey = "home_" + homeName;

            if (dataManager.getLocation(player, homeKey) != null) {
                dataManager.removeLocation(player, homeKey);
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.home.delete").replace("%home%", homeName));
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.home.not-exist").replace("%home%", homeName));
            }
        } else {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/deletehome <homeName>"));
        }
        return true;
    }
}
