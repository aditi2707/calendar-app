package com.calendar.storage.impl;

import com.calendar.exception.EventStorageException;
import com.calendar.model.Event;
import com.calendar.storage.EventStorage;
import com.calendar.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventFileStorage implements EventStorage {

    private final String filePath;

    public EventFileStorage(String filePath) {

        this.filePath = filePath;
    }

    @Override
    public List<Event> loadEvents() throws EventStorageException {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            if (content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            Event[] events = JsonUtil.fromJson(content, Event[].class);
            List<Event> list = new ArrayList<>();
            Collections.addAll(list, events);
            return list;
        } catch (IOException | com.calendar.exception.JsonProcessingException e) {
            throw new EventStorageException("Failed to load events from file", e);
        }
    }

    @Override
    public void saveEvents(List<Event> events) throws EventStorageException {
        try {
            String json = JsonUtil.toJson(events);
            Files.write(Paths.get(filePath), json.getBytes());
        } catch (IOException e) {
            throw new EventStorageException("Failed to save events to file", e);
        }
    }
}
