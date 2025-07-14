package com.calendar.service.impl;

import com.calendar.exception.EventConflictException;
import com.calendar.exception.EventStorageException;
import com.calendar.model.Event;
import com.calendar.storage.EventStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarServiceImplTest {

    private EventStorage storage;
    private CalendarServiceImpl service;

    @BeforeEach
    void setUp() {
        storage = mock(EventStorage.class);
        service = new CalendarServiceImpl(storage);
    }

    @Test
    void testAddEventSuccess() {
        Event event = new Event("Meeting", epoch("2025-11-01T10:00", "America/New_York"), epoch("2025-11-01T11:00", "America/New_York"));
        when(storage.loadEvents()).thenReturn(Collections.emptyList());

        Event added = service.addEvent(event);
        assertEquals(event, added);
        verify(storage).saveEvents(anyList());
    }

    @Test
    void testAddEventConflict() {
        Event existing = new Event("Call", epoch("2025-11-01T10:00", "America/New_York"), epoch("2025-11-01T11:00", "America/New_York"));
        Event conflict = new Event("Overlap", epoch("2025-11-01T10:30", "America/New_York"), epoch("2025-11-01T11:30", "America/New_York"));
        when(storage.loadEvents()).thenReturn(Collections.singletonList(existing));

        assertThrowsExactly(EventConflictException.class, () -> service.addEvent(conflict));
    }

//    @Test
//    void testAddEventThrowsStorageException() {
//        Event event = new Event("Crash", epoch("2025-11-01T10:00", "America/New_York"), epoch("2025-11-01T11:00", "America/New_York"));
//        when(storage.loadEvents()).thenThrow(new RuntimeException("Boom"));
//
//        assertThrowsExactly(EventStorageException.class, () -> service.addEvent(event));
//    }

    @Test
    void testListEventsForTodayWithDifferentTimeZones() {
        Event event1 = new Event("Cross-Time", epoch("2025-11-01T08:00", "America/Los_Angeles"), epoch("2025-11-01T09:00", "America/Los_Angeles"));
        when(storage.loadEvents()).thenReturn(Collections.singletonList(event1));

        List<Event> events = service.listEventsForToday("America/Chicago");
        ZonedDateTime pacificTime = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
        long startMillis = pacificTime.toInstant().toEpochMilli();
        long endMillis = pacificTime.plusHours(1).toInstant().toEpochMilli();

        Event event2 = new Event("Time Zone Meeting", startMillis, endMillis);
        events.add(event2);
        when(storage.loadEvents()).thenReturn(events);

        List<Event> results = service.listEventsForDay("2025-01-01", "America/Chicago");
        assertEquals(1, results.size());
    }

    @Test
    void testListRemainingEventsForToday() {
        long now = Instant.now().toEpochMilli();
        Event upcoming = new Event("Upcoming", now + 10_000, now + 60_000);
        Event past = new Event("Past", now - 60_000, now - 10_000);
        when(storage.loadEvents()).thenReturn(Arrays.asList(past, upcoming));

        List<Event> remaining = service.listRemainingEventsForToday(ZoneId.systemDefault().getId());
        assertEquals(1, remaining.size());
        assertEquals("Upcoming", remaining.get(0).getTitle());
    }

    @Test
    void testFindNextAvailableSlotSuccess() {
        long base = ZonedDateTime.of(LocalDate.of(2025, 11, 1), LocalTime.of(8, 0), ZoneId.of("America/Los_Angeles")).toInstant().toEpochMilli();
        Event busy = new Event("Busy", base, base + 60 * 60 * 1000);
        when(storage.loadEvents()).thenReturn(Collections.singletonList(busy));

        Map<String, Object> slot = service.findNextAvailableSlot(30, "2025-11-01", "America/Los_Angeles");
        long start = (long) slot.get("startEpochMillis");
        long end = (long) slot.get("endEpochMillis");

        long slotDuration = end - start;
        assertEquals(Duration.ofMinutes(30).toMillis(), slotDuration);

        for (Event e : storage.loadEvents()) {
            assertTrue(end <= e.getStartEpochMillis() || start >= e.getEndEpochMillis(),
                    "Found slot overlaps with an existing event");
        }
        assertEquals(30 * 60 * 1000, end - start);
    }

    @Test
    void testFindNextAvailableSlotNoRoom() {
        List<Event> events = new ArrayList<>();
        ZonedDateTime start = ZonedDateTime.of(LocalDate.of(2025, 11, 1), LocalTime.of(0, 0), ZoneId.of("America/Los_Angeles"));
        for (int i = 0; i < 24; i++) {
            long s = start.plusHours(i).toInstant().toEpochMilli();
            events.add(new Event("Hour " + i, s, s + 59 * 60 * 1000));
        }
        when(storage.loadEvents()).thenReturn(events);

        Map<String, Object> result = service.findNextAvailableSlot(30, "2025-11-01", "America/Los_Angeles");
        assertEquals("No available slot found.", result.get("message"));
    }

    @Test
    void testDSTTransitionOverlapHour() {
        ZoneId zone = ZoneId.of("America/New_York");
        ZonedDateTime start = ZonedDateTime.of(2025, 11, 2, 1, 30, 0, 0, zone);
        ZonedDateTime end = start.plusMinutes(30);
        Event event = new Event("DST Test", start.toInstant().toEpochMilli(), end.toInstant().toEpochMilli());

        when(storage.loadEvents()).thenReturn(Collections.singletonList(event));
        List<Event> results = service.listEventsForDay("2025-11-02", "America/New_York");
        assertEquals(1, results.size());
    }

    private long epoch(String datetime, String zoneId) {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.parse(datetime), ZoneId.of(zoneId));
        return zdt.toInstant().toEpochMilli();
    }
}
