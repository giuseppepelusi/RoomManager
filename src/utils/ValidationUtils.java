package utils;

import models.reservation.Reservation;
import models.room.Room;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Utility class for validation operations.
 */
public class ValidationUtils {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Represents the result of a validation operation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        /**
         * Constructs a ValidationResult.
         *
         * @param valid whether the validation was successful
         * @param message the validation message
         */
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        /**
         * Checks if the validation was successful.
         *
         * @return true if the validation was successful, false otherwise
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Gets the validation message.
         *
         * @return the validation message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Creates a successful ValidationResult.
         *
         * @return the successful ValidationResult
         */
        public static ValidationResult success() {
            return new ValidationResult(true, "");
        }

        /**
         * Creates a failed ValidationResult with the specified message.
         *
         * @param message the failure message
         * @return the failed ValidationResult
         */
        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
    }

    /**
     * Validates the reservation time for a room.
     *
     * @param room the room
     * @param startTime the start time
     * @param endTime the end time
     * @param date the date
     * @return the validation result
     */
    public static ValidationResult validateReservationTime(Room room, LocalTime startTime, 
                                                         LocalTime endTime, LocalDate date) {
        if (!DateTimeUtils.isWithinBusinessHours(startTime) || 
            !DateTimeUtils.isWithinBusinessHours(endTime)) {
            return ValidationResult.failure("Reservation must be within business hours (8:00-18:00)");
        }

        if (!startTime.isBefore(endTime)) {
            return ValidationResult.failure("Start time must be before end time");
        }

        int duration = DateTimeUtils.calculateHoursBetween(startTime, endTime);
        if (!room.isValidReservationDuration(duration)) {
            return ValidationResult.failure(
                String.format("Invalid duration for this room type. Min: %d, Max: %d, Increment: %d",
                    room.getMinReservationIncrement(),
                    room.getMaxReservationDuration(),
                    room.getMinReservationIncrement())
            );
        }

        if (DateTimeUtils.isPastDate(date)) {
            return ValidationResult.failure("Cannot make reservations for past dates");
        }

        return ValidationResult.success();
    }

    /**
     * Validates the name of the person who reserved the room.
     *
     * @param name the name
     * @return the validation result
     */
    public static ValidationResult validateReservedBy(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.failure("Name is required");
        }

        name = name.trim();
        if (name.length() < MIN_NAME_LENGTH) {
            return ValidationResult.failure(
                String.format("Name must be at least %d characters long", MIN_NAME_LENGTH));
        }

        if (name.length() > MAX_NAME_LENGTH) {
            return ValidationResult.failure(
                String.format("Name must not exceed %d characters", MAX_NAME_LENGTH));
        }

        if (!name.matches("^[a-zA-Z0-9\\s.-]+$")) {
            return ValidationResult.failure(
                "Name can only contain letters, numbers, spaces, dots, and hyphens");
        }

        return ValidationResult.success();
    }

    /**
     * Validates that there are no conflicts with existing reservations.
     *
     * @param newReservation the new reservation
     * @param existingReservations the list of existing reservations
     * @param reservationBeingEdited the reservation being edited (if any)
     * @return the validation result
     */
    public static ValidationResult validateNoConflict(Reservation newReservation, List<Reservation> existingReservations, Reservation reservationBeingEdited) {
        boolean hasConflict = existingReservations.stream()
                .filter(r -> r.getRoom().equals(newReservation.getRoom()))
                .filter(r -> r.getDate().equals(newReservation.getDate()))
                .filter(r -> !r.equals(reservationBeingEdited)) // Exclude the reservation being edited
                .anyMatch(r -> r.overlaps(newReservation) && !r.getEndTime().equals(newReservation.getStartTime()) && !r.getStartTime().equals(newReservation.getEndTime()));

        if (hasConflict) {
            return ValidationResult.failure("This time slot conflicts with an existing reservation");
        }

        return ValidationResult.success();
    }

    /**
     * Validates the room and its capacity.
     *
     * @param room the room
     * @param requiredCapacity the required capacity
     * @return the validation result
     */
    public static ValidationResult validateRoom(Room room, int requiredCapacity) {
        if (room == null) {
            return ValidationResult.failure("Room must be selected");
        }

        if (room.getCapacity() < requiredCapacity) {
            return ValidationResult.failure(
                String.format("Room capacity (%d) is less than required (%d)", 
                    room.getCapacity(), requiredCapacity));
        }

        return ValidationResult.success();
    }
}