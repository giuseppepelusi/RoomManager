package models.room;

/**
 * Abstract class representing a room.
 */
public abstract class Room {
    private final String name;
    private final int capacity;
    private final RoomType type;

    /**
     * Constructs a Room with the specified details.
     *
     * @param name the name of the room
     * @param capacity the capacity of the room
     * @param type the type of the room
     */
    public Room(String name, int capacity, RoomType type) {
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }

    /**
     * Gets the name of the room.
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the capacity of the room.
     *
     * @return the capacity of the room
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the type of the room.
     *
     * @return the type of the room
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Validates the reservation duration for the room.
     *
     * @param hours the duration in hours
     * @return true if the duration is valid, false otherwise
     */
    public abstract boolean isValidReservationDuration(int hours);
    
    /**
     * Gets the maximum reservation duration for the room.
     *
     * @return the maximum reservation duration in hours
     */
    public abstract int getMaxReservationDuration();
    
    /**
     * Gets the minimum reservation increment for the room.
     *
     * @return the minimum reservation increment in hours
     */
    public abstract int getMinReservationIncrement();

    @Override
    public String toString() {
        return "Room " + name + " (Capacity: " + capacity + ")";
    }
}