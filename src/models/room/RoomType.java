package models.room;

/**
 * Enum representing the type of a room.
 */
public enum RoomType {
    CLASSROOM("Classroom"),
    LABORATORY("Laboratory");

    private final String displayName;

    /**
     * Constructs a RoomType with the specified display name.
     *
     * @param displayName the display name of the room type
     */
    RoomType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the room type.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}