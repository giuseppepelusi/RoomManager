package models.room;

/**
 * Represents a laboratory.
 */
public class Laboratory extends Room {
    private final boolean hasPCs;
    private final boolean hasElectricalOutlets;
    private static final int MAX_RESERVATION_HOURS = 4;
    private static final int MIN_RESERVATION_INCREMENT = 2;

    /**
     * Constructs a Laboratory with the specified details.
     *
     * @param name the name of the laboratory
     * @param capacity the capacity of the laboratory
     * @param hasPCs whether the laboratory has PCs
     * @param hasElectricalOutlets whether the laboratory has electrical outlets
     */
    public Laboratory(String name, int capacity, boolean hasPCs, boolean hasElectricalOutlets) {
        super(name, capacity, RoomType.LABORATORY);
        this.hasPCs = hasPCs;
        this.hasElectricalOutlets = hasElectricalOutlets;
    }

    /**
     * Checks if the laboratory has PCs.
     *
     * @return true if the laboratory has PCs, false otherwise
     */
    public boolean hasPCs() {
        return hasPCs;
    }

    /**
     * Checks if the laboratory has electrical outlets.
     *
     * @return true if the laboratory has electrical outlets, false otherwise
     */
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