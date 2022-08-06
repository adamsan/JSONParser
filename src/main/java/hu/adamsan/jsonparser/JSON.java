package hu.adamsan.jsonparser;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public sealed abstract class JSON {
    private JSON() {
    }

    public static JSON parse(String json) {
        json = json.trim();
        if (isString(json)) return new JSONString(json);
        if (isNumber(json)) return new JSONNumber(json);
        if (isNull(json)) return new JSONNull();
        if (isArray(json)) return new JSONArray(json);
        if (isObject(json)) return new JSONObject(json);
        throw new JsonParseException("Could not parse:\n" + json);
    }

    private static boolean isObject(String json) {
        return json.startsWith("{") && json.endsWith("}");
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
        Deque<Character> q = new LinkedList<>();
        return IntStream.range(0, inside.length())
                .peek(i -> {
                    char c = inside.charAt(i);
                    switch (c) {
                        case '{' -> q.push('}');
                        case '[' -> q.push(']');
                        case ']', '}' -> {
                            if (!q.isEmpty() && q.peek() == c) q.pop();
                        }
                    }
                })
                .filter(i -> inside.charAt(i) == ',' && q.size() == 0)
                .boxed().toList();
    }

    private static char findSpecialCharNotInString(String inside) {
        char specialChar = 'ⶔ';
        while (inside.indexOf(specialChar) > 0) specialChar++; // in case above char occures in the string.
        return specialChar;
    }

    protected static Stream<String> splitByIndexes(String inside, List<Integer> commaIndexes) {
        StringBuilder builder = new StringBuilder(inside);
        char splitBy = findSpecialCharNotInString(inside);
        commaIndexes.forEach(i -> builder.setCharAt(i, splitBy));
        return Arrays.stream(builder.toString().split(String.valueOf(splitBy)));
    }

    static final class JSONString extends JSON {
        String value;

        public JSONString(String value) {
            if (!value.startsWith("\"") || !value.endsWith("\""))
                throw new IllegalArgumentException("parameter did not start or end with '\"'!");
            this.value = value.substring(1, value.length() - 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JSONString that = (JSONString) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return '"' + value + '"';
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

        @Override
        public String toString() {
            return getValue().toString();
        }

        public Number getValue() {
            if (value.stripTrailingZeros().scale() <= 0) return value.intValue();
            return value.doubleValue();
        }
    }

    static final class JSONNull extends JSON {
        @Override
        public String toString() {
            return "null";
        }
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
    }

    static final class JSONObject extends JSON {
        Map<JSONString, JSON> map = new HashMap<>();

        public JSONObject(String json) {
            String inside = json.substring(1, json.length() - 1);
            List<Integer> commaIndexes = findCommaIndexes(inside);
            splitByIndexes(inside, commaIndexes)
                    .map(s -> s.split(":", 2))
                    .forEach(e -> map.put(new JSONString(e[0].trim()), JSON.parse(e[1])));
        }
    }

    private static class JsonParseException extends RuntimeException {
        public JsonParseException(String message) {
            super(message);
        }
    }
}
