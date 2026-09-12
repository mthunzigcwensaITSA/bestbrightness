package com.bestbrightness.pos.service;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private final DatabaseManager databaseManager;

    public AuthService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public User authenticate(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT user_id, username, password
                     FROM users
                     WHERE username = ?
                     """)) {
            statement.setString(1, username.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()
                        || !PasswordUtil.matches(password, resultSet.getString("password"))) {
                    return null;
                }
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"));
            }
        }
    }
}
