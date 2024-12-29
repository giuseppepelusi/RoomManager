package app;

import controllers.ReservationManager;
import controllers.FileManager;
import controllers.AutoSaveManager;
import views.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the look and feel to the system's look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize the reservation manager and file manager
        ReservationManager reservationManager = new ReservationManager();
        FileManager fileManager = new FileManager();

        // Load rooms from the file
        fileManager.loadRooms(reservationManager);

        // Initialize the auto-save manager
        AutoSaveManager autoSaveManager = new AutoSaveManager(reservationManager, fileManager);
        autoSaveManager.startAutoSave();

        // Create and show the main window
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow(reservationManager, fileManager);
            mainWindow.setVisible(true);
        });

        // Add a shutdown hook to stop the auto-save manager
        Runtime.getRuntime().addShutdownHook(new Thread(autoSaveManager::shutdown));
    }
}