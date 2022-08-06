package hu.adamsan.jsonparser;

import java.math.BigDecimal;

public sealed class JSON {
    private JSON() {
    }

    public static JSON parse(String json) {
        json = json.trim();
        if (isString(json)) return new JSONString(json);
        if (isNumber(json)) return new JSONNumber(json);
        if (isNull(json)) return new JSONNull();
        else return null;
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

    static final class JSONString extends JSON {
        String value;

        public JSONString(String value) {
            this.value = value;
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
    }

    static final class JSONNull extends JSON {

    }
}
