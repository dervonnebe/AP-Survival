package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import de.dervonnebe.aps.utils.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class APSurvivalCommand implements CommandExecutor, TabCompleter {

    private final APSurvival plugin;
    private final Messages msg;
    private final PersistentDataManager dataManager;

    public APSurvivalCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("debug") && args[1].equalsIgnoreCase("data")) {
            Player target;

            if (args.length == 3) {
                target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(plugin.getPrefix() + "§cPlayer not found.");
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(plugin.getPrefix() + "§cPlease specify a player.");
                return true;
            }

            PersistentDataContainer dataContainer = target.getPersistentDataContainer();
            sender.sendMessage(plugin.getPrefix() + "§7Debug Information for §a" + target.getName() + "§7:");

            for (NamespacedKey key : dataContainer.getKeys()) {
                String value;
                if (dataContainer.has(key, PersistentDataType.STRING)) {
                    value = dataContainer.get(key, PersistentDataType.STRING);
                } else if (dataContainer.has(key, PersistentDataType.INTEGER)) {
                    value = String.valueOf(dataContainer.get(key, PersistentDataType.INTEGER));
                } else if (dataContainer.has(key, PersistentDataType.BYTE)) {
                    value = String.valueOf(dataContainer.get(key, PersistentDataType.BYTE) == 1);
                } else {
                    value = "§cUnknown DataType";
                }
                sender.sendMessage("§7Key: §a" + key.getKey() + " §7| Value: §a" + value);
            }

            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            return true;
        }

        sender.sendMessage(plugin.getPrefix() + "§7Invalid command usage.");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("debug");
            completions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            completions.add("data");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("debug") && args[1].equalsIgnoreCase("data")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}
