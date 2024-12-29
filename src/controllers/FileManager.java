package controllers;

import models.reservation.Reservation;
import models.room.Classroom;
import models.room.Laboratory;
import models.room.Room;
import models.room.RoomType;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class FileManager {
    private static final String DEFAULT_ROOMS_FILE = "config/rooms.txt";
    private static final String DEFAULT_TEMP_FILE = "reservations.temp";

    public void saveReservations(String filename, List<Reservation> reservations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Reservation> loadReservations(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Reservation>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void loadRooms(ReservationManager manager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DEFAULT_ROOMS_FILE))) {
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
                System.out.println("Loaded room: " + room);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void autoSave(List<Reservation> reservations) {
        saveReservations(DEFAULT_TEMP_FILE, reservations);
    }

    public boolean fileExists(String filename) {
        return new File(filename).exists();
    }
}