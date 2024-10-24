package de.dervonnebe.aps.utils;

import de.dervonnebe.aps.APSurvival;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

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
            url = setupMySQL();
        } else {
            url = setupSQLite();
        }
    }

    private String setupMySQL() {
        String host = plugin.getConfig().getString("database.host", "localhost");
        int port = plugin.getConfig().getInt("database.port", 3306);
        String database = plugin.getConfig().getString("database.name", "apsurvival");
        String username = plugin.getConfig().getString("database.username", "root");
        String password = plugin.getConfig().getString("database.password", "password123");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            connection = DriverManager.getConnection(url, username, password);
            plugin.log(msg.getMessage("database.connection.success").replace("%type%", "MySQL"));
        } catch (SQLException e) {
            logSQLException(e, "MySQL");
        }
        return url;
    }

    private String setupSQLite() {
        String databasePath = plugin.getDataFolder().getAbsolutePath() + "/data.aps";
        String url = "jdbc:sqlite:" + databasePath;

        try {
            connection = DriverManager.getConnection(url);
            plugin.log(msg.getMessage("database.connection.success").replace("%type%", "SQLite"));
        } catch (SQLException e) {
            logSQLException(e, "SQLite");
        }
        return url;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                plugin.log(msg.getMessage("database.connection.closed"));
            } catch (SQLException e) {
                logSQLException(e, "closing connection");
            }
        }
    }

    public void executeUpdate(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            logSQLException(e, "executing update: " + query);
        }
    }

    public ResultSet executeQuery(String query) {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            logSQLException(e, "executing query: " + query);
            return null;
        }
    }

    public void executePreparedUpdate(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setParameters(stmt, params);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logSQLException(e, "executing prepared update: " + query);
        }
    }

    public ResultSet executePreparedQuery(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setParameters(stmt, params);
            return stmt.executeQuery();
        } catch (SQLException e) {
            logSQLException(e, "executing prepared query: " + query);
            return null;
        }
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    private void logSQLException(SQLException e, String context) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        plugin.log(msg.getMessage("database.query-error")
                .replace("%query%", context)
                .replace("%error%", sw.toString()), "ERROR");
    }
}
