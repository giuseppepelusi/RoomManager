package models.room;

/**
 * Represents a classroom.
 */
public class Classroom extends Room {
    private final boolean hasWhiteboard;
    private final boolean hasProjector;
    private static final int MAX_RESERVATION_HOURS = 8;
    private static final int MIN_RESERVATION_INCREMENT = 1;

    /**
     * Constructs a Classroom with the specified details.
     *
     * @param name the name of the classroom
     * @param capacity the capacity of the classroom
     * @param hasWhiteboard whether the classroom has a whiteboard
     * @param hasProjector whether the classroom has a projector
     */
    public Classroom(String name, int capacity, boolean hasWhiteboard, boolean hasProjector) {
        super(name, capacity, RoomType.CLASSROOM);
        this.hasWhiteboard = hasWhiteboard;
        this.hasProjector = hasProjector;
    }

    /**
     * Checks if the classroom has a whiteboard.
     *
     * @return true if the classroom has a whiteboard, false otherwise
     */
    public boolean hasWhiteboard() {
        return hasWhiteboard;
    }

    /**
     * Checks if the classroom has a projector.
     *
     * @return true if the classroom has a projector, false otherwise
     */
    public boolean hasProjector() {
        return hasProjector;
    }

    @Override
    public boolean isValidReservationDuration(int hours) {
        return hours >= MIN_RESERVATION_INCREMENT && hours <= MAX_RESERVATION_HOURS;
    }

    @Override
    public int getMaxReservationDuration() {
        return MAX_RESERVATION_HOURS;
    }

    @Override
    public int getMinReservationIncrement() {
        return MIN_RESERVATION_INCREMENT;
    }
}