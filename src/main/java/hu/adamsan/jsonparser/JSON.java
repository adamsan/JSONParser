package hu.adamsan.jsonparser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public sealed class JSON {
    private JSON() {
    }

    public static JSON parse(String json) {
        json = json.trim();
        if (isString(json)) return new JSONString(json);
        if (isNumber(json)) return new JSONNumber(json);
        if (isNull(json)) return new JSONNull();
        if (isArray(json)) return new JSONArray(json);
        else return null;
    }

    private static boolean isArray(String json) {
        return json.startsWith("[") && json.endsWith("]");
    }

    private static boolean isNull(String json) {
        return "null".equals(json);
    }

    private static boolean isNumber(String json) {
        if (json == null) return false;
        try {
            Double.parseDouble(json);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isString(String json) {
        return json.startsWith("\"") && json.endsWith("\"");
    }

    private static List<Integer> findCommaIndexes(String inside) {
        return IntStream.range(0, inside.length())
                .filter(i -> inside.charAt(i) == ',')
                .boxed().toList();
    }

    static final class JSONString extends JSON {
        String value;

        public JSONString(String value) {
            this.value = value.substring(1, value.length() - 1);
        }
    }

    static final class JSONNumber extends JSON {
        BigDecimal value;

        public JSONNumber(String json) {
            try {
                this.value = BigDecimal.valueOf(Integer.parseInt(json));
            } catch (NumberFormatException ex) {
                this.value = BigDecimal.valueOf(Double.parseDouble(json));
            }
        }

        public Number getValue() {
            if (value.stripTrailingZeros().scale() <= 0) return value.intValue();
            return value.doubleValue();
        }
    }

    static final class JSONNull extends JSON {

    }

    static final class JSONArray extends JSON {
        List<JSON> items = new ArrayList<>();

        public JSONArray(String json) {
            String inside = json.substring(1, json.length() - 1);
            List<Integer> commaIndexes = findCommaIndexes(inside);
            splitByIndexes(inside, commaIndexes)
                    .map(JSON::parse)
                    .forEach(items::add);
        }

        private Stream<String> splitByIndexes(String inside, List<Integer> commaIndexes) {
            StringBuilder builder = new StringBuilder(inside);
            char specialChar = 'ⶔ'; // assume this does not occures in inside string
            commaIndexes.forEach(i -> builder.setCharAt(i, specialChar));
            return Arrays.stream(builder.toString().split(String.valueOf(specialChar)));
        }
    }
}
