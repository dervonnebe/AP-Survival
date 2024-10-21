package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class APSurvivalCommand implements CommandExecutor {

    private APSurvival plugin;

    //Constructor
    public APSurvivalCommand(APSurvival plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix() + "§7This command is not available yet!");
            return true;
        }
        return false;
    }
}
