package com.calendar.cli;

import com.calendar.exception.EventConflictException;
import com.calendar.exception.EventStorageException;
import com.calendar.exception.ValidationException;
import com.calendar.model.Event;
import com.calendar.service.CalendarService;
import com.calendar.util.DateTimeUtil;
import com.calendar.util.EventFactory;
import com.calendar.util.SeedDataGenerator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command-line interface (CLI) runner for interacting with the calendar application.
 */
public class CalendarCliRunner {

    /**
     * Starts the interactive CLI.
     *
     * @param calendarService the calendar service instance
     */
    public static void run(CalendarService calendarService) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Calendar CLI started. Type 'exit' anytime to quit.");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(choice)) break;

            try {
                switch (choice) {
                    case "1":
                        handleAddEvent(scanner, calendarService);
                        break;
                    case "2":
                        handleListToday(scanner, calendarService);
                        break;
                    case "3":
                        handleListRemainingToday(scanner, calendarService);
                        break;
                    case "4":
                        handleListByDay(scanner, calendarService);
                        break;
                    case "5":
                        handleFindSlot(scanner, calendarService);
                        break;
                    case "6":
                        handleSeedDataGeneration();
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1–5 or type 'exit'.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Exiting Calendar CLI.");
    }

    private static void printMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Add event");
        System.out.println("2. List today’s events");
        System.out.println("3. List remaining events today");
        System.out.println("4. List events for a specific day");
        System.out.println("5. Find next available slot");
        System.out.println("6. Generate seed data");
        System.out.print("Enter choice (1–6 or 'exit'): ");
    }

    private static void handleAddEvent(Scanner scanner, CalendarService calendarService) {
        try {
            System.out.print("Enter event title: ");
            String title = scanner.nextLine();

            System.out.print("Enter start time (yyyy-MM-dd HH:mm): ");
            String start = scanner.nextLine();

            System.out.print("Enter end time (yyyy-MM-dd HH:mm): ");
            String end = scanner.nextLine();

            System.out.print("Enter time zone (e.g., America/Los_Angeles), or press Enter to use system default: ");
            String zoneInput = scanner.nextLine().trim();

            long startMillis = DateTimeUtil.toEpochMillis(start, zoneInput);
            long endMillis = DateTimeUtil.toEpochMillis(end, zoneInput);

            Event event = EventFactory.fromValues(title, startMillis, endMillis);
            Event added = calendarService.addEvent(event);
            System.out.println("Event added: " + added);

        } catch (ValidationException | EventConflictException | EventStorageException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to add event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleListToday(Scanner scanner, CalendarService service) {
        System.out.print("Enter timezone (e.g., \"America/Los_Angeles\") or press Enter for system default: ");
        String zone = scanner.nextLine().trim();
        try {
            List<Event> events = service.listEventsForToday(emptyToNull(zone));
            printEvents(events);
        } catch (ValidationException | EventStorageException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to list events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleListRemainingToday(Scanner scanner, CalendarService service) {
        System.out.print("Enter timezone (e.g., \"America/Los_Angeles\") or press Enter for system default: ");
        String zone = scanner.nextLine().trim();
        try {
            List<Event> events = service.listRemainingEventsForToday(emptyToNull(zone));
            printEvents(events);
        } catch (ValidationException | EventStorageException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to list events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleListByDay(Scanner scanner, CalendarService service) {
        System.out.print("Enter date (yyyy-MM-dd): ");
        String date = scanner.nextLine().trim();

        System.out.print("Enter timezone (e.g., \"America/Los_Angeles\") or press Enter for system default: ");
        String zone = scanner.nextLine().trim();

        try {
            List<Event> events = service.listEventsForDay(date, emptyToNull(zone));
            printEvents(events);
        } catch (ValidationException | EventStorageException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to list events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleFindSlot(Scanner scanner, CalendarService service) {
        System.out.print("Enter duration in minutes: ");
        int minutes = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter date (yyyy-MM-dd or press Enter for today): ");
        String date = scanner.nextLine().trim();
        date = date.isEmpty() ? null : date;

        System.out.print("Enter timezone (e.g., \"America/Los_Angeles\") or press Enter for system default: ");
        String zone = scanner.nextLine().trim();
        zone = zone.isEmpty() ? null : zone;

        try {
            Map<String, Object> slot = service.findNextAvailableSlot(minutes, date, zone);
            System.out.println("Slot: " + slot);
        } catch (ValidationException | EventStorageException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to find slot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleSeedDataGeneration() {
        try {
            SeedDataGenerator.generateSeedData();
            System.out.println("Seed data generated successfully.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            for (Event e : events) {
                System.out.printf("%s | %s – %s%n",
                        e.getTitle(),
                        DateTimeUtil.formatInstant(e.getStartInstant()),
                        DateTimeUtil.formatInstant(e.getEndInstant()));
            }
        }
    }

    private static String emptyToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }
}
