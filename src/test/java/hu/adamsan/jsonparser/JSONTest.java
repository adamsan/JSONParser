package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JSONTest {

    @Test
    void assertJsonParseCanParseString() {
        String string = "\"apple\"";
        JSON.JSONString json = (JSON.JSONString) JSON.parse(string);
        assertThat(json).isNotNull();
        assertThat(json.value).isEqualTo("apple");
    }

    @Test
    void assertJsonParseCanParseInteger() {
        Number number = 44;
        JSON.JSONNumber json = (JSON.JSONNumber) JSON.parse(String.valueOf(number));
        assertThat(json).isNotNull();
        assertThat(json.value.intValue()).isEqualTo(number.intValue());
    }

    @Test
    void assertJsonParseCanParseDouble() {
        Number number = 44.345;
        JSON.JSONNumber json = (JSON.JSONNumber) JSON.parse(String.valueOf(number));
        assertThat(json).isNotNull();
        assertThat(json.value.doubleValue()).isEqualTo(number.doubleValue());
    }

    @Test
    void assertJsonParseCanParseNull() {
        JSON.JSONNull json = (JSON.JSONNull) JSON.parse("null");
        assertThat(json).isNotNull();
    }

    @Test
    void assertJsonParseCanParseArrayOfNumbers() {
        String input = "[ 5, 6, 50.1, 60]";
        JSON.JSONArray json = (JSON.JSONArray) JSON.parse(input);
        assertThat(json).isNotNull();
        assertThat(json.items).hasSize(4);

        var last = (JSON.JSONNumber) json.items.get(3);
        assertThat(last.getValue()).isEqualTo(60);

        var lastButOne = (JSON.JSONNumber) json.items.get(2);
        assertThat(lastButOne.getValue()).isEqualTo(50.1);
    }

    @Test
    void assertJsonParseCanParseArrayOfNumbersStringsAndNulls() {
        String input = "[ null, \"orange\", 50.1, 60]";
        JSON.JSONArray json = (JSON.JSONArray) JSON.parse(input);
        assertThat(json).isNotNull();
        assertThat(json.items).hasSize(4);

        var last = (JSON.JSONNumber) json.items.get(3);
        assertThat(last.getValue()).isEqualTo(60);

        var lastButOne = (JSON.JSONNumber) json.items.get(2);
        assertThat(lastButOne.getValue()).isEqualTo(50.1);

        var first = (JSON.JSONNull) json.items.get(0);
        assertThat(first).isNotNull();

        var second = (JSON.JSONString) json.items.get(1);
        assertThat(second.value).isEqualTo("orange");
    }


    @Test
    void assertJsonParseCanParseArrayOfNumbersWithSpecialChar() {
        String input = "[ 5, 6, \"ⶔ\" , 60]";
        JSON.JSONArray json = (JSON.JSONArray) JSON.parse(input);
        assertThat(json).isNotNull();
        assertThat(json.items).hasSize(4);
    }

}