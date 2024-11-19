package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager implements Listener {
    private final APSurvival plugin;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatManager(APSurvival plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfigManager().getBoolean("chat.enabled")) return;

        event.setCancelled(true);
        
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Verarbeite Chat-Pings
        if (plugin.getConfigManager().getBoolean("chat.ping.enabled")) {
            message = processPings(message, player);
        }

        // Prüfe Farbcode-Berechtigung
        boolean canUseColors = plugin.getConfigManager().getBoolean("chat.colors.all-players") 
                || player.hasPermission("aps.chat.colors");
        
        // Wenn keine Berechtigung, entferne Farbcodes aus der Nachricht
        if (!canUseColors) {
            message = message.replaceAll("&[0-9a-fk-or]", "");
        } else {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        // Erstelle das Chat-Format
        String format = plugin.getConfigManager().getString("chat.format");
        format = format.replace("%time%", timeFormat.format(new Date()));
        format = format.replace("%world%", player.getWorld().getName());
        format = format.replace("%player%", player.getName());
        format = format.replace("%message%", message);
        
        // Füge den Status-Placeholder hinzu
        if (plugin.getStatusManager() != null) {
            String status = plugin.getStatusManager().getFormattedStatus(player, "chat");
            format = format.replace("%status%", (status != null && !status.trim().isEmpty()) ? status : "");
        } else {
            format = format.replace("%status%", "");
        }

        // Färbe den Chat (Format wird immer gefärbt)
        final String finalFormat = ChatColor.translateAlternateColorCodes('&', format);

        // Sende die Nachricht an alle Spieler
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(finalFormat));
        // Log in Konsole
        plugin.getServer().getConsoleSender().sendMessage(finalFormat);
    }

    private String processPings(String message, Player sender) {
        String[] words = message.split(" ");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Entferne das @ wenn vorhanden
            String playerName = word.startsWith("@") ? word.substring(1) : word;
            
            Player target = Bukkit.getPlayer(playerName);
            if (target != null && target.isOnline() && canPingPlayer(target)) {
                // Ersetze den Namen mit dem formatierten Ping
                String pingFormat = plugin.getConfigManager().getString("chat.ping.format")
                        .replace("%player%", target.getName());
                result.append(ChatColor.translateAlternateColorCodes('&', pingFormat));
                
                // Spiele den Ping-Sound
                playPingSound(target);
            } else {
                result.append(word);
            }
            
            // Füge Leerzeichen hinzu, außer beim letzten Wort
            if (i < words.length - 1) {
                result.append(" ");
            }
        }
        
        return result.toString();
    }

    private boolean canPingPlayer(Player player) {
        return !plugin.getDataManager().getBooleanData(player, "chat.ping.disabled", false);
    }

    private void playPingSound(Player player) {
        String soundName = plugin.getConfigManager().getString("chat.ping.sound");
        float volume = (float) plugin.getConfigManager().getDouble("chat.ping.volume");
        float pitch = (float) plugin.getConfigManager().getDouble("chat.ping.pitch");

        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.log("Invalid sound in config: " + soundName, "ERROR");
        }
    }
} 