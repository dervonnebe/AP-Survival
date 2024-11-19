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

public class ChatCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public ChatCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("ping")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.chat.usage"));
            return true;
        }

        boolean enable = !args[1].equalsIgnoreCase("off");
        plugin.getDataManager().setBooleanData(player, "chat.ping.disabled", !enable);
        
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, 
            enable ? "command.chat.ping.enabled" : "command.chat.ping.disabled"));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("ping");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("ping")) {
            completions.add("on");
            completions.add("off");
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
} 