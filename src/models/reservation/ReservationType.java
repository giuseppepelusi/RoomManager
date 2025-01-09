package models.reservation;

import java.io.Serializable;

public enum ReservationType implements Serializable {
    LESSON("Lesson"),
    EXAM("Exam"),
    CATCH_UP("Catch-up"),
    OTHER("Other");

    private final String displayName;

    ReservationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}