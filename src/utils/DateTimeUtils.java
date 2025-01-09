package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for date and time operations.
 */
public class DateTimeUtils {
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Gets the available time slots within business hours.
     *
     * @return a list of available time slots
     */
    public static List<LocalTime> getAvailableTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime current = OPENING_TIME;
        while (!current.isAfter(CLOSING_TIME)) {
            timeSlots.add(current);
            current = current.plusHours(1);
        }
        return timeSlots;
    }

    /**
     * Checks if a time is within business hours.
     *
     * @param time the time to check
     * @return true if the time is within business hours, false otherwise
     */
    public static boolean isWithinBusinessHours(LocalTime time) {
        return !time.isBefore(OPENING_TIME) && !time.isAfter(CLOSING_TIME);
    }

    /**
     * Validates a time range.
     *
     * @param start the start time
     * @param end the end time
     * @return true if the time range is valid, false otherwise
     */
    public static boolean isValidTimeRange(LocalTime start, LocalTime end) {
        return isWithinBusinessHours(start) && 
               isWithinBusinessHours(end) && 
               start.isBefore(end);
    }

    /**
     * Formats a time to a string.
     *
     * @param time the time to format
     * @return the formatted time string
     */
    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    /**
     * Formats a date to a string.
     *
     * @param date the date to format
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Calculates the number of hours between two times.
     *
     * @param start the start time
     * @param end the end time
     * @return the number of hours between the times
     */
    public static int calculateHoursBetween(LocalTime start, LocalTime end) {
        return end.getHour() - start.getHour();
    }

    /**
     * Checks if two dates are the same.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if the dates are the same, false otherwise
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1.equals(date2);
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date the date to check
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Rounds a time to the nearest hour.
     *
     * @param time the time to round
     * @return the rounded time
     */
    public static LocalTime roundToNearestHour(LocalTime time) {
        return LocalTime.of(time.getHour(), 0);
    }
}