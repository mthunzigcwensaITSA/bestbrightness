package com.bestbrightness.pos;

import com.bestbrightness.pos.db.DatabaseManager;
import com.bestbrightness.pos.service.AuthService;
import com.bestbrightness.pos.service.PosService;
import com.bestbrightness.pos.ui.LoginFrame;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import javax.swing.SwingUtilities;

public final class BestBrightnessApp {

    private BestBrightnessApp() {
    }

    public static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager(Path.of("bestbrightness.db"));
            databaseManager.initializeDatabase();
            AuthService authService = new AuthService(databaseManager);
            PosService posService = new PosService(databaseManager);

            if (GraphicsEnvironment.isHeadless()) {
                System.out.println("Best Brightness POS database initialized.");
                return;
            }

            SwingUtilities.invokeLater(() -> new LoginFrame(authService, posService).setVisible(true));
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }
}
