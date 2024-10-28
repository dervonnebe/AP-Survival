package de.dervonnebe.aps.commands.warp;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.ConfigManager;
import de.dervonnebe.aps.utils.Messages;
import de.dervonnebe.aps.utils.PersistentDataManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetHomeCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;
    private final PersistentDataManager dataManager;
    private final ConfigManager configManager;

    public SetHomeCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.dataManager = plugin.getDataManager();
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length >= 1) {
            String homeName = args[0];
            boolean confirmOverwrite = args.length == 2 && args[1].equalsIgnoreCase("confirm");

            int maxHomes = getMaxHomes(player);
            int currentHomeCount = getHomeCount(player);

            if (dataManager.getLocation(player, "home_" + homeName) != null && !confirmOverwrite) {
                player.sendMessage(plugin.getPrefix() + "§cHome '" + homeName + "' already exists. Use /sethome " + homeName + " confirm to overwrite.");
                return true;
            } else if (currentHomeCount >= maxHomes && !confirmOverwrite) {
                player.sendMessage(plugin.getPrefix() + "§cYou have reached your home limit of " + maxHomes + ". Remove a home or upgrade your permissions.");
                return true;
            }

            dataManager.setLocation(player, "home_" + homeName, player.getLocation());
            player.sendMessage(plugin.getPrefix() + "§7Home '" + homeName + "' set successfully.");
        } else {
            player.sendMessage(plugin.getPrefix() + "§cUsage: /sethome <homeName> [confirm]");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2 && args[1].isEmpty()) {
            completions.add("confirm");
        }

        return completions;
    }


    private int getMaxHomes(Player player) {
        int maxHomes = configManager.getInt("teleportation.home.max-homes");

        for (int i = 10; i > 0; i--) {
            if (player.hasPermission("aps.home.max." + i)) {
                maxHomes = i;
                break;
            }
        }
        return maxHomes;
    }

    public int getHomeCount(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        int count = 0;

        for (NamespacedKey key : dataContainer.getKeys()) {
            if (key.getKey().startsWith("home_") && key.getKey().endsWith("_world")) {
                count++;
            }
        }

        return count;
    }
}
