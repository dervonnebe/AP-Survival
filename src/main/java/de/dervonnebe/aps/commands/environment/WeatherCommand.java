package de.dervonnebe.aps.commands.environment;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeatherCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public WeatherCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("aps.command.weather")) {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.no-perm").replace("%perm%", "aps.command.weather"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid").replace("%command%", "/weather <clear | rain | thunder> [-w <world> | -t <time>]"));
            return true;
        }

        String weatherType = args[0].toLowerCase();
        boolean worldSpecified = false;
        String worldName = null;
        int duration = 1000;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-w") && i + 1 < args.length) {
                worldName = args[i + 1];
                worldSpecified = true;
                i++;
            } else if (args[i].equalsIgnoreCase("-t") && i + 1 < args.length) {
                try {
                    duration = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-int").replace("%int%", args[i + 1]));
                    return true;
                }
            }
        }

        switch (weatherType) {
            case "clear":
                clearWeather(player, worldName, duration);
                break;
            case "rain":
                setRainWeather(player, worldName, duration);
                break;
            case "thunder":
                setThunderWeather(player, worldName, duration);
                break;
            default:
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid").replace("%command%", "/weather <clear | rain | thunder> [-w <world> | -t <time>]"));
                break;
        }
        return true;
    }

    private void clearWeather(Player player, String worldName, int duration) {
        if (worldName != null) {
            if (player.getServer().getWorld(worldName) != null) {
                player.getServer().getWorld(worldName).setClearWeatherDuration(duration);
                player.getServer().getWorld(worldName).setStorm(false);
                player.getServer().getWorld(worldName).setThundering(false);
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.sun"));
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-world").replace("%world%", worldName));
            }
        } else {
            player.getWorld().setClearWeatherDuration(duration);
            player.getWorld().setStorm(false);
            player.getWorld().setThundering(false);
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.sun"));
        }
    }

    private void setRainWeather(Player player, String worldName, int duration) {
        if (worldName != null) {
            if (player.getServer().getWorld(worldName) != null) {
                player.getServer().getWorld(worldName).setStorm(true);
                player.getServer().getWorld(worldName).setWeatherDuration(duration);
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.rain"));
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-world").replace("%world%", worldName));
            }
        } else {
            player.getWorld().setStorm(true);
            player.getWorld().setWeatherDuration(duration);
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.rain"));
        }
    }

    private void setThunderWeather(Player player, String worldName, int duration) {
        if (worldName != null) {
            if (player.getServer().getWorld(worldName) != null) {
                player.getServer().getWorld(worldName).setThundering(true);
                player.getServer().getWorld(worldName).setWeatherDuration(duration);
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.thunder"));
            } else {
                player.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-world").replace("%world%", worldName));
            }
        } else {
            player.getWorld().setThundering(true);
            player.getWorld().setWeatherDuration(duration);
            player.sendMessage(plugin.getPrefix() + msg.getMessage("command.environment.thunder"));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("clear");
            completions.add("rain");
            completions.add("thunder");
        } else if (args.length == 2) {
            if ("-w".equalsIgnoreCase(args[1])) {
                for (var world : Bukkit.getWorlds()) {
                    completions.add(world.getName());
                }
            } else {
                completions.add("-w");
                completions.add("-t");
            }
        } else if (args.length == 3 && "-t".equalsIgnoreCase(args[1])) {
            completions.add("1000");
        }

        return completions;
    }
}
