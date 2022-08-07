package hu.adamsan.jsonparser;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

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
                throw new IllegalArgumentException("parameter did not start or end with '\"':\n" + value);
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
            return (T) value;
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

        public <T, I> T convertCollection(Class<?> clazz, Type itemType) {
            if (List.class.isAssignableFrom(clazz)) {
                return (T) items.stream().map(j -> j.convert(findItemJavaType(itemType))).toList();
            }
            if (Set.class.isAssignableFrom(clazz)) {
                return (T) items.stream().map(j -> j.convert(findItemJavaType(itemType))).collect(toSet());
            }
            throw new JsonConversionException(this.toString(), clazz, null);
        }

        private Class<?> findItemJavaType(Type itemType) {
            String className = itemType.toString().split("\\<|\\>")[1];
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new JsonConversionException(this.toString(), null, e);
            }
        }

        @Override
        public <T> T convert(Class<T> clazz) {
            if (clazz.isArray()) {
                Object arr = Array.newInstance(clazz.getComponentType(), items.size());
                for (int i = 0; i < items.size(); i++) {
                    JSON json = items.get(i);
                    Object convertedItem = json.convert(clazz.componentType());
                    Array.set(arr, i, convertedItem);
                }
                return (T) arr;
            }

            if (List.class.isAssignableFrom(clazz)) {
                boolean isObject = (this.items.get(0) instanceof JSONObject);
                if (isObject)
                    return (T) this.items.stream()
                            .map(o -> (JSONObject) o)
                            .map(it -> it.convert(clazz.getComponentType()))
                            .toList();
                return (T) this.items.stream()
                        .map(it -> it.convert(clazz.getComponentType()))
                        .toList();
            }
            if (Set.class.isAssignableFrom(clazz)) {
                boolean isObject = (this.items.get(0) instanceof JSONObject);
                if (isObject)
                    return (T) this.items.stream()
                            .map(o -> (JSONObject) o)
                            .map(it -> it.convert(clazz.getComponentType()))
                            .collect(toSet());
                return (T) this.items.stream()
                        .map(it -> it.convert(clazz.getComponentType()))
                        .collect(toSet());
            }
            return null;
        }
    }

    static final class JSONObject extends JSON {
        Map<JSONString, JSON> map = new HashMap<>();
        List<JSONString> keysInOrder = new ArrayList<>();

        public JSONObject(String json) {
            json = json.replace("\n", "");
            String inside = json.substring(1, json.length() - 1);
            List<Integer> commaIndexes = findCommaIndexes(inside);
            splitByIndexes(inside, commaIndexes)
//                    .peek(insideJson -> System.out.println(insideJson))
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
            try {
                return tryToConvert(clazz);
            } catch (ReflectiveOperationException e) {
                throw new JsonConversionException(this.toString(), clazz, e);
            }
        }

        private <T> T tryToConvert(Class<T> clazz) throws ReflectiveOperationException {
            Constructor<T> constructor = clazz.getConstructor();
            T object = constructor.newInstance();

            SetterNameFinder finder = new SetterNameFinder(clazz);
            map.entrySet().stream()
                    .forEach(e -> {
                        String jsonPropertyName = e.getKey().value;
                        String javaFieldName = finder.getFieldName(jsonPropertyName);
                        var setterName = "set" + javaFieldName;
                        Arrays.stream(clazz.getMethods())
                                .filter(m -> m.getName().equalsIgnoreCase(setterName))
                                .filter(m -> m.getParameterCount() == 1)
                                .findFirst()
                                .ifPresent(method -> {
                                    try {
                                        method.invoke(object, findSetterParameterValue(e.getValue(), method));
                                    } catch (ReflectiveOperationException ex) {
                                        throw new JsonConversionException(this.toString(), clazz, ex);
                                    }
                                });
                    });
            return object;
        }

        private Object findSetterParameterValue(JSON e, Method method) {
            var parameterType = method.getParameterTypes()[0];
            Object o;
            if (isCollection(parameterType))
                o = ((JSONArray) e).convertCollection(parameterType, method.getGenericParameterTypes()[0]);
            else
                o = e.convert(parameterType);
            return o;
        }

        private boolean isCollection(Class<?> clazz) {
            return List.class.isAssignableFrom(clazz) || Set.class.isAssignableFrom(clazz);
        }
    }

    private static class JsonParseException extends RuntimeException {
        public JsonParseException(String message) {
            super(message);
        }
    }

    private static class JsonConversionException extends RuntimeException {
        public JsonConversionException(String json, Class<?> clazz, Throwable cause) {
            super("Could not convert to " + clazz + " the following JSON object: " + json, cause);
        }
    }
}