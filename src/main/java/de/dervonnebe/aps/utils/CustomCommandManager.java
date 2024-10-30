package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CustomCommandManager {

    private final APSurvival plugin;
    private final Messages msg;
    private final Map<String, CustomCommand> commands = new HashMap<>();
    private CommandMap commandMap;

    public CustomCommandManager(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        loadCommandMap();
        loadCommandsFromConfig();
        plugin.log("§l" + commands.size() + "§7 Custom commands loaded!");
    }

    private void loadCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCommandsFromConfig() {
        plugin.getConfig().getConfigurationSection("custom-commands.commands").getKeys(false).forEach(commandName -> {
            String actionType = plugin.getConfig().getString("custom-commands.commands." + commandName + ".action-type");
            String action = plugin.getConfig().getString("custom-commands.commands." + commandName + ".action");
            String message = plugin.getConfig().getString("custom-commands.commands." + commandName + ".message");

            CustomCommand customCommand = new CustomCommand(commandName, actionType, action, message);
            commands.put(commandName, customCommand);

            // Command dynamisch registrieren
            registerCommand(commandName, customCommand);
        });
    }

    private void registerCommand(String commandName, CustomCommand customCommand) {
        if (commandMap != null) {
            Command dynamicCommand = new BukkitCommand(commandName) {
                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    handleCustomCommand(sender, customCommand, args);
                    return true;
                }
            };
            commandMap.register(plugin.getName(), dynamicCommand);
        }
    }

    private void handleCustomCommand(CommandSender sender, CustomCommand command, String[] args) {
        String action = command.getAction().replace("%args%", String.join(" ", args));
        String message = command.getMessage().replace("%prefix%", plugin.getConfig().getString("prefix"))
                .replace("%player%", sender.getName())
                .replace("%action%", action)
                .replace("%args%", String.join(" ", args));

        switch (command.getActionType()) {
            case "OPEN-URL":
                if (sender instanceof Player) {
                    ((Player) sender).sendMessage(message);
                    ((Player) sender).spigot().sendMessage(new TextComponent(action));
                } else sender.sendMessage(message);
                break;
            case "RUNCOMMAND[PLAYER]":
                if (sender instanceof Player) {
                    ((Player) sender).performCommand(action);
                    if (command.getMessage() != null) {
                        sender.sendMessage(message);
                    }
                } else sender.sendMessage(plugin.getPrefix() + msg.getMessage("custom-command.no-player"));
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
                sender.sendMessage(plugin.getPrefix() + msg.getMessage("custom-command.invalid-actiontype"));
        }
    }
}
