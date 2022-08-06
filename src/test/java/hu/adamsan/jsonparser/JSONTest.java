package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JSONTest {

    @Test
    void assertJsonParseCanParseString() {
        String string = "\"apple\"";
        JSON.JSONString json = (JSON.JSONString) JSON.parse(string);
        assertNotNull(json);
        assertEquals(string, json.value);
    }

    @Test
    void assertJsonParseCanParseInteger() {
        Number number = 44;
        JSON.JSONNumber json = (JSON.JSONNumber) JSON.parse(String.valueOf(number));
        assertNotNull(json);
        assertEquals(number.intValue(), json.value.intValue());
    }

    @Test
    void assertJsonParseCanParseDouble() {
        Number number = 44.345;
        JSON.JSONNumber json = (JSON.JSONNumber) JSON.parse(String.valueOf(number));
        assertNotNull(json);
        assertEquals(number.doubleValue(), json.value.doubleValue());
    }

    @Test
    void assertJsonParseCanParseNull() {
        JSON.JSONNull json = (JSON.JSONNull) JSON.parse("null");
        assertNotNull(json);
    }

}