package com.bestbrightness.pos.db;

import com.bestbrightness.pos.service.PasswordUtil;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final String jdbcUrl;

    public DatabaseManager(Path databasePath) {
        this.jdbcUrl = "jdbc:sqlite:" + databasePath.toAbsolutePath();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    public void initializeDatabase() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products (
                        product_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        product_name VARCHAR(150) NOT NULL,
                        price DOUBLE NOT NULL,
                        quantity INTEGER NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sales (
                        sale_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        total DOUBLE NOT NULL,
                        discount DOUBLE NOT NULL,
                        final_total DOUBLE NOT NULL,
                        sale_date DATE NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sale_items (
                        item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        sale_id INTEGER NOT NULL,
                        product_id INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        subtotal DOUBLE NOT NULL,
                        FOREIGN KEY (sale_id) REFERENCES sales (sale_id),
                        FOREIGN KEY (product_id) REFERENCES products (product_id)
                    )
                    """);
        }
    }

    public boolean hasUsers() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM users LIMIT 1");
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next();
        }
    }

    public void createInitialAdmin(String password) throws SQLException {
        createInitialAdmin(password == null ? null : password.toCharArray());
    }

    public void createInitialAdmin(char[] password) throws SQLException {
        if (password == null || password.length == 0 || isBlank(password)) {
            throw new IllegalArgumentException("Admin password is required.");
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO users (username, password)
                     SELECT 'admin', ?
                     WHERE NOT EXISTS (SELECT 1 FROM users)
                     """)) {
            statement.setString(1, PasswordUtil.hashPassword(password));
            statement.executeUpdate();
        }
    }

    private boolean isBlank(char[] value) {
        for (char character : value) {
            if (!Character.isWhitespace(character)) {
                return false;
            }
        }
        return true;
    }
}
