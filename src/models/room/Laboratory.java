package models.room;

public class Laboratory extends Room {
    private final boolean hasPCs;
    private final boolean hasElectricalOutlets;
    private static final int MAX_RESERVATION_HOURS = 4;
    private static final int MIN_RESERVATION_INCREMENT = 2;

    public Laboratory(String name, int capacity, boolean hasPCs, boolean hasElectricalOutlets) {
        super(name, capacity, RoomType.LABORATORY);
        this.hasPCs = hasPCs;
        this.hasElectricalOutlets = hasElectricalOutlets;
    }

    public boolean hasPCs() {
        return hasPCs;
    }

    public boolean hasElectricalOutlets() {
        return hasElectricalOutlets;
    }

    @Override
    public boolean isValidReservationDuration(int hours) {
        return hours >= MIN_RESERVATION_INCREMENT && 
               hours <= MAX_RESERVATION_HOURS && 
               hours % MIN_RESERVATION_INCREMENT == 0;
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