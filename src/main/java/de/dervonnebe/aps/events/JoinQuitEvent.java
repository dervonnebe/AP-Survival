package de.dervonnebe.aps.events;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitEvent implements Listener {

    private final APSurvival plugin;
    private final ConfigManager configManager;

    public JoinQuitEvent(APSurvival plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (configManager.getBoolean("join-leaves.join.custom") && !configManager.getBoolean("join-leaves.join.no-message")) {
            event.setJoinMessage(null);
            sendEveryPlayerCustomMessageFromKey("join-leaves.join");
        } else if (configManager.getBoolean("join-leaves.join.no-message")) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (configManager.getBoolean("join-leaves.leave.custom") && !configManager.getBoolean("join-leaves.leave.no-message")) {
            event.setQuitMessage(null);
            sendEveryPlayerCustomMessageFromKey("join-leaves.leave");
        } else if (configManager.getBoolean("join-leaves.leave.no-message")) {
            event.setQuitMessage(null);
        }

    }

    private void sendEveryPlayerCustomMessageFromKey(String key) {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            String message = plugin.getMessages().getPlayerMessage(player, key);
            if (message != null) {
                player.sendMessage(message);
            }
        });
    }
}
