package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONTest {

    @Test
    void assertJSONparseCanParseString() {
        String string = "\"apple\"";
        JSON.JSONString json = (JSON.JSONString) JSON.parse(string);
        assertEquals(string, json.value);
    }

    @Test
    void assertJSONparseCanParseInteger() {
        Number number = 44;
        JSON.JSONNumber json = (JSON.JSONNumber) JSON.parse(String.valueOf(number));
        assertEquals(number.intValue(), json.value.intValue());
    }

    @Test
    void assertJSONparseCanParseDouble() {
        Number number = 44.345;
        JSON.JSONNumber json = (JSON.JSONNumber) JSON.parse(String.valueOf(number));
        assertEquals(number.doubleValue(), json.value.doubleValue());
    }

}