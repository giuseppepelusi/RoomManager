package controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the auto-saving of reservations at regular intervals.
 */
public class AutoSaveManager {
    private static final int SAVE_INTERVAL_MINUTES = 1;
    private static final String AUTO_SAVE_FILE = "autosave";
    private final ScheduledExecutorService scheduler;
    private final ReservationManager reservationManager;
    private final FileManager fileManager;

    /**
     * Constructs an AutoSaveManager.
     *
     * @param reservationManager the reservation manager
     * @param fileManager the file manager
     */
    public AutoSaveManager(ReservationManager reservationManager, FileManager fileManager) {
        this.reservationManager = reservationManager;
        this.fileManager = fileManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Starts the auto-save process.
     */
    public void startAutoSave() {
        scheduler.scheduleAtFixedRate(
            this::performAutoSave,
            SAVE_INTERVAL_MINUTES,
            SAVE_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
    }

    /**
     * Performs the auto-save operation.
     */
    private void performAutoSave() {
        fileManager.saveReservations(AUTO_SAVE_FILE, reservationManager.getAllReservations());
    }

    /**
     * Shuts down the auto-save process.
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}