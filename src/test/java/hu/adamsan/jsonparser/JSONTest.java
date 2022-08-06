package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONTest {

    @Test
    void assertJsonCantBeInstantiatedWithConstructor() {
        assertThrows(
                NoSuchMethodException.class,
                JSON.class::getConstructor);
    }

    @Test
    void assertJSONparseCanParseString() {
        String string = "\"apple\"";
        JSON.JSONString json = JSON.parser(string);
        assertEquals(string, json.value);
    }
}