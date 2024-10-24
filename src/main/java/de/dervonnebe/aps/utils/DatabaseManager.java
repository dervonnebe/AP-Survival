package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final APSurvival plugin;
    private Connection connection;
    private Messages msg;

    public DatabaseManager(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    public void setup() {
        String type = plugin.getConfig().getString("database.type", "sqlite");
        String url;

        if (type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb")) {
            String host = plugin.getConfig().getString("database.host", "localhost");
            int port = plugin.getConfig().getInt("database.port", 3306);
            String database = plugin.getConfig().getString("database.name", "apsurvival");
            String username = plugin.getConfig().getString("database.username", "root");
            String password = plugin.getConfig().getString("database.password", "password123");
            url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            try {
                connection = DriverManager.getConnection(url, username, password);
                plugin.log(msg.getMessage("database.connection.success").replace("%type%", "MySQL"));
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.log(msg.getMessage("database.connection.error").replace("%type%", "MySQL").replace("%error%", e.getMessage()), "ERROR");
            }
        } else {
            String databasePath = plugin.getDataFolder().getAbsolutePath() + "/data.aps";
            url = "jdbc:sqlite:" + databasePath;
            try {
                connection = DriverManager.getConnection(url);
                plugin.log(msg.getMessage("database.connection.success").replace("%type%", "SQLite"));
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.log(msg.getMessage("database.connection.error").replace("%type%", "SQLite").replace("%error%", e.getMessage()), "ERROR");
            }
        }
    }

    // Schließen der Datenbankverbindung
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                plugin.log(msg.getMessage("database.connection.closed"));
            } catch (SQLException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                plugin.log(msg.getMessage("database.connection.close-error").replace("%error%", sw.toString()), "ERROR");
            }
        }
    }

    // Ausführen von SQL-Befehlen ohne Rückgabewert (z.B. INSERT, UPDATE, DELETE)
    public void executeUpdate(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            plugin.log(msg.getMessage("database.query-error").replace("%query%", query).replace("%error%", sw.toString()), "ERROR");
        }
    }

    // Ausführen von SQL-Befehlen mit Rückgabewert (z.B. SELECT)
    public ResultSet executeQuery(String query) {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            plugin.log(msg.getMessage("database.query-error").replace("%query%", query).replace("%error%", sw.toString()), "ERROR");
            return null;
        }
    }

    // Beispiel für Prepared Statements
    public void executePreparedUpdate(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            plugin.log(msg.getMessage("database.query-error").replace("%query%", query).replace("%error%", sw.toString()), "ERROR");
        }
    }

    public ResultSet executePreparedQuery(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            plugin.log(msg.getMessage("database.query-error").replace("%query%", query).replace("%error%", sw.toString()), "ERROR");
            return null;
        }
    }
}
