package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CustomCommandManager {

    private final APSurvival plugin;
    private final Messages msg;
    private final Map<String, CustomCommand> commands = new HashMap<>();

    public CustomCommandManager(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        plugin.log("Loading custom commands...");
        loadCommandsFromConfig();
        int commandCount = plugin.getConfig().getConfigurationSection("custom-commands.commands").getKeys(false).size();
        plugin.log("§l" + commandCount + "§7Custom commands loaded!");
    }

    private void loadCommandsFromConfig() {
        plugin.getConfig().getConfigurationSection("custom-commands.commands").getKeys(false).forEach(commandName -> {
            String actionType = plugin.getConfig().getString("custom-commands.commands." + commandName + ".action-type");
            String action = plugin.getConfig().getString("custom-commands.commands." + commandName + ".action");
            String message = plugin.getConfig().getString("custom-commands.commands." + commandName + ".message");

            CustomCommand customCommand = new CustomCommand(commandName, actionType, action, message);
            commands.put(commandName, customCommand);

            PluginCommand pluginCommand = plugin.getCommand(commandName);
            if (pluginCommand == null) {
                pluginCommand = plugin.getServer().getPluginCommand(commandName);
                pluginCommand.setExecutor((sender, command, label, args) -> {
                    handleCustomCommand(sender, customCommand, args);
                    return true;
                });
            }
        });
    }

    private void handleCustomCommand(CommandSender sender, CustomCommand command, String[] args) {
        String action = command.getAction().replace("%args%", String.join(" ", args));
        String message = command.getMessage();
        message.replace("%prefix%", plugin.getConfig().getString("prefix"));
        message.replace("%player%", sender.getName());
        message.replace("%action%", action);
        message.replace("%args%", String.join(" ", args));

        switch (command.getActionType()) {
            case "OPEN-URL":
                if (sender instanceof Player) {
                    ((Player) sender).sendMessage(message);
                    ((Player) sender).spigot().sendMessage(new net.md_5.bungee.api.chat.TextComponent(action));
                } else sender.sendMessage(message);
                break;
            case "RUNCOMMAND[PLAYER]":
                if (sender instanceof Player) {
                    ((Player) sender).performCommand(action);
                    if (command.getMessage() != null) {
                        sender.sendMessage(message);
                    }
                }
                break;
            case "RUNCOMMAND[SERVER]":
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action);
                if (command.getMessage() != null) {
                    sender.sendMessage(message);
                }
                break;
            case "PLAIN-MESSAGE":
                sender.sendMessage(message);
                break;
            default:
                if (sender instanceof Player) {
                    sender.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(((Player) sender).getPlayer(), "custom-command.invalid-actiontype"));
                } else sender.sendMessage(plugin.getPrefix() + msg.getMessage("custom-command.invalid-actiontype"));
        }
    }
}
