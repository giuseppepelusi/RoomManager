package models.room;

public enum RoomType {
    CLASSROOM("Classroom"),
    LABORATORY("Laboratory");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}