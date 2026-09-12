package com.bestbrightness.pos.db;

import com.bestbrightness.pos.service.PasswordUtil;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
            upsertDefaultAdmin(connection);
        }
    }

    private void upsertDefaultAdmin(Connection connection) throws SQLException {
        String passwordHash = PasswordUtil.hashPassword("admin123");

        try (PreparedStatement updateLegacyPassword = connection.prepareStatement("""
                UPDATE users
                SET password = ?
                WHERE username = 'admin' AND password = 'admin123'
                """);
             PreparedStatement insertAdmin = connection.prepareStatement("""
                     INSERT INTO users (username, password)
                     SELECT 'admin', ?
                     WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
                     """)) {
            updateLegacyPassword.setString(1, passwordHash);
            updateLegacyPassword.executeUpdate();

            insertAdmin.setString(1, passwordHash);
            insertAdmin.executeUpdate();
        }
    }
}
