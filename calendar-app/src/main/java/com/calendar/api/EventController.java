package com.calendar.api;

import com.calendar.dto.EventRequest;
import com.calendar.model.Event;
import com.calendar.service.CalendarService;
import com.calendar.util.EventFactory;
import com.calendar.util.SeedDataGenerator;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class EventController {

    public EventController(Javalin app, CalendarService calendarService) {

        app.get("/health", new Handler() {
            @Override
            public void handle(Context ctx) {
                ctx.result("OK");
            }
        });

        app.get("/events/today", new Handler() {
            @Override
            public void handle(Context ctx) {
                String zone = ctx.queryParam("zone");
                ctx.json(calendarService.listEventsForToday(zone));
            }
        });

        app.get("/events/remaining", new Handler() {
            @Override
            public void handle(Context ctx) {
                String zone = ctx.queryParam("zone");
                ctx.json(calendarService.listRemainingEventsForToday(zone));
            }
        });

        app.get("/events/day", new Handler() {
            @Override
            public void handle(Context ctx) {
                String date = ctx.queryParam("date");
                String zone = ctx.queryParam("zone");
                ctx.json(calendarService.listEventsForDay(date, zone));
            }
        });

        app.get("/events/next-slot", new Handler() {
            @Override
            public void handle(Context ctx) {
                int minutes = Integer.parseInt(ctx.queryParam("minutes"));
                String date = ctx.queryParam("date");
                String zone = ctx.queryParam("zone");
                ctx.json(calendarService.findNextAvailableSlot(minutes, date, zone));
            }
        });

        app.post("/events", ctx -> {
            EventRequest req = ctx.bodyAsClass(EventRequest.class);
            Event event = EventFactory.fromRequest(req);
            ctx.json(calendarService.addEvent(event));
        });

        app.get("/generate-seed-data", new Handler() {
            @Override
            public void handle(Context ctx) {
                SeedDataGenerator.generateSeedData();
                ctx.result("Seed data generated successfully.");
            }
        });

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type");
        });

        app.options("/*", ctx -> {
            ctx.status(200);
        });

    }

}
