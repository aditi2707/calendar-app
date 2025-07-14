package com.calendar.storage.impl;

import com.calendar.exception.EventStorageException;
import com.calendar.model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EventFileStorageTest {

    private Path tempFile;
    private EventFileStorage storage;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("events", ".json");
        storage = new EventFileStorage(tempFile.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testSaveAndLoadEvents() {
        Event event = new Event();
        event.setTitle("Meeting");
        event.setStartEpochMillis(1000L);
        event.setEndEpochMillis(2000L);

        List<Event> eventsToSave = Collections.singletonList(event);
        storage.saveEvents(eventsToSave);

        List<Event> loadedEvents = storage.loadEvents();
        assertEquals(1, loadedEvents.size());
        assertEquals("Meeting", loadedEvents.get(0).getTitle());
    }

    @Test
    public void testLoadEmptyFile() throws IOException {
        Files.write(tempFile, "".getBytes());  // Simulate empty file
        List<Event> events = storage.loadEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    public void testLoadMissingFileReturnsEmptyList() {
        Path nonExistentFile = tempFile.resolveSibling("nonexistent.json");
        EventFileStorage missingFileStorage = new EventFileStorage(nonExistentFile.toString());
        List<Event> events = missingFileStorage.loadEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    public void testLoadEventsThrowsOnInvalidJson() throws IOException {
        Files.write(tempFile, "invalid json".getBytes());
        assertThrowsExactly(EventStorageException.class, () -> storage.loadEvents());
    }

    @Test
    public void testSaveEventsThrowsOnIoError() throws IOException {
        Path readOnlyDir = Files.createTempDirectory("readonly-dir");
        Path readOnlyFile = Files.createFile(readOnlyDir.resolve("readonly.json"));
        readOnlyFile.toFile().setReadOnly();

        try {
            EventFileStorage roStorage = new EventFileStorage(readOnlyFile.toString());
            List<Event> events = Collections.singletonList(new Event());
            assertThrows(EventStorageException.class, () -> roStorage.saveEvents(events));
        } finally {
            readOnlyFile.toFile().setWritable(true);
            Files.deleteIfExists(readOnlyFile);
            Files.deleteIfExists(readOnlyDir);
        }
    }
}
