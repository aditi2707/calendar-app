package com.calendar.api;

import com.calendar.exception.*;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Centralized exception handler configuration for mapping exceptions to HTTP responses.
 */
public class ExceptionHandlerConfig {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerConfig.class);


    public static void register(Javalin app) {
        app.exception(Exception.class, (e, ctx) -> {
            log.error("Error occurred. ", e);

            int status = 500;
            String message = "Internal server error";

            if (e instanceof ValidationException) {
                status = 400;
                message = e.getMessage();
            } else if (e instanceof EventConflictException) {
                status = 400;
                message = e.getMessage();
            } else if (e instanceof JsonProcessingException) {
                status = 400;
                message = "Invalid input format";
            } else if (e instanceof EventStorageException) {
                status = 500;
                message = "Error saving or loading data";
            } else if (e instanceof CalendarException) {
                message = e.getMessage();
            }

            ctx.status(status).json(error(message));
        });
    }

    private static Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }
}
