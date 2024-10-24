package de.dervonnebe.aps.events;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;

public class CommandPreProcessEvent implements Listener {

    private final APSurvival plugin;
    private Messages msg;

    public CommandPreProcessEvent(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.startsWith("/minecraft:")) {
            if (!player.hasPermission("aps.command.minecraft")) {
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.minecraft"));
                event.setCancelled(true);
            }
        }
    }
}

