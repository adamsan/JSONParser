package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONTest {

    @Test
    void assertJsonCantBeInstantiatedWithConstructor() {
        assertThrows(
                NoSuchMethodException.class,
                JSON.class::getConstructor);
    }
}