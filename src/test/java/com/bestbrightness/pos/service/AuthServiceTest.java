package com.bestbrightness.pos.service;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.model.User;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager databaseManager = new DatabaseManager(tempDir.resolve("auth.db"));
        databaseManager.initializeDatabase();
        authService = new AuthService(databaseManager);
    }

    @Test
    void authenticatesTheDefaultAdminUser() throws Exception {
        User user = authService.authenticate("admin", "admin123");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void authenticatesWithTrimmedUsername() throws Exception {
        User user = authService.authenticate("  admin  ", "admin123");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void rejectsInvalidCredentials() throws Exception {
        assertNull(authService.authenticate("admin", "wrong-password"));
    }
}
