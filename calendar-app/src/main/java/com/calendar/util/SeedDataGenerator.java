package com.calendar.util;

import com.calendar.model.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeedDataGenerator {

    private static final String OUTPUT_PATH = "events.json";

    public static void generateSeedData() {
        List<Event> events = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();

        ZonedDateTime now = ZonedDateTime.now(zone);

        // Past events
        events.add(createEvent("Doctor Appointment", now.minusDays(5).withHour(10), 60));
        events.add(createEvent("Team Meeting", now.minusDays(3).withHour(15), 30));

        // Today's events
        events.add(createEvent("Morning Workout", now.withHour(7), 45));
        events.add(createEvent("Lunch with Alex", now.withHour(12), 60));
        events.add(createEvent("Interview with Aditi", now.withHour(16), 30));

        // Future events
        events.add(createEvent("Dentist Visit", now.plusDays(1).withHour(9), 45));
        events.add(createEvent("Parent-Teacher Conference", now.plusDays(2).withHour(11), 60));
        events.add(createEvent("Vacation Flight", now.plusDays(5).withHour(6), 120));

        try {
            String json = JsonUtil.toJson(events);
            Files.write(Paths.get(OUTPUT_PATH), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write seed data to file", e);
        }
    }

    private static Event createEvent(String title, ZonedDateTime startTime, int durationMinutes) {
        Event e = new Event();
        e.setTitle(title);
        Instant start = startTime.toInstant();
        Instant end = startTime.plusMinutes(durationMinutes).toInstant();
        e.setStartEpochMillis(start.toEpochMilli());
        e.setEndEpochMillis(end.toEpochMilli());
        return e;
    }
}
