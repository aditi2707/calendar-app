# 🗓️ Calendar Application

A simple yet powerful timezone-aware calendar application that supports both RESTful API and CLI modes. Users can create events, view today's or a specific day's events, check remaining events, and find available time slots — all with full timezone support.

---

## Table of Contents

1. [Features](#features)
2. [Technology Stack & Requirements](#technology-stack--requirements)
3. [Repository Setup](#repository-setup)
4. [Walkthrough](#walkthrough)
5. [Running the Backend](#running-the-backend)
    - [REST API Mode](#rest-api-mode)
    - [CLI Mode](#cli-mode)
6. [Running the Frontend](#running-the-frontend)
7. [API Endpoints & Inputs](#api-endpoints--inputs)
8. [Example Usage](#example-usage)

---

## Features

- Add calendar events with title, start/end time, and timezone
- List all events for today (by timezone)
- List remaining events for today
- List events for a specific date
- Find next available time slot for a given day and duration
- Dual-mode support: REST and CLI
- Timezone and daylight saving aware
- Lightweight UI with Bootstrap
- JSON-based file storage

---

## Technology Stack & Requirements

### Java Backend

- Java 8+
- Maven
- [Javalin](https://javalin.io/) (REST API)
- SLF4J + Logback
- Jackson for JSON
- JUnit 5.10 for testing
- Mockito for mocking

### Python (for optional static file hosting)

- Python 3.6+
- Required only if hosting the UI with `python -m http.server` (optional)

---

## Repository Setup

```bash
git clone https://github.com/aditi2707/calendar-app.git
cd calendar-app
```

Make sure your `JAVA_HOME` points to Java 8 or later.

---

## Build and Run Backend

### REST API Mode (Default)

```bash
# Build the app
mvn clean install package

# Run the app (default port 8000)
java -jar target/calendar-app-1.0.0.jar
```

Or specify a custom port:

```bash
PORT=8080 java -jar target/calendar-app-1.0.0.jar
```

The app will start on `http://localhost:8000` (or your chosen port).

### CLI Mode

```bash
java -jar target/calendar-app-1.0.0.jar CLI
```

Follow the console prompts to add/list events or find available slots.

---

## Running the Frontend (UI)

The frontend is a static HTML page that interacts with the REST API.

### Option 1: Open Directly

Simply open `resources/public/calendar.html` in your browser if the backend is running on `localhost:8000`.

> ⚠️ Ensure your browser allows CORS requests to `localhost`.

### Option 2: Serve with Python (Recommended for CORS)

```bash
cd resources/public
python -m http.server 8081
```

Then open: [http://localhost:8081/calendar.html](http://localhost:8081/calendar.html)

---

## Walkthrough

### 1. Add Event (REST or UI)

- Input: Title, Start Date & Time, End Date & Time, Timezone
- Start/end must be in `yyyy-MM-dd HH:mm` format
- Example (REST):

POST /events
```json
{
  "title": "Team Meeting",
  "startEpochMillis": 1721355600000,
  "endEpochMillis": 1721359200000
}
```

---

### 2. List All Events for Today

```http
GET /events/today?zone=America/New_York
```

---

### 3. List Remaining Events

```http
GET /events/remaining?zone=Europe/London
```

---

### 4. List Events for a Specific Day

```http
GET /events/day?date=2025-07-14&zone=Asia/Kolkata
```

---

### 5. Find Next Available Slot

```http
GET /events/next-slot?minutes=30&date=2025-07-14&zone=America/Los_Angeles
```

Returns:

```json
{
  "startEpochMillis": 1721348400000,
  "endEpochMillis": 1721350200000
}
```

---

## Required Inputs

| Action | Required Fields |
|--------|-----------------|
| Add Event (REST/CLI/UI) | Title, Start Time, End Time, Timezone |
| List Events for Today | Timezone |
| List Events for Date | Date, Timezone |
| Find Slot | Duration (minutes), Optional Date, Timezone |

---

## Testing

To run tests:

```bash
mvn test
```

Includes comprehensive unit tests with 100% coverage using JUnit 5.10 and Mockito.

---

## Seed Data Generator

The project includes a dynamic **Seed Data Generator** accessible via both code and UI.

### Why it’s Important

- A static seed file can’t reflect **"today"** or **"remaining"** event scenarios reliably.
- Generates **past, present, and future** events to test:
   - Conflict resolution
   - Time zone correctness
   - Next available slot calculation
- Ensures the app demonstrates full capabilities across time zones and edge cases.

### How to Use

1. **From UI**: Click the “Generate Seed Data” button at the bottom.
   - This invokes the `GET /generate-seed-data` endpoint.
   - Automatically overwrites `events.json` with fresh content.

2. **From backend code** (Java):
   ```java
   new SeedDataGenerator("events.json").generate();
   ```

3. **From browser**:
   ```
   http://localhost:8000/generate-seed-data
   ```

---

## Project Structure

```
calendar-app/
│
├── src/
│   ├── main/
│   │   ├── java/com/calendar/
│   │   ├── resources/
│   │       ├── public/calendar.html
│
├── target/        ← compiled jar
├── events.json    ← storage file
├── README.md
├── pom.xml
```

---

## Author & License

Built by Aditi Srivastava.  
Licensed under [MIT](LICENSE).

---
