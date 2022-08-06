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

    @Test
    void assertJsonArrayCanStringify() {
        String arr = "[1, 2, 3.55, \"apple\"]";
        assertThat(new JSON.JSONArray(arr).toString()).isEqualTo(arr);
    }

    @Test
    void assertJsonObjectCanStringify() {
        String obj = "{\"id\": 7234, \"title\": \"The lost paradise\", \"scores\": [4, 2, 5]}";
        assertThat(new JSON.JSONObject(obj).toString()).isEqualTo(obj);
    }


}
