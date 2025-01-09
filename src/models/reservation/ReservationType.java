package models.reservation;

/**
 * Enum representing the type of a reservation.
 */
public enum ReservationType {
    LESSON("Lesson"),
    EXAM("Exam"),
    CATCH_UP("Catch-up"),
    OTHER("Other");

    private final String displayName;

    /**
     * Constructs a ReservationType with the specified display name.
     *
     * @param displayName the display name of the reservation type
     */
    ReservationType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the reservation type.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}