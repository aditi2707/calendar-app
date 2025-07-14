package com.calendar.storage;

import com.calendar.exception.EventStorageException;
import com.calendar.model.Event;

import java.util.List;

/**
 * Abstraction for storing and retrieving calendar events.
 *
 * This interface decouples the service layer from the underlying storage mechanism
 * (e.g., file system, database, or in-memory).
 */
public interface EventStorage {

    /**
     * Loads all calendar events from the underlying storage.
     *
     * @return a list of {@link Event} objects
     * @throws EventStorageException if an error occurs while accessing or parsing the data
     */
    List<Event> loadEvents() throws EventStorageException;

    /**
     * Persists the given list of events to the underlying storage.
     *
     * @param events the list of {@link Event} objects to be saved
     * @throws EventStorageException if an error occurs while writing to storage
     */
    void saveEvents(List<Event> events) throws EventStorageException;
}
