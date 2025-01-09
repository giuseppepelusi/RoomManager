package controllers;

import models.reservation.Reservation;
import models.room.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Manages reservations and rooms.
 */
public class ReservationManager {
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    
    private final Map<String, Room> rooms;
    private final List<Room> sortedRooms;
    private final List<Reservation> reservations;

    /**
     * Constructs a ReservationManager.
     */
    public ReservationManager() {
        this.rooms = new HashMap<>();
        this.sortedRooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    /**
     * Adds a room to the manager.
     *
     * @param room the room to add
     */
    public void addRoom(Room room) {
        rooms.put(room.getName(), room);
        sortedRooms.add(room);
        sortedRooms.sort(Comparator.comparing(Room::getName));
    }

    /**
     * Gets a room by name.
     *
     * @param name the name of the room
     * @return the room, or null if not found
     */
    public Room getRoom(String name) {
        return rooms.get(name);
    }

    /**
     * Gets all rooms.
     *
     * @return an unmodifiable collection of all rooms
     */
    public Collection<Room> getAllRooms() {
        return Collections.unmodifiableCollection(sortedRooms);
    }

    /**
     * Adds a reservation.
     *
     * @param reservation the reservation to add
     * @return true if the reservation was added, false otherwise
     */
    public boolean addReservation(Reservation reservation) {
        if (reservation.getStartTime().isBefore(OPENING_TIME) || 
            reservation.getEndTime().isAfter(CLOSING_TIME)) {
            return false;
        }

        int duration = reservation.getDurationHours();
        if (!reservation.getRoom().isValidReservationDuration(duration)) {
            return false;
        }

        for (Reservation existing : reservations) {
            if (existing.overlaps(reservation)) {
                return false;
            }
        }

        return reservations.add(reservation);
    }

    /**
     * Removes a reservation.
     *
     * @param reservation the reservation to remove
     * @return true if the reservation was removed, false otherwise
     */
    public boolean removeReservation(Reservation reservation) {
        return reservations.remove(reservation);
    }

    /**
     * Gets reservations for a specific date.
     *
     * @param date the date
     * @return a list of reservations for the date
     */
    public List<Reservation> getReservationsForDate(LocalDate date) {
        return reservations.stream()
                          .filter(r -> r.getDate().equals(date))
                          .toList();
    }

    /**
     * Gets reservations for a specific room.
     *
     * @param room the room
     * @return a list of reservations for the room
     */
    public List<Reservation> getReservationsForRoom(Room room) {
        return reservations.stream()
                          .filter(r -> r.getRoom().equals(room))
                          .toList();
    }

    /**
     * Gets all reservations.
     *
     * @return an unmodifiable list of all reservations
     */
    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(reservations);
    }

    /**
     * Loads reservations from a list.
     *
     * @param reservations the list of reservations to load
     */
    public void loadReservations(List<Reservation> reservations) {
        this.reservations.clear();
        this.reservations.addAll(reservations);
    }

    /**
     * Loads reservations from a file.
     *
     * @param filename the name of the file
     */
    public void loadReservations(String filename) {
        FileManager fileManager = new FileManager();
        try {
            List<Reservation> loadedReservations = fileManager.loadReservations(filename, this);
            reservations.clear();
            reservations.addAll(loadedReservations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}