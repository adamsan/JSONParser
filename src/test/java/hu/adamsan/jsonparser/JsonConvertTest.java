package hu.adamsan.jsonparser;

import org.junit.jupiter.api.Test;

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

}
