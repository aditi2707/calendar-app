package com.calendar.util;

import com.calendar.exception.ValidationException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDate toLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Missing required date parameter.");
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date " + dateStr);
        }
    }
    public static ZoneId getZone(String zone) {
        if (zone == null || zone.trim().isEmpty()) {
            return ZoneId.systemDefault();
        }

        try {
            return ZoneId.of(zone);
        } catch (DateTimeException e) {
            throw new ValidationException("Invalid timezone: " + zone);
        }
    }

    public static String formatInstant(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public static long toEpochMillis(String dateTimeStr, String zone) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new ValidationException("Missing required 'date' parameter.");
        }
        ZoneId zoneId = getZone(zone);
        try {
            ZonedDateTime zonedDateTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMAT).atZone(zoneId);
            return zonedDateTime.toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date " + dateTimeStr);
        } catch (ZoneRulesException e) {
            throw new ValidationException("Invalid time zone " + zone);
        }
    }

}
