package models.reservation;

import models.room.Room;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a reservation for a room.
 */
public class Reservation {

    private Room room;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reservedBy;
    private ReservationType type;

    /**
     * Default constructor.
     */
    public Reservation() {}

    /**
     * Constructs a Reservation with the specified details.
     *
     * @param room the room being reserved
     * @param date the date of the reservation
     * @param startTime the start time of the reservation
     * @param endTime the end time of the reservation
     * @param reservedBy the name of the person who reserved the room
     * @param type the type of the reservation
     */
    public Reservation(Room room, LocalDate date, LocalTime startTime, LocalTime endTime, String reservedBy, ReservationType type) {
        this.room = room;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservedBy = reservedBy;
        this.type = type;
    }

    // Getters
    public Room getRoom() { return room; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getReservedBy() { return reservedBy; }
    public ReservationType getType() { return type; }

    // Setters for mutable properties
    public void setRoom(Room room) { this.room = room; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setReservedBy(String reservedBy) { this.reservedBy = reservedBy; }
    public void setType(ReservationType type) { this.type = type; }

    /**
     * Gets the duration of the reservation in hours.
     *
     * @return the duration in hours
     */
    public int getDurationHours() {
        return endTime.getHour() - startTime.getHour();
    }

    /**
     * Checks if this reservation overlaps with another reservation.
     *
     * @param other the other reservation
     * @return true if the reservations overlap, false otherwise
     */
    public boolean overlaps(Reservation other) {
        if (!this.date.equals(other.date) || !this.room.equals(other.room)) {
            return false;
        }
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime) || this.endTime.equals(other.startTime));
    }
    
    @Override
    public String toString() {
        return String.format("Reservation: %s - %s to %s by %s for %s", 
            date, startTime, endTime, reservedBy, type.getDisplayName());
    }
}