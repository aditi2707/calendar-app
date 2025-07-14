package com.calendar.util;

import com.calendar.exception.JsonProcessingException;
import com.calendar.model.Event;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilTest {

    @Test
    public void testToJson_Success() {
        Event event = new Event("Meeting", 1699999999999L, 1700003599999L);
        String json = JsonUtil.toJson(event);
        assertTrue(json.contains("Meeting"));
        assertTrue(json.contains("1699999999999"));
    }

    @Test
    public void testFromJson_Success() {
        String json = "{\"title\":\"Meeting\",\"startEpochMillis\":1699999999999,\"endEpochMillis\":1700003599999}";
        Event event = JsonUtil.fromJson(json, Event.class);
        assertEquals("Meeting", event.getTitle());
        assertEquals(1699999999999L, event.getStartEpochMillis());
        assertEquals(1700003599999L, event.getEndEpochMillis());
    }

    @Test
    public void testToJson_InvalidInput() {
        Object invalidObject = new Object() {
            private final Object self = this;
        };
        assertThrowsExactly(JsonProcessingException.class, () -> JsonUtil.toJson(invalidObject));
    }

    @Test
    public void testFromJson_InvalidInput() {
        String invalidJson = "{\"title\":\"Meeting\",\"startEpochMillis\":\"notANumber\"}";
        assertThrowsExactly(JsonProcessingException.class, () -> JsonUtil.fromJson(invalidJson, Event.class));
    }
}
