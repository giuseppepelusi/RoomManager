package models.reservation;

import models.room.Room;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Room room;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reservedBy;
    private ReservationType type;

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
    public void setDate(LocalDate date) { this.date = date; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setReservedBy(String reservedBy) { this.reservedBy = reservedBy; }
    public void setType(ReservationType type) { this.type = type; }

    public int getDurationHours() {
        return endTime.getHour() - startTime.getHour();
    }

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