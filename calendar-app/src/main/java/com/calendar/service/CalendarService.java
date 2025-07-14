package com.calendar.service;

import com.calendar.exception.EventConflictException;
import com.calendar.exception.EventStorageException;
import com.calendar.exception.ValidationException;
import com.calendar.model.Event;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing calendar events.
 */
public interface CalendarService {

    /**
     * Lists all events scheduled for today based on the provided timezone.
     *
     * @param zoneName the time zone ID (e.g., "America/Los_Angeles")
     * @return list of events occurring today
     * @throws ValidationException if the timezone is invalid
     * @throws EventStorageException if loading events fails
     */
    List<Event> listEventsForToday(String zoneName)
            throws ValidationException, EventStorageException;

    /**
     * Lists only those events for today that have not yet ended.
     *
     * @param zoneName the time zone ID
     * @return list of remaining events for today
     * @throws ValidationException if the timezone is invalid
     * @throws EventStorageException if loading events fails
     */
    List<Event> listRemainingEventsForToday(String zoneName)
            throws ValidationException, EventStorageException;

    /**
     * Lists all events scheduled for the specified date in the given timezone.
     *
     * @param date     the date in format yyyy-MM-dd
     * @param zoneName the time zone ID
     * @return list of events for that day
     * @throws ValidationException if the date or timezone is invalid
     * @throws EventStorageException if loading events fails
     */
    List<Event> listEventsForDay(String date, String zoneName)
            throws ValidationException, EventStorageException;

    /**
     * Finds the next available slot of the given duration on the specified date.
     *
     * @param durationMinutes the required duration in minutes
     * @param date            the date in format yyyy-MM-dd (nullable, defaults to today)
     * @param zoneName        the time zone ID
     * @return a map containing start and end epoch millis of the available slot,
     *         or a message if no slot is found
     * @throws ValidationException if inputs are invalid
     * @throws EventStorageException if loading events fails
     */
    Map<String, Object> findNextAvailableSlot(int durationMinutes, String date, String zoneName)
            throws ValidationException, EventStorageException;

    /**
     * Adds a new event after validating and checking for conflicts.
     *
     * @param event the new event to add
     * @return the added event object
     * @throws ValidationException if the event is null or invalid
     * @throws EventConflictException if the event overlaps with an existing one
     * @throws EventStorageException if saving fails
     */
    Event addEvent(Event event)
            throws ValidationException, EventConflictException, EventStorageException;
}
