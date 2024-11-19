package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ChatClearCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Messages msg;

    public ChatClearCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("aps.command.chatclear")) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("no-perm")
                    .replace("%perm%", "aps.command.chatclear"));
            return true;
        }

        String targetLang = null;
        int delay = 0;
        
        // Parse flags
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-l") && i + 1 < args.length) {
                targetLang = args[i + 1].toLowerCase();
                i++;
            } else if (args[i].equals("-t") && i + 1 < args.length) {
                try {
                    delay = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.invalid-int")
                            .replace("%int%", args[i + 1]));
                    return true;
                }
            }
        }

        // Schedule chat clear
        if (delay > 0) {
            String finalTargetLang = targetLang;
            new BukkitRunnable() {
                @Override
                public void run() {
                    clearChat(sender, finalTargetLang);
                }
            }.runTaskLater(plugin, delay * 20L);

            // Ankündigung
            String announcement = msg.getMessage("command.chatclear.scheduled")
                    .replace("%seconds%", String.valueOf(delay));
            
            if (targetLang != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String playerLang = plugin.getDataManager().getStringData(player, "language");
                    if (playerLang != null && playerLang.equalsIgnoreCase(targetLang)) {
                        player.sendMessage(plugin.getPrefix() + announcement);
                    }
                }
            } else {
                Bukkit.broadcastMessage(plugin.getPrefix() + announcement);
            }
        } else {
            clearChat(sender, targetLang);
        }

        return true;
    }

    private void clearChat(CommandSender sender, String targetLang) {
        Random random = new Random();

        if (targetLang != null) {
            // Nur für Spieler mit der ausgewählten Sprache leeren
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerLang = plugin.getDataManager().getStringData(player, "language");
                if (playerLang != null && playerLang.equalsIgnoreCase(targetLang)) {
                    for (int i = 0; i < 100; i++) {
                        player.sendMessage("§" + random.nextInt(10));
                    }
                    player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.chatclear.cleared")
                            .replace("%player%", sender.getName()));
                }
            }
        } else {
            // Für alle Spieler leeren
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < 100; i++) {
                    player.sendMessage("§" + random.nextInt(10));
                }
                player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.chatclear.cleared")
                        .replace("%player%", sender.getName()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("aps.command.chatclear")) {
            return completions;
        }

        if (args.length > 0) {
            String lastArg = args[args.length - 1].toLowerCase();
            String prevArg = args.length > 1 ? args[args.length - 2].toLowerCase() : "";

            if (prevArg.equals("-l")) {
                completions.add("de");
                completions.add("en");
            } else if (prevArg.equals("-t")) {
                // Zeitvorschläge
                completions.add("5");
                completions.add("10");
                completions.add("30");
                completions.add("60");
            } else {
                // Flags
                completions.add("-l");
                completions.add("-t");
            }
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
} 