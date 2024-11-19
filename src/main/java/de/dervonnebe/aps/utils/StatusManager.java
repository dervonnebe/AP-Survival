package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class StatusManager implements Listener {
    private final APSurvival plugin;

    public StatusManager(APSurvival plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public String getFormattedStatus(Player player, String type) {
        String status = plugin.getDataManager().getStringData(player, "status");
        if (status == null || status.trim().isEmpty()) return "";
        
        String format = plugin.getConfigManager().getString("status.format." + type);
        if (format == null) return "";
        
        return ChatColor.translateAlternateColorCodes('&', 
            format.replace("%status%", status));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfigManager().getBoolean("status.enabled")) return;
        
        Player player = event.getPlayer();
        String status = getFormattedStatus(player, "chat");
        if (!status.isEmpty()) {
            event.setFormat(status + " " + event.getFormat());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfigManager().getBoolean("status.enabled")) return;
        
        Player player = event.getPlayer();
        updatePlayerListName(player);
    }

    public void updatePlayerListName(Player player) {
        player.setPlayerListName(player.getName());
        
        String status = getFormattedStatus(player, "tab");
        if (!status.isEmpty()) {
            player.setPlayerListName(status + " §r" + player.getName());
        }
    }
} 