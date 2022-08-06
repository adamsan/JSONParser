package hu.adamsan.jsonparser;

public class JSON {
    private JSON() {
    }

    public static JSONString parser(String s) {
        return new JSONString(s);
    }

    static class JSONString extends JSON {
        String value;

        public JSONString(String value) {
            this.value = value;
        }
    }
}
