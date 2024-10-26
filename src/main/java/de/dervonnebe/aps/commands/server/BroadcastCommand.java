package de.dervonnebe.aps.commands.server;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastCommand implements CommandExecutor {
    private final APSurvival plugin;
    private final Messages msg;

    public BroadcastCommand(APSurvival plugin) {
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
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/broadcast <message> [-l (LANG)]"));
            return true;
        }

        String language = null;
        String message;
        if (args[args.length - 2].equalsIgnoreCase("-l")) {
            language = args[args.length - 1];
            message = String.join(" ", args).replace(" -l " + language, "");
        } else {
            message = String.join(" ", args);
        }

        if (command.toString().startsWith("alert")) {
            message = "§4" + message;
        }

        message = message.replaceAll("(?<!\\\\)&", "§").replaceAll("\\\\&", "&");

        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.server.broadcast.sent").replace("%message%", message).replace("%language%", language == null ? "all" : language));

        String formattedMessage = plugin.getPrefix() + message;
        if (language != null) {
            broadcastToLanguage(language, formattedMessage);
        } else {
            broadcastToAllPlayers(formattedMessage);
        }

        return true;
    }

    private void broadcastToLanguage(String language, String message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            String playerLanguage = plugin.getDataManager().getStringData(player, "language");
            if (playerLanguage != null && playerLanguage.equalsIgnoreCase(language)) {
                sendBroadcastMessage(player, message);
            }
        }
    }

    private void broadcastToAllPlayers(String message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            sendBroadcastMessage(player, message);
        }
    }

    private void sendBroadcastMessage(Player player, String message) {
        player.sendActionBar(msg.getPlayerMessage(player, "command.server.broadcast.income-alert"));
        player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_0, 1, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage("§3");
                player.sendMessage(message);
                player.sendMessage("§4");
            }
        }.runTaskLater(plugin, 10L);
    }
}
