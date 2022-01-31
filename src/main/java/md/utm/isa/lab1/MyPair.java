package md.utm.isa.lab1;

public class MyPair
{
    private final String key;
    private final String value;

    public MyPair(String aKey, String aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public String key()   { return key; }
    public String value() { return value; }
}