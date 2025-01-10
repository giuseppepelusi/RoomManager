package controllers;

import models.reservation.Reservation;
import models.reservation.ReservationType;
import models.room.Classroom;
import models.room.Laboratory;
import models.room.Room;
import models.room.RoomType;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages file operations for saving and loading reservations and rooms.
 */
public class FileManager {
    private static final String DEFAULT_ROOMS_FILE = "config/rooms.txt";
    private static final String RESERVATION_FILE_EXTENSION = ".resv";

    /**
     * Saves reservations to a file.
     *
     * @param filename the name of the file
     * @param reservations the list of reservations to save
     */
    public void saveReservations(String filename, List<Reservation> reservations) {
        if (!filename.endsWith(RESERVATION_FILE_EXTENSION)) {
            filename += RESERVATION_FILE_EXTENSION;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Reservation reservation : reservations) {
                writer.write("RESERVATION\n");
                writer.write("room=" + reservation.getRoom().getName() + "\n");
                writer.write("date=" + reservation.getDate() + "\n");
                writer.write("startTime=" + reservation.getStartTime() + "\n");
                writer.write("endTime=" + reservation.getEndTime() + "\n");
                writer.write("reservedBy=" + reservation.getReservedBy() + "\n");
                writer.write("type=" + reservation.getType() + "\n");
                writer.write("END\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads reservations from a file.
     *
     * @param filename the name of the file
     * @param reservationManager the reservation manager
     * @return the list of loaded reservations
     */
    public List<Reservation> loadReservations(String filename, ReservationManager reservationManager) {
        if (!filename.endsWith(RESERVATION_FILE_EXTENSION)) {
            filename += RESERVATION_FILE_EXTENSION;
        }
        List<Reservation> reservations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Reservation reservation = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals("RESERVATION")) {
                    reservation = new Reservation();
                } else if (line.equals("END")) {
                    if (reservation != null) {
                        reservations.add(reservation);
                    }
                } else if (reservation != null) {
                    String[] parts = line.split("=");
                    switch (parts[0]) {
                        case "room":
                            Room room = reservationManager.getRoom(parts[1]);
                            reservation.setRoom(room);
                            break;
                        case "date":
                            reservation.setDate(LocalDate.parse(parts[1]));
                            break;
                        case "startTime":
                            reservation.setStartTime(LocalTime.parse(parts[1]));
                            break;
                        case "endTime":
                            reservation.setEndTime(LocalTime.parse(parts[1]));
                            break;
                        case "reservedBy":
                            reservation.setReservedBy(parts[1]);
                            break;
                        case "type":
                            reservation.setType(ReservationType.valueOf(parts[1]));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Loads rooms from the default configuration file.
     *
     * @param manager the reservation manager
     */
    public void loadRooms(ReservationManager manager) {
        BufferedReader reader = null;
        try {
            // Try external file (config/rooms.txt)
            File externalFile = new File(DEFAULT_ROOMS_FILE);
            if (externalFile.exists()) {
                reader = new BufferedReader(new FileReader(externalFile));
                System.out.println("Loaded external rooms file.");
            } else {
                // Fallback to internal file (packaged in JAR)
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_ROOMS_FILE);
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    System.out.println("Loaded internal rooms from JAR.");
                } else {
                    System.out.println("Rooms file not found!");
                }
            }

            // Reading the file if loaded
            if (reader != null) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String name = parts[0];
                    RoomType type = RoomType.valueOf(parts[1].toUpperCase());
                    int capacity = Integer.parseInt(parts[2]);
                    boolean feature1 = Boolean.parseBoolean(parts[3]);
                    boolean feature2 = Boolean.parseBoolean(parts[4]);

                    Room room;
                    if (type == RoomType.CLASSROOM) {
                        room = new Classroom(name, capacity, feature1, feature2);
                    } else {
                        room = new Laboratory(name, capacity, feature1, feature2);
                    }
                    manager.addRoom(room);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a file exists.
     *
     * @param filename the name of the file
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String filename) {
        return new File(filename).exists();
    }
}