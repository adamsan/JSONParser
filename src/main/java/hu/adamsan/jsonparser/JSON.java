package hu.adamsan.jsonparser;

public class JSON {
    private JSON() {
    }

    public static JSON parse(String json) {
        json = json.trim();
        if(isString(json)) return new JSONString(json);
        else return new JSONNumber(json);
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
        int value;

        public JSONNumber(String json){
            this.value = Integer.parseInt(json);
        }
    }
}
