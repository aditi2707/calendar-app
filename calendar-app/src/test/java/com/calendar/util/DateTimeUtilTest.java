package com.calendar.util;

import com.calendar.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeUtilTest {

    @Test
    public void testToLocalDate_Success() {
        LocalDate result = DateTimeUtil.toLocalDate("2025-07-12");
        assertEquals(LocalDate.of(2025, 7, 12), result);
    }

    @Test
    public void testToLocalDate_NullInput() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.toLocalDate(null));
    }

    @Test
    public void testToLocalDate_EmptyInput() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.toLocalDate("   "));
    }

    @Test
    public void testToLocalDate_InvalidFormat() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.toLocalDate("12/07/2025"));
    }

    @Test
    public void testGetZone_ValidZone() {
        ZoneId zone = DateTimeUtil.getZone("America/New_York");
        assertEquals(ZoneId.of("America/New_York"), zone);
    }

    @Test
    public void testGetZone_NullInput() {
        ZoneId expected = ZoneId.systemDefault();
        assertEquals(expected, DateTimeUtil.getZone(null));
    }

    @Test
    public void testGetZone_EmptyInput() {
        ZoneId expected = ZoneId.systemDefault();
        assertEquals(expected, DateTimeUtil.getZone(" "));
    }

    @Test
    public void testGetZone_InvalidZone() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.getZone("Invalid/Zone"));
    }

    @Test
    public void testFormatInstant_Success() {
        Instant instant = Instant.parse("2025-07-12T10:15:30Z");
        String formatted = DateTimeUtil.formatInstant(instant);
        assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
    }

    @Test
    public void testToEpochMillis_ValidInput_WithZone() {
        long millis = DateTimeUtil.toEpochMillis("2025-07-12 10:15", "UTC");
        ZonedDateTime expected = ZonedDateTime.of(2025, 7, 12, 10, 15, 0, 0, ZoneId.of("UTC"));
        assertEquals(expected.toInstant().toEpochMilli(), millis);
    }

    @Test
    public void testToEpochMillis_ValidInput_EmptyZone() {
        long millis = DateTimeUtil.toEpochMillis("2025-07-12 10:15", " ");
        ZonedDateTime expected = ZonedDateTime.of(2025, 7, 12, 10, 15, 0, 0, ZoneId.systemDefault());
        assertEquals(expected.toInstant().toEpochMilli(), millis);
    }

    @Test
    public void testToEpochMillis_InvalidDateTime() {
        assertThrows(ValidationException.class, () -> DateTimeUtil.toEpochMillis("invalid-date", "UTC"));
    }

    @Test
    public void testToEpochMillis_NullInput() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.toEpochMillis(null, "UTC"));
    }

    @Test
    public void testToEpochMillis_EmptyInput() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.toEpochMillis("  ", "UTC"));
    }

    @Test
    public void testToEpochMillis_InvalidZone() {
        assertThrowsExactly(ValidationException.class, () -> DateTimeUtil.toEpochMillis("2025-07-12 10:15", "Invalid/Zone"));
    }
}
