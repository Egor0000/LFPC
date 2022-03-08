package md.utm.isa.utils;

public class CustomPair {
    private final String key;
    private final String value;

    public CustomPair(String key, String value) {
        this.key   = key;
        this.value = value;
    }

    public String key()   { return key; }
    public String value() { return value; }
}
