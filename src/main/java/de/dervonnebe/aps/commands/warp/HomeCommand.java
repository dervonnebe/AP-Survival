package de.dervonnebe.aps.commands.warp;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import de.dervonnebe.aps.utils.PersistentDataManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;
    private final PersistentDataManager dataManager;
    private final Map<UUID, Long> lastTeleportTimes = new HashMap<>();
    private long teleportDelay = 0;

    public HomeCommand(APSurvival plugin,) {
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

        Pattern pattern = Pattern.compile("aps\\.home\\.delay\\.(\\d+)");
        for (String permission : player.getEffectivePermissions().stream().map(perm -> perm.getPermission()).toList()) {
            Matcher matcher = pattern.matcher(permission);
            if (matcher.matches()) {
                int delaySeconds = Integer.parseInt(matcher.group(1));
                teleportDelay = delaySeconds * 1000L;
                break;
            }
        }

        if (teleportDelay > 0 && !player.hasPermission("aps.home.delay.bpypass")) {
            long currentTime = System.currentTimeMillis();
            long lastTeleportTime = lastTeleportTimes.getOrDefault(player.getUniqueId(), 0L);
            long timeSinceLastTeleport = currentTime - lastTeleportTime;

            if (timeSinceLastTeleport < teleportDelay) {
                long remainingTime = (teleportDelay - timeSinceLastTeleport) / 1000;
                player.sendMessage(plugin.getPrefix() + "§cYou must wait " + remainingTime + " seconds before using /home again.");
                return true;
            }
        }

        if (args.length == 1) {
            String homeName = args[0];
            Location homeLocation = dataManager.getLocation(player, "home_" + homeName);
            if (homeLocation != null) {
                player.teleport(homeLocation);
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.home.teleport").replace("{home}", homeName));

                lastTeleportTimes.put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                player.sendMessage(plugin.getPrefix() + "§cHome '" + homeName + "' does not exist.");
            }
        } else {
            player.sendMessage(plugin.getPrefix() + "§cUsage: /home <homeName>");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            Set<String> homeNames = getAllHomeNames(player);
            completions.addAll(homeNames);
        }

        return completions;
    }

    public Set<String> getAllHomeNames(Player player) {
        Set<String> homeNames = new HashSet<>();
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        for (NamespacedKey key : dataContainer.getKeys()) {
            if (key.getKey().startsWith("home_") && key.getKey().endsWith("_world")) {
                String homeName = key.getKey().substring(5, key.getKey().lastIndexOf("_world"));
                homeNames.add(homeName);
            }
        }
        return homeNames;
    }
}
