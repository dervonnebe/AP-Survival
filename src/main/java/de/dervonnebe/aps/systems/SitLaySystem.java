package de.dervonnebe.aps.systems;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitLaySystem implements Listener {
    private final APSurvival plugin;
    private final Map<UUID, ArmorStand> sittingPlayers = new HashMap<>();
    private final Map<UUID, ArmorStand> layingPlayers = new HashMap<>();
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public SitLaySystem(APSurvival plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void attemptSit(Player player) {
        if (!plugin.getConfigManager().getBoolean("sit-and-lay.enabled") || 
            !plugin.getConfigManager().getBoolean("sit-and-lay.sit.enabled")) {
            return;
        }

        if (sittingPlayers.containsKey(player.getUniqueId())) {
            unsitPlayer(player);
            return;
        }

        if (isPlayerOccupied(player)) {
            return;
        }

        previousLocations.put(player.getUniqueId(), player.getLocation().clone());
        
        Location seatLocation = player.getLocation().clone();
        seatLocation.add(0, -0.7, 0);
        
        ArmorStand seat = createSeat(seatLocation, "sit");
        sittingPlayers.put(player.getUniqueId(), seat);
        
        // Verzögertes Teleportieren für bessere Animation
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (seat.isValid() && player.isOnline()) {
                seat.addPassenger(player);
                player.sendMessage(plugin.getPrefix() + plugin.getMessages().getPlayerMessage(player, "command.sit-lay.sit.action"));
            }
        }, 1L);
    }

    public void attemptLay(Player player) {
        if (!plugin.getConfigManager().getBoolean("sit-and-lay.enabled") ||
                !plugin.getConfigManager().getBoolean("sit-and-lay.lay.enabled")) {
            return;
        }

        if (layingPlayers.containsKey(player.getUniqueId())) {
            unlayPlayer(player);
            return;
        }

        if (isPlayerOccupied(player)) {
            return;
        }

        previousLocations.put(player.getUniqueId(), player.getLocation().clone());

        Location layLocation = player.getLocation().clone();
        layLocation.setY(layLocation.getY() - 1); // Spieler etwas absenken

        // Temporäres Bett platzieren und Spieler schlafen lassen
        ArmorStand seat = createSeat(layLocation, "lay");
        layingPlayers.put(player.getUniqueId(), seat);

        layPlayer(player, layLocation);

        // Automatische Entlay-Logik, falls aktiviert
        int maxDuration = plugin.getConfigManager().getInt("sit-and-lay.lay.max-duration");
        if (maxDuration > 0) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> unlayPlayer(player), maxDuration * 20L);
        }
    }

    private void layPlayer(Player player, Location location) {
        // Temporäres Bett platzieren
        Location bedLocation = location.clone();
        bedLocation.setY(location.getY() - 1);
        bedLocation.getBlock().setType(Material.RED_BED);

        // Spieler in die Schlafposition versetzen
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && bedLocation.getBlock().getType() == Material.RED_BED) {
                player.sleep(bedLocation, true);
                player.sendMessage(plugin.getPrefix() + plugin.getMessages().getPlayerMessage(player, "command.sit-lay.lay.action"));
            }
        }, 1L);
    }

    private void unlayPlayer(Player player) {
        ArmorStand seat = layingPlayers.remove(player.getUniqueId());
        if (seat != null) {
            restorePlayerPosition(player);
            seat.remove();
        }

        // Bett an vorheriger Position entfernen
        Location previousLoc = previousLocations.get(player.getUniqueId());
        if (previousLoc != null) {
            Block bedBlock = previousLoc.clone().add(0, -1, 0).getBlock();
            if (bedBlock.getType() == Material.RED_BED) {
                bedBlock.setType(Material.AIR);
            }
        }
    }

    private boolean isPlayerOccupied(Player player) {
        return player.isInsideVehicle() || 
               player.isSleeping() || 
               player.isGliding() || 
               player.isSwimming() || 
               player.isDead();
    }

    private ArmorStand createSeat(Location location, String type) {
        ArmorStand seat = location.getWorld().spawn(location, ArmorStand.class);
        seat.setVisible(false);
        seat.setGravity(false);
        seat.setInvulnerable(true);
        seat.setSmall(true);
        seat.setMarker(true); // Verhindert Kollisionen
        seat.setMetadata("aps_seat_type", new FixedMetadataValue(plugin, type));
        
        if (type.equals("lay")) {
            location.setPitch(90);
            seat.setRotation(location.getYaw(), location.getPitch());
        }
        
        return seat;
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (sittingPlayers.containsKey(player.getUniqueId())) {
                unsitPlayer(player);
            } else if (layingPlayers.containsKey(player.getUniqueId())) {
                unlayPlayer(player);
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        
        if (sittingPlayers.containsKey(player.getUniqueId()) || 
            layingPlayers.containsKey(player.getUniqueId())) {
            unsitPlayer(player);
            unlayPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (sittingPlayers.containsKey(player.getUniqueId()) || 
            layingPlayers.containsKey(player.getUniqueId())) {
            unsitPlayer(player);
            unlayPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        unsitPlayer(player);
        unlayPlayer(player);
    }

    private void unsitPlayer(Player player) {
        ArmorStand seat = sittingPlayers.remove(player.getUniqueId());
        if (seat != null) {
            restorePlayerPosition(player);
            seat.remove();
        }
    }

    private void restorePlayerPosition(Player player) {
        Location previousLoc = previousLocations.remove(player.getUniqueId());
        if (previousLoc != null) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.teleport(previousLoc);
                }
            }, 1L);
        }
    }

    public void cleanup() {
        sittingPlayers.values().forEach(Entity::remove);
        layingPlayers.values().forEach(Entity::remove);
        sittingPlayers.clear();
        layingPlayers.clear();
        previousLocations.clear();
    }
} 