package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateTimeUtils {
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static List<LocalTime> getAvailableTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime current = OPENING_TIME;
        while (!current.isAfter(CLOSING_TIME)) {
            timeSlots.add(current);
            current = current.plusHours(1);
        }
        return timeSlots;
    }

    public static boolean isWithinBusinessHours(LocalTime time) {
        return !time.isBefore(OPENING_TIME) && !time.isAfter(CLOSING_TIME);
    }

    public static boolean isValidTimeRange(LocalTime start, LocalTime end) {
        return isWithinBusinessHours(start) && 
               isWithinBusinessHours(end) && 
               start.isBefore(end);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static int calculateHoursBetween(LocalTime start, LocalTime end) {
        return end.getHour() - start.getHour();
    }

    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1.equals(date2);
    }

    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    public static LocalTime roundToNearestHour(LocalTime time) {
        return LocalTime.of(time.getHour(), 0);
    }
}