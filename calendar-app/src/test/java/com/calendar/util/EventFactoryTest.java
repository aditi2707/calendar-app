package com.calendar.util;

import com.calendar.dto.EventRequest;
import com.calendar.exception.ValidationException;
import com.calendar.model.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventFactoryTest {

    @Test
    public void testFromValues_Success() {
        Event event = EventFactory.fromValues("Meeting", 1000L, 2000L);
        assertEquals("Meeting", event.getTitle());
        assertEquals(1000L, event.getStartEpochMillis());
        assertEquals(2000L, event.getEndEpochMillis());
    }

    @Test
    public void testFromValues_NullTitle() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> EventFactory.fromValues(null, 1000L, 2000L));
        assertEquals("Event title must not be empty.", ex.getMessage());
    }

    @Test
    public void testFromValues_EmptyTitle() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> EventFactory.fromValues("   ", 1000L, 2000L));
        assertEquals("Event title must not be empty.", ex.getMessage());
    }

    @Test
    public void testFromValues_NonPositiveStartTime() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> EventFactory.fromValues("Test", 0L, 2000L));
        assertEquals("Start and end times must be provided.", ex.getMessage());
    }

    @Test
    public void testFromValues_NonPositiveEndTime() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> EventFactory.fromValues("Test", 1000L, 0L));
        assertEquals("Start and end times must be provided.", ex.getMessage());
    }

    @Test
    public void testFromValues_EndBeforeStart() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> EventFactory.fromValues("Test", 3000L, 2000L));
        assertEquals("End time must be after start time.", ex.getMessage());
    }

    @Test
    public void testFromJson_Success() {
        String json = "{\n" +
                "  \"title\": \"Sample Event\",\n" +
                "  \"startEpochMillis\": 1000,\n" +
                "  \"endEpochMillis\": 2000\n" +
                "}";
        Event event = EventFactory.fromJson(json);
        assertEquals("Sample Event", event.getTitle());
        assertEquals(1000L, event.getStartEpochMillis());
        assertEquals(2000L, event.getEndEpochMillis());
    }

    @Test
    public void testFromJson_InvalidJson() {
        String invalidJson = "{ not valid json }";
        assertThrowsExactly(com.calendar.exception.JsonProcessingException.class,
                () -> EventFactory.fromJson(invalidJson));
    }

    @Test
    public void testFromJson_InvalidEventFields() {
        String invalidJson = "{\n" +
                "  \"title\": \"\",\n" +
                "  \"startEpochMillis\": 1000,\n" +
                "  \"endEpochMillis\": 2000\n" +
                "}";
        ValidationException ex = assertThrowsExactly(ValidationException.class,
                () -> EventFactory.fromJson(invalidJson));
        assertEquals("Event title must not be empty.", ex.getMessage());
    }

    @Test
    void testFromRequest_validInput_shouldCreateEvent() {
        EventRequest request = new EventRequest();
        request.setTitle("Team Meeting");
        request.setStartDate("2025-07-14");
        request.setStartTime("09:00");
        request.setEndDate("2025-07-14");
        request.setEndTime("10:00");
        request.setZone("America/New_York");

        Event event = EventFactory.fromRequest(request);

        assertEquals("Team Meeting", event.getTitle());

        ZonedDateTime start = ZonedDateTime.of(
                LocalDate.parse("2025-07-14"),
                LocalTime.parse("09:00"),
                ZoneId.of("America/New_York")
        );
        assertEquals(start.toInstant().toEpochMilli(), event.getStartEpochMillis());
    }

    @Test
    void testFromRequest_nullRequest_shouldThrow() {
        assertThrows(ValidationException.class, () -> EventFactory.fromRequest(null));
    }

    @Test
    void testFromRequest_missingTitle_shouldThrow() {
        EventRequest request = new EventRequest();
        request.setStartDate("2025-07-14");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setZone("America/Los_Angeles");

        assertThrowsExactly(ValidationException.class, () -> EventFactory.fromRequest(request));
    }

    @Test
    void testFromRequest_invalidZone_shouldThrow() {
        EventRequest request = new EventRequest();
        request.setTitle("Call");
        request.setStartDate("2025-07-14");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setZone("Invalid/Zone");

        assertThrowsExactly(ValidationException.class, () -> EventFactory.fromRequest(request));
    }

    @Test
    void testFromRequest_endBeforeStart_shouldThrow() {
        EventRequest request = new EventRequest();
        request.setTitle("Early Call");
        request.setStartDate("2025-07-14");
        request.setStartTime("10:00");
        request.setEndTime("09:00");
        request.setZone("America/Chicago");

        assertThrowsExactly(ValidationException.class, () -> EventFactory.fromRequest(request));
    }

    @Test
    void testFromRequest_missingFields_shouldThrow() {
        EventRequest request = new EventRequest();
        request.setTitle("Incomplete Event");

        assertThrowsExactly(ValidationException.class, () -> EventFactory.fromRequest(request));
    }

    @Test
    void testFromRequest_invalidDateOrTimeFormat_shouldThrow() {
        EventRequest request = new EventRequest();
        request.setTitle("Bad Date Format");
        request.setStartDate("14-07-2025");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setZone("UTC");

        assertThrowsExactly(ValidationException.class, () -> EventFactory.fromRequest(request));
    }
}
