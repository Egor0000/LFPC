package md.utm.isa.utils;

public class StringUtil {

    public static String removeNonAscii(String line){
        return line.replaceAll("[^\\x20-\\x7E]", "?");
    }
}
