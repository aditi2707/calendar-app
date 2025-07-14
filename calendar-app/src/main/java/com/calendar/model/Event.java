package com.calendar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a calendar event with start and end times stored as epoch milliseconds.
 */
public class Event {

    private String title;
    private long startEpochMillis;
    private long endEpochMillis;

    public Event() {

    }

    public Event(String title, long startEpochMillis, long endEpochMillis) {
        this.title = title;
        this.startEpochMillis = startEpochMillis;
        this.endEpochMillis = endEpochMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartEpochMillis() {
        return startEpochMillis;
    }

    public void setStartEpochMillis(long startEpochMillis) {
        this.startEpochMillis = startEpochMillis;
    }

    public long getEndEpochMillis() {
        return endEpochMillis;
    }

    public void setEndEpochMillis(long endEpochMillis) {
        this.endEpochMillis = endEpochMillis;
    }

    @JsonIgnore
    public Instant getStartInstant() {
        return Instant.ofEpochMilli(startEpochMillis);
    }

    @JsonIgnore
    public Instant getEndInstant() {
        return Instant.ofEpochMilli(endEpochMillis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return startEpochMillis == event.startEpochMillis &&
                endEpochMillis == event.endEpochMillis &&
                Objects.equals(title, event.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, startEpochMillis, endEpochMillis);
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", startEpochMillis=" + startEpochMillis +
                ", endEpochMillis=" + endEpochMillis +
                '}';
    }
}
