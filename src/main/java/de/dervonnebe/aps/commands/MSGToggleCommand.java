package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.DatabaseManager;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MSGToggleCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final DatabaseManager dbm;
    private final Messages msg;

    public MSGToggleCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.dbm = plugin.getDatabaseManager();
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            boolean toggled = isMessagesToggled(player);
            toggleMessages(player, !toggled);
            player.sendMessage(msg.getPlayerMessage(player, toggled ? "msgtoggle.off" : "msgtoggle.on"));
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("-t")) {
                if (args.length < 2) {
                    player.sendMessage(msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/msgtoggle -t <duration>"));
                    return true;
                }
                handleTemporaryToggle(player, args[1]);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(msg.getPlayerMessage(player, "command.player-not-found"));
                return true;
            }

            if (args.length == 1) {
                boolean blocked = isTargetToggled(target);
                player.sendMessage(msg.getPlayerMessage(player, blocked ? "msgtoggle.player.blocked" : "msgtoggle.player.not-blocked").replace("%player%", target.getName()));
                return true;
            }

            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("on")) {
                    toggleTargetMessages(target, false);
                    player.sendMessage(msg.getPlayerMessage(player, "msgtoggle.player.on").replace("%player%", target.getName()));
                } else if (args[1].equalsIgnoreCase("off")) {
                    toggleTargetMessages(target, true);
                    player.sendMessage(msg.getPlayerMessage(player, "msgtoggle.player.off").replace("%player%", target.getName()));
                }

                if (args.length == 4 && args[2].equalsIgnoreCase("-t")) {
                    handleTemporaryToggle(player, target, args[3]);
                }
            }
        }

        return true;
    }

    private void handleTemporaryToggle(Player player, String timeArg) {
        long durationMillis = parseTimeArg(timeArg);
        if (durationMillis > 0) {
            Instant unblockTime = Instant.now().plusMillis(durationMillis);
            dbm.executePreparedUpdate("UPDATE users SET msg_toggle_time = ? WHERE uuid = ?", unblockTime.toString(), player.getUniqueId().toString());
            player.sendMessage(msg.getPlayerMessage(player, "msgtoggle.temp.on").replace("%duration%", timeArg));
            // Nachrichten nach der Zeit wieder aktivieren
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                toggleMessages(player, false);
                player.sendMessage(msg.getPlayerMessage(player, "msgtoggle.temp.off"));
            }, durationMillis / 50);
        }
    }

    private void handleTemporaryToggle(Player player, Player target, String timeArg) {
        long durationMillis = parseTimeArg(timeArg);
        if (durationMillis > 0) {
            Instant unblockTime = Instant.now().plusMillis(durationMillis);
            dbm.executePreparedUpdate("UPDATE users SET msg_toggle_time = ? WHERE uuid = ?", unblockTime.toString(), target.getUniqueId().toString());
            player.sendMessage(msg.getPlayerMessage(player, "msgtoggle.player.temp.on")
                    .replace("%player%", target.getName()).replace("%duration%", timeArg));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                toggleTargetMessages(target, false);
                player.sendMessage(msg.getPlayerMessage(player, "msgtoggle.player.temp.off").replace("%player%", target.getName()));
            }, durationMillis / 50);
        }
    }

    private long parseTimeArg(String timeArg) {
        try {
            if (timeArg.endsWith("m")) {
                return TimeUnit.MINUTES.toMillis(Long.parseLong(timeArg.replace("m", "")));
            } else if (timeArg.endsWith("h")) {
                return TimeUnit.HOURS.toMillis(Long.parseLong(timeArg.replace("h", "")));
            } else if (timeArg.endsWith("d")) {
                return TimeUnit.DAYS.toMillis(Long.parseLong(timeArg.replace("d", "")));
            } else {
                return Long.parseLong(timeArg) * 1000; // Sekunden
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isMessagesToggled(Player player) {
        int userId = getUserId(player); // Benutzer-ID abrufen
        return dbm.getBooleanData(String.valueOf(userId), "msg_toggle_all", "messages_toggles");
    }

    private void toggleMessages(Player player, boolean enable) {
        int userId = getUserId(player);
        String toggleName = "msg_toggle_all";

        if (enable) {
            dbm.executePreparedUpdate("INSERT INTO messages_toggles (user_id, toggle_name, is_enabled) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE is_enabled = true", userId, toggleName);
        } else {
            dbm.executePreparedUpdate("UPDATE messages_toggles SET is_enabled = false WHERE user_id = ? AND toggle_name = ?", userId, toggleName);
        }
    }

    private boolean isTargetToggled(Player target) {
        int userId = getUserId(target);
        return dbm.getBooleanData(String.valueOf(userId), "msg_toggle_" + target.getUniqueId(), "messages_toggles");
    }

    private void toggleTargetMessages(Player target, boolean enable) {
        int userId = getUserId(target);
        dbm.executePreparedUpdate("INSERT INTO messages_toggles (user_id, toggle_name, is_enabled) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE is_enabled = ?", userId, "msg_toggle_" + target.getUniqueId(), !enable);
    }

    private int getUserId(Player player) {
        String query = "SELECT id FROM users WHERE uuid = ?";
        try (PreparedStatement stmt = dbm.getConnection().prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error fetching user ID for player " + player.getName() + ": " + e.getMessage());
        }
        return 0;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        } else if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            completions.add("on");
            completions.add("off");
            return completions;
        } else if (args.length == 3 || args.length == 4) {
            List<String> completions = new ArrayList<>();
            completions.add("-t");
            completions.add("7m");
            completions.add("1h");
            completions.add("1d");
            return completions;
        }
        return null;
    }
}
