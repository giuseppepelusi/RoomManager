package models.room;

public abstract class Room {
    private final String name;
    private final int capacity;
    private final RoomType type;

    public Room(String name, int capacity, RoomType type) {
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public RoomType getType() {
        return type;
    }

    // Abstract method for validating reservation duration
    public abstract boolean isValidReservationDuration(int hours);
    
    // Abstract method for getting maximum reservation duration
    public abstract int getMaxReservationDuration();
    
    // Abstract method for getting minimum reservation increment
    public abstract int getMinReservationIncrement();

    @Override
    public String toString() {
        return "Room " + name + " (Capacity: " + capacity + ")";
    }
}