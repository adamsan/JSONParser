package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonToStringTest {
    @Test
    void assertJsonNullCanStringify() {
        String input = "null";
        assertThat(new JSON.JSONNull().toString()).isEqualTo(input);
    }

    @Test
    void assertJsonNumberCanStringify() {
        String i = "499";
        assertThat(new JSON.JSONNumber(i).toString()).isEqualTo(i);

        String d = "3.4359";
        assertThat(new JSON.JSONNumber(d).toString()).isEqualTo(d);
    }

    @Test
    void assertJsonStringCanStringify() {
        String s = "\"some sneaky string\"";
        assertThat(new JSON.JSONString(s).toString()).isEqualTo(s);
    }
}
