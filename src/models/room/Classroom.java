package models.room;

public class Classroom extends Room {
    private final boolean hasWhiteboard;
    private final boolean hasProjector;
    private static final int MAX_RESERVATION_HOURS = 8;
    private static final int MIN_RESERVATION_INCREMENT = 1;

    public Classroom(String name, int capacity, boolean hasWhiteboard, boolean hasProjector) {
        super(name, capacity, RoomType.CLASSROOM);
        this.hasWhiteboard = hasWhiteboard;
        this.hasProjector = hasProjector;
    }

    public boolean hasWhiteboard() {
        return hasWhiteboard;
    }

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