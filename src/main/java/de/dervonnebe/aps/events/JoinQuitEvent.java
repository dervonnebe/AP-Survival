package de.dervonnebe.aps.events;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.ConfigManager;
import de.dervonnebe.aps.utils.DatabaseManager;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JoinQuitEvent implements Listener {

    private final APSurvival plugin;
    private final ConfigManager configManager;
    private DatabaseManager databaseManager;

    public JoinQuitEvent(APSurvival plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add User to Database if not already in
        databaseManager.executeUpdate("INSERT OR IGNORE INTO users (uuid, username) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "')");

        if (configManager.getBoolean("join-leaves.join.particle")) {
            player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 100, 0.5, 0.5, 0.5, 0.1);
        }

        if (configManager.getBoolean("join-leaves.join.custom") && !configManager.getBoolean("join-leaves.join.no-message")) {
            event.setJoinMessage(null);
            sendEveryPlayerCustomMessageFromKey("join-leaves.join");
        } else if (configManager.getBoolean("join-leaves.join.no-message")) {
            event.setJoinMessage(null);
        }

        if (configManager.getBoolean("join-leaves.join.welcome.title")) {
            int titleFadeIn = configManager.getInt("join-leaves.join.welcome.title-fade-in") * 20;
            int titleDuration = configManager.getInt("join-leaves.join.welcome.title-stay") * 20;
            int titleFadeOut = configManager.getInt("join-leaves.join.welcome.title-fade-out") * 20;

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                String title = plugin.getMessages().getPlayerMessage(player, "join-leaves.welcome.title").replace("%player%", player.getName());
                String subtitle = configManager.getBoolean("join-leaves.join.welcome.subtitle") ?
                        plugin.getMessages().getPlayerMessage(player, "join-leaves.welcome.subtitle").replace("%player%", player.getName()) : null;

                player.sendTitle(title, subtitle, titleFadeIn, titleDuration, titleFadeOut);
            }, 10L);
        }

        if (configManager.getBoolean("join-leaves.join.welcome.actionbar")) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                String actionBarMessage = plugin.getMessages().getPlayerMessage(player, "join-leaves.welcome.actionbar").replace("%player%", player.getName());
                player.sendActionBar(actionBarMessage);
            }, 10L);
        }

        if (configManager.getBoolean("join-leaves.join.welcome.chat")) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                String chatMessage = plugin.getMessages().getPlayerMessage(player, "join-leaves.welcome.chat").replace("%player%", player.getName());
                player.sendMessage(chatMessage);
            }, 10L);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (configManager.getBoolean("join-leaves.leave.particle")) {
            Player player = event.getPlayer();
            player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation(), 10);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.4F, 1.1F);
        }

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
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = now.format(formatter);
                message.replace("%player%", player.getName());
                message.replace("%date%", formattedDate);
                player.sendMessage(message);
            }
        });
    }
}
