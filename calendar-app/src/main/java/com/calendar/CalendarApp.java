package com.calendar;

import com.calendar.api.EventController;
import com.calendar.api.ExceptionHandlerConfig;
import com.calendar.cli.CalendarCliRunner;
import com.calendar.service.CalendarService;
import com.calendar.service.impl.CalendarServiceImpl;
import com.calendar.storage.EventStorage;
import com.calendar.storage.impl.EventFileStorage;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarApp {

    private static final String STORAGE_FILE_NAME = "events.json";
    private static final String PARAM_NAME_PORT = "PORT";
    private static final Integer DEFAULT_PORT = 8000;
    private static final Logger log = LoggerFactory.getLogger(CalendarApp.class);

    public static void main(String[] args) {
        log.info("Starting application...");
        EventStorage storage = new EventFileStorage(STORAGE_FILE_NAME);
        CalendarService calendarService = new CalendarServiceImpl(storage);

        if (args.length > 0 && "cli".equalsIgnoreCase(args[0])) {
            log.info("Running in CLI mode");
            CalendarCliRunner.run(calendarService);
        } else {
            startRestServer(calendarService);
        }
    }

    private static void startRestServer(CalendarService calendarService) {
        int port = getPort();
        log.info("Starting HTTP server on port {}", port);
        try {
            Javalin app = Javalin.create().start(port);
            ExceptionHandlerConfig.register(app);
            new EventController(app, calendarService);
        } catch (Exception e) {
            log.error("Error starting HTTP server. ", e);
        }
    }

    private static int getPort() {
        String port = System.getenv(PARAM_NAME_PORT);
        return port != null ? Integer.parseInt(port) : DEFAULT_PORT;
    }
}
