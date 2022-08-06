package hu.adamsan.jsonparser;

import hu.adamsan.jsonparser.testmodels.Person;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonConvertTest {
    @Test
    void testJsonNullConversion() {
        assertThat(new JSON.JSONNull().convert(Object.class)).isNull();
    }

    @Test
    void testJsonNumberConversionIntegerToInteger() {
        assertThat(new JSON.JSONNumber("22").convert(Integer.class)).isEqualTo(22);
    }

    @Test
    void testJsonNumberConversionIntegerToShort() {
        assertThat(new JSON.JSONNumber("22").convert(Short.class)).isEqualTo((short) 22);
    }

    @Test
    void testJsonNumberConversionIntegerToLong() {
        assertThat(new JSON.JSONNumber("98792291989151591").convert(Long.class)).isEqualTo(98792291989151591L);
    }

    @Test
    void testJsonNumberConversionIntegerToFloatingPoint() {
        assertThat(new JSON.JSONNumber("226").convert(Double.class)).isEqualTo(226d);

        assertThat(new JSON.JSONNumber("56").convert(Float.class)).isEqualTo(56f);
        assertThat(new JSON.JSONNumber("22").convert(Integer.class)).isEqualTo(22);
    }

    @Test
    void testJsonNumberConversionFloatingPointToFloatingPoint() {
        assertThat(new JSON.JSONNumber("12.226").convert(Double.class)).isEqualTo(12.226);
        assertThat(new JSON.JSONNumber("56.159").convert(Float.class)).isEqualTo(56.159f);
    }

    @Test
    void testJsonArrayConversionCanConvertToIntArray() {
        String input = "[1, 10, 20]";
        var json = new JSON.JSONArray(input).convert(int[].class);
        assertThat(json).hasSize(3).containsExactly(1, 10, 20);
    }


    @Test
    void testJsonArrayConversionCanConvertToDoubleArray() {
        String input = "[3.14, 10, 20.5]";
        var json = new JSON.JSONArray(input).convert(double[].class);
        assertThat(json).hasSize(3).containsExactly(3.14, 10, 20.5);
    }

    @Test
    void testJsonArrayConversionCanConvertToIntList() {
        String input = "[1, 10, 20]";
        List<Integer> nums = new ArrayList<>();
        var json = new JSON.JSONArray(input).convert(nums.getClass()); // TODO: how to preserve type information?
        assertThat(json).hasSize(3).containsExactly(1, 10, 20);
    }

    @Test
    void testJsonArrayConversionCanConvertToIntSet() {
        String input = "[1, 10, 20, 159]";
        Set<Integer> nums = new HashSet<>();
        var json = new JSON.JSONArray(input).convert(nums.getClass()); // TODO: how to preserve type information?
        assertThat(json).hasSize(4).containsExactlyInAnyOrder(1, 10, 20, 159);
    }

    @Test
    void testJsonArrayConversionCanConvertToStringList() {
        String input = "[\"apple\", \"banana\", \"coconut\"]";
        List<String> nums = new ArrayList<>();
        var json = new JSON.JSONArray(input).convert(nums.getClass()); // TODO: how to preserve type information?
        assertThat(json).hasSize(3).containsExactly("apple", "banana", "coconut");
    }

    @Test
    void testJsonArrayConversionCanConvertSimpleObject() {
        String input = """
                {
                    "id": null,
                    "firstName": "John",
                    "lastName": [1,2,3,4,5],
                    "items":{
                        "name":"orange",
                        "price": 25.5,
                        "type": "food"
                    },
                    "age": 35
                }
                """;
        Person person = JSON.parse(input).convert(Person.class);
        assertThat(person.getId()).isNull();
        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getAge()).isEqualTo(35);
    }
}


