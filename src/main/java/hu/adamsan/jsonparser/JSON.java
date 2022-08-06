package hu.adamsan.jsonparser;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
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

    public abstract <T> T convert(Class<T> clazz);

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

        @Override
        public <T> T convert(Class<T> clazz) {
            return null;
        }
    }

    static final class JSONNumber extends JSON {
        BigDecimal value;

        public JSONNumber(String json) {
            try {
                this.value = BigDecimal.valueOf(Long.parseLong(json));
            } catch (NumberFormatException ex) {
                this.value = BigDecimal.valueOf(Double.parseDouble(json));
            }
        }

        @Override
        public String toString() {
            return getValue().toString();
        }

        public Number getValue() {
            if (isInteger()) return value.intValue();
            return value.doubleValue();
        }

        private boolean isInteger() {
            return value.stripTrailingZeros().scale() <= 0;
        }

        @Override
        public <T> T convert(Class<T> clazz) {
            List<?> intTypes = List.of(Integer.class, Short.class, Long.class, Byte.class);
            if (!isInteger() && intTypes.contains(clazz)) throw new JsonNumberConversionException();
            try {
                if (clazz == Long.class) return (T) (Long) value.longValue();
                if (clazz == Integer.class) return (T) (Integer) value.intValue();
                if (clazz == Short.class) return (T) (Short) value.shortValueExact();
                if (clazz == Byte.class) return (T) (Byte) value.byteValueExact();

                if (clazz == Double.class) return (T) (Double) value.doubleValue();
                if (clazz == Float.class) return (T) (Float) value.floatValue();
                if (clazz == BigDecimal.class) return (T) value;
                return (T) getValue();
            } catch (ClassCastException ex) {
                throw new RuntimeException(ex);
            }
        }

        static class JsonNumberConversionException extends RuntimeException {

        }
    }

    static final class JSONNull extends JSON {
        @Override
        public String toString() {
            return "null";
        }

        public <T> T convert(Class<T> clazz) {
            return null;
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

        @Override
        public String toString() {
            return items.stream().map(JSON::toString).collect(Collectors.joining(", ", "[", "]"));
        }

        @Override
        public <T> T convert(Class<T> clazz) {
            return null;
        }
    }

    static final class JSONObject extends JSON {
        Map<JSONString, JSON> map = new HashMap<>();
        List<JSONString> keysInOrder = new ArrayList<>();

        public JSONObject(String json) {
            String inside = json.substring(1, json.length() - 1);
            List<Integer> commaIndexes = findCommaIndexes(inside);
            splitByIndexes(inside, commaIndexes)
                    .map(s -> s.split(":", 2))
                    .forEach(e -> {
                        JSONString key = new JSONString(e[0].trim());
                        map.put(key, JSON.parse(e[1]));
                        keysInOrder.add(key);
                    });
        }

        @Override
        public String toString() {
            return keysInOrder.stream()
                    .map(k -> k.toString() + ": " + map.get(k).toString()).collect(Collectors.joining(", ", "{", "}"));
        }

        @Override
        public <T> T convert(Class<T> clazz) {
            return null;
        }
    }

    private static class JsonParseException extends RuntimeException {
        public JsonParseException(String message) {
            super(message);
        }
    }
}
