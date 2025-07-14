package com.calendar.service.impl;

import com.calendar.exception.EventConflictException;
import com.calendar.exception.EventStorageException;
import com.calendar.exception.ValidationException;
import com.calendar.model.Event;
import com.calendar.service.CalendarService;
import com.calendar.storage.EventStorage;
import com.calendar.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


public class CalendarServiceImpl implements CalendarService {

    private final EventStorage storage;

    private static final Logger log = LoggerFactory.getLogger(CalendarServiceImpl.class);

    public CalendarServiceImpl(EventStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<Event> listEventsForToday(String zoneName)
            throws ValidationException, EventStorageException {
        ZoneId zone = DateTimeUtil.getZone(zoneName);
        LocalDate today = LocalDate.now(zone);
        return getEventsForDay(today, zone);
    }

    @Override
    public List<Event> listRemainingEventsForToday(String zoneName)
            throws ValidationException, EventStorageException {
        ZoneId zone = DateTimeUtil.getZone(zoneName);
        ZonedDateTime now = ZonedDateTime.now(zone);
        List<Event> events = listEventsForToday(zoneName);
        List<Event> remaining = new ArrayList<>();
        for (Event e : events) {
            if (e.getEndInstant().isAfter(now.toInstant())) {
                remaining.add(e);
            }
        }
        return remaining;
    }

    @Override
    public List<Event> listEventsForDay(String date, String zoneName)
            throws ValidationException, EventStorageException {
        LocalDate parsedDate = DateTimeUtil.toLocalDate(date);
        ZoneId zone = DateTimeUtil.getZone(zoneName);
        return getEventsForDay(parsedDate, zone);
    }

    private List<Event> getEventsForDay(LocalDate day, ZoneId zone) {
        ZonedDateTime startOfDay = day.atStartOfDay(zone);
        ZonedDateTime endOfDay = day.plusDays(1).atStartOfDay(zone);
        long startMillis = startOfDay.toInstant().toEpochMilli();
        long endMillis = endOfDay.toInstant().toEpochMilli();

        List<Event> result = new ArrayList<>();
        for (Event e : storage.loadEvents()) {
            if (e.getStartEpochMillis() < endMillis && e.getEndEpochMillis() > startMillis) {
                result.add(e);
            }
        }
        result.sort(new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return Long.compare(e1.getStartEpochMillis(), e2.getStartEpochMillis());
            }
        });
        return result;
    }

    @Override
    public Map<String, Object> findNextAvailableSlot(int durationMinutes, String date, String zoneName)
            throws ValidationException, EventStorageException {

        ZoneId zone = DateTimeUtil.getZone(zoneName);
        LocalDate day = (date != null) ? DateTimeUtil.toLocalDate(date) : LocalDate.now(zone);
        ZonedDateTime startOfDay = day.atStartOfDay(zone);
        ZonedDateTime endOfDay = day.plusDays(1).atStartOfDay(zone);

        long slotDurationMillis = Duration.ofMinutes(durationMinutes).toMillis();
        long nowMillis = ZonedDateTime.now(zone).toInstant().toEpochMilli();
        long searchStart = Math.max(startOfDay.toInstant().toEpochMilli(), nowMillis);
        long searchEnd = endOfDay.toInstant().toEpochMilli();

        List<Event> events = getEventsForDay(day, zone);
        long current = searchStart;

        for (Event e : events) {
            if (e.getStartEpochMillis() >= searchEnd) break;

            if (e.getStartEpochMillis() - current >= slotDurationMillis) {
                return makeSlotResult(current, current + slotDurationMillis);
            }
            current = Math.max(current, e.getEndEpochMillis());
        }

        if (searchEnd - current >= slotDurationMillis) {
            return makeSlotResult(current, current + slotDurationMillis);
        }

        Map<String, Object> noSlot = new HashMap<>();
        noSlot.put("message", "No available slot found.");
        return noSlot;
    }

    private Map<String, Object> makeSlotResult(long start, long end) {
        Map<String, Object> result = new HashMap<>();
        result.put("startEpochMillis", start);
        result.put("endEpochMillis", end);
        return result;
    }

    @Override
    public Event addEvent(Event newEvent)
            throws ValidationException, EventConflictException, EventStorageException {
        if (newEvent == null) {
            throw new ValidationException("Event cannot be null.");
        }

        log.debug("Attempting to add new event: {}", newEvent);

        List<Event> events;
        try {
            events = new ArrayList<>(storage.loadEvents());
        } catch (Exception e) {
            log.error("Failed to load existing events from storage", e);
            throw new EventStorageException("Unable to access event storage.", e);
        }

        for (Event existing : events) {
            boolean overlaps = newEvent.getStartEpochMillis() < existing.getEndEpochMillis()
                    && newEvent.getEndEpochMillis() > existing.getStartEpochMillis();

            if (overlaps) {
                log.info("Conflict detected [newEvent={} existingEvent={}]", newEvent, existing);
                throw new EventConflictException("Event conflicts with an existing event - " + existing.getTitle());
            }
        }

        events.add(newEvent);
        events.sort(Comparator.comparingLong(Event::getStartEpochMillis));

        try {
            storage.saveEvents(events);
            log.info("Successfully added event: {}", newEvent);
        } catch (Exception e) {
            log.error("Failed to save event to storage", e);
            throw new EventStorageException("Failed to persist event to storage.", e);
        }

        return newEvent;
    }

}
