package de.dervonnebe.aps.setup;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.DatabaseManager;
import de.dervonnebe.aps.utils.Messages;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private final APSurvival plugin;
    private final DatabaseManager databaseManager;
    private Messages msg;

    public DatabaseSetup(APSurvival plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.msg = plugin.getMessages();
    }

    public void setupTables(boolean rebuild) {
        Connection connection = databaseManager.getConnection();
        try (Statement stmt = connection.createStatement()) {
            if (rebuild) {
                stmt.execute("DROP TABLE IF EXISTS messages_toggles");
                stmt.execute("DROP TABLE IF EXISTS users");
                stmt.execute("DROP TABLE IF EXISTS reports");
                stmt.execute("DROP TABLE IF EXISTS bans");
                stmt.execute("DROP TABLE IF EXISTS warns");
                stmt.execute("DROP TABLE IF EXISTS server_stats");
                stmt.execute("DROP TABLE IF EXISTS command_log");
            }

            // Tabelle für Nachrichten-Toggles erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS messages_toggles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "toggle_name TEXT NOT NULL, " +
                    "is_enabled BOOLEAN NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "UNIQUE (user_id, toggle_name) -- Ensure unique toggle per user/target" +
                    ");");

            // Tabelle für Benutzerstatistiken erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "first_join DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "last_join DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "bans INTEGER DEFAULT 0, " +
                    "kicks INTEGER DEFAULT 0, " +
                    "warns INTEGER DEFAULT 0)");

            // Tabelle für Spielerberichte erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS reports (" +
                    "report_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "reported_user_id INTEGER NOT NULL, " +
                    "report_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "description TEXT NOT NULL, " +
                    "FOREIGN KEY (reported_user_id) REFERENCES users(id))");

            // Tabelle für Banns erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS bans (" +
                    "ban_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "ban_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "reason TEXT NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Tabelle für Warns erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS warns (" +
                    "warn_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "warn_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "reason TEXT NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Tabelle für Serverstatistiken erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS server_stats (" +
                    "stat_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "stat_name TEXT NOT NULL, " +
                    "stat_value TEXT NOT NULL)");

            // Tabelle für Befehlsprotokolle erstellen
            stmt.execute("CREATE TABLE IF NOT EXISTS command_log (" +
                    "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "command TEXT NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            plugin.log(msg.getMessage("database.setup.tables.success"));
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            plugin.log(msg.getMessage("database.setup.tables.error").replace("%error%", sw.toString()), "ERROR");
        }
    }
}

