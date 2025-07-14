package com.calendar.util;

import com.calendar.dto.EventRequest;
import com.calendar.exception.ValidationException;
import com.calendar.model.Event;

import java.time.ZoneId;

public class EventFactory {

    /**
     * Parses a JSON string into an Event object and validates it.
     *
     * @param json the JSON representation of an Event
     * @return the validated Event
     * @throws ValidationException if validation fails
     */
    public static Event fromJson(String json) {
        Event event = JsonUtil.fromJson(json, Event.class);
        validate(event);
        return event;
    }

    /**
     * Creates and validates an Event from individual fields.
     *
     * @param title the event title
     * @param startEpochMillis the start time in milliseconds
     * @param endEpochMillis the end time in milliseconds
     * @return the validated Event
     * @throws ValidationException if validation fails
     */
    public static Event fromValues(String title, long startEpochMillis, long endEpochMillis) {
        Event event = new Event();
        event.setTitle(title);
        event.setStartEpochMillis(startEpochMillis);
        event.setEndEpochMillis(endEpochMillis);
        validate(event);
        return event;
    }

    /**
     * Converts an {@link EventRequest} DTO into a validated {@link Event} object.
     *
     * <p>This method parses the start and end date-time strings provided in the request,
     * combines them with the specified time zone, and converts them into epoch milliseconds.
     * It then creates and validates the corresponding {@link Event} object.</p>
     *
     * @param req the {@link EventRequest} containing the title, date, time, and zone
     * @return a validated {@link Event} object ready for addition to the calendar
     * @throws ValidationException if any required field is missing or the resulting event is invalid
     */
    public static Event fromRequest(EventRequest req) {
        if (req == null) throw new ValidationException("Missing event input.");

        String zoneStr = req.getZone();
        String startDateTimeStr = req.getStartDate() + " " + req.getStartTime();
        String endDateTimeStr = req.getEndDate() + " " + req.getEndTime();

        long startMillis = DateTimeUtil.toEpochMillis(startDateTimeStr, zoneStr);
        long endMillis = DateTimeUtil.toEpochMillis(endDateTimeStr, zoneStr);

        return fromValues(req.getTitle(), startMillis, endMillis);
    }


    /**
     * Validates an Event object.
     *
     * @param event the Event to validate
     * @throws ValidationException if validation fails
     */
    private static void validate(Event event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new ValidationException("Event title must not be empty.");
        }

        if (event.getStartEpochMillis() <= 0 || event.getEndEpochMillis() <= 0) {
            throw new ValidationException("Start and end times must be provided.");
        }

        if (event.getEndEpochMillis() <= event.getStartEpochMillis()) {
            throw new ValidationException("End time must be after start time.");
        }
    }
}
