package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatusPlaceholder extends PlaceholderExpansion {
    private final APSurvival plugin;

    public StatusPlaceholder(APSurvival plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "apstatus";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "DerVonNebe";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        String status = plugin.getDataManager().getStringData(player, "status");
        if (status == null || status.isEmpty()) return "";

        switch (params.toLowerCase()) {
            case "raw":
                // Roher Status ohne Formatierung, entferne nur § Zeichen aber behalte &
                return status.replaceAll("§[0-9a-fk-or]", "");
            case "colored":
                // Status mit Farben
                return ChatColor.translateAlternateColorCodes('&', status);
            case "brackets_raw":
                // Status in Klammern ohne Farben, entferne nur § Zeichen aber behalte &
                return "[" + status.replaceAll("§[0-9a-fk-or]", "") + "]";
            case "brackets_colored":
                // Status in Klammern mit Farben
                return "&7[" + status + "&7]";
            case "formatted":
                // Vollständig formatierter Status (wie in der Tab-Liste)
                return plugin.getStatusManager().getFormattedStatus(player, "tab");
            default:
                return ChatColor.translateAlternateColorCodes('&', status);
        }
    }
} 