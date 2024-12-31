package controllers;

import models.reservation.Reservation;
import models.room.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ReservationManager {
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    
    private final Map<String, Room> rooms;
    private final List<Room> sortedRooms;
    private final List<Reservation> reservations;

    public ReservationManager() {
        this.rooms = new HashMap<>();
        this.sortedRooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    public void addRoom(Room room) {
        rooms.put(room.getName(), room);
        sortedRooms.add(room);
        sortedRooms.sort(Comparator.comparing(Room::getName));
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public Collection<Room> getAllRooms() {
        return Collections.unmodifiableCollection(sortedRooms);
    }

    public boolean addReservation(Reservation reservation) {
        // Validate reservation times
        if (reservation.getStartTime().isBefore(OPENING_TIME) || 
            reservation.getEndTime().isAfter(CLOSING_TIME)) {
            return false;
        }

        // Validate duration
        int duration = reservation.getDurationHours();
        if (!reservation.getRoom().isValidReservationDuration(duration)) {
            return false;
        }

        // Check for conflicts
        for (Reservation existing : reservations) {
            if (existing.overlaps(reservation)) {
                return false;
            }
        }

        return reservations.add(reservation);
    }

    public boolean removeReservation(Reservation reservation) {
        return reservations.remove(reservation);
    }

    public List<Reservation> getReservationsForDate(LocalDate date) {
        return reservations.stream()
                          .filter(r -> r.getDate().equals(date))
                          .toList();
    }

    public List<Reservation> getReservationsForRoom(Room room) {
        return reservations.stream()
                          .filter(r -> r.getRoom().equals(room))
                          .toList();
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(reservations);
    }

    public void loadReservations(List<Reservation> reservations) {
        this.reservations.clear();
        this.reservations.addAll(reservations);
    }

    public void loadReservations(String filename) {
        FileManager fileManager = new FileManager();
        try {
            List<Reservation> loadedReservations = fileManager.loadReservations(filename);
            reservations.clear();
            reservations.addAll(loadedReservations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}