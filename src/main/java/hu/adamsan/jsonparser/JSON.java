package hu.adamsan.jsonparser;

import java.math.BigDecimal;

public class JSON {
    private JSON() {
    }

    public static JSON parse(String json) {
        json = json.trim();
        if (isString(json)) return new JSONString(json);
        if (isNumber(json)) return new JSONNumber(json);
        else return null;
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

    static class JSONString extends JSON {
        String value;

        public JSONString(String value) {
            this.value = value;
        }
    }

    static class JSONNumber extends JSON {
        BigDecimal value;

        public JSONNumber(String json) {
            try {
                this.value = BigDecimal.valueOf(Integer.parseInt(json));
            } catch (NumberFormatException ex) {
                this.value = BigDecimal.valueOf(Double.parseDouble(json));
            }
        }
    }
}
