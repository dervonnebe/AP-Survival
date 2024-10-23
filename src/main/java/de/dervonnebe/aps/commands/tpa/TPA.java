package de.dervonnebe.aps.commands.tpa;

import de.dervonnebe.aps.APSurvival;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TPA {
    @Getter
    @Setter
    private Map<Player, TeleportRequest> teleportRequests = new HashMap<>();

    APSurvival plugin;

    public TPA(APSurvival plugin) {
        this.plugin = plugin;
        // Starte den Scheduler, der die Anfragen regelmäßig überprüft
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                teleportRequests.entrySet().removeIf(entry -> (currentTime - entry.getValue().getTimestamp()) > (5 * 60 * 60 * 1000));
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 5); // Überprüfung jede 5 Minuten
    }

    public void addTeleportRequest(Player requester, Player target) {
        teleportRequests.put(requester, new TeleportRequest(requester, target, System.currentTimeMillis()));
    }

    public boolean hasRequest(Player player) {
        return teleportRequests.containsKey(player);
    }

    public TeleportRequest getRequest(Player player) {
        return teleportRequests.get(player);
    }

    public void removeRequest(Player player) {
        teleportRequests.remove(player);
    }
}
