package com.calendar.exception;

public class EventConflictException extends CalendarException {
    public EventConflictException(String message) {
        super(message);
    }
}
