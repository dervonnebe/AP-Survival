package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SitLayCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public SitLayCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.sit-lay.toggle.usage"));
            return true;
        }

        String type = args[0].toLowerCase();
        boolean enable = args.length == 2 ? args[1].equalsIgnoreCase("on") : 
                        !plugin.getDataManager().getBooleanData(player, "sitlay." + type + ".enabled", true);

        switch (type) {
            case "sit":
                plugin.getDataManager().setBooleanData(player, "sitlay.sit.enabled", enable);
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, 
                    enable ? "command.sit-lay.sit.enabled" : "command.sit-lay.sit.disabled"));
                break;
            case "lay":
                plugin.getDataManager().setBooleanData(player, "sitlay.lay.enabled", enable);
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, 
                    enable ? "command.sit-lay.lay.enabled" : "command.sit-lay.lay.disabled"));
                break;
            default:
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.sit-lay.toggle.usage"));
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("sit");
            completions.add("lay");
        } else if (args.length == 2) {
            completions.add("on");
            completions.add("off");
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
} 