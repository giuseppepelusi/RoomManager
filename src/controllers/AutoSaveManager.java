package controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoSaveManager {
    private static final int SAVE_INTERVAL_MINUTES = 1;
    private static final String AUTO_SAVE_FILE = "autosave";
    private final ScheduledExecutorService scheduler;
    private final ReservationManager reservationManager;
    private final FileManager fileManager;

    public AutoSaveManager(ReservationManager reservationManager, FileManager fileManager) {
        this.reservationManager = reservationManager;
        this.fileManager = fileManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startAutoSave() {
        scheduler.scheduleAtFixedRate(
            this::performAutoSave,
            SAVE_INTERVAL_MINUTES,
            SAVE_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        );
    }

    private void performAutoSave() {
        fileManager.saveReservations(AUTO_SAVE_FILE, reservationManager.getAllReservations());
    }

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