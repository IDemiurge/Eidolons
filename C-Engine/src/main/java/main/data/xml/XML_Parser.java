package main.data.xml;

import main.system.auxiliary.StringMaster;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

public class XML_Parser {

    public static final String ASCII = "ASCII";
    public static final String ASCII_OPEN = "ASCII_OPEN";
    public static final String ASCII_CLOSE = "ASCII_CLOSE";
    static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

    // or "ISO-8859-1" for ISO Latin 1

    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
    }

    public static String getCodeFromChar(String key) {
        return ASCII_OPEN + key.codePointAt(0) + ASCII_CLOSE;
        // return constructContainer(ListMaster.toStringList(key.getBytes()));
    }

    public static String getStringFromCode(String key) {
        List<String> list = StringMaster.openContainer(key);
        String result = "";
        for (String o : list) {
            o = StringMaster.getSubStringBetween(o, ASCII_OPEN, ASCII_CLOSE);
            try {
                // Character.toChars((int) StringMaster.getInteger(o)).
                result += Character.toString((char) (int) StringMaster.getInteger(o));
            } catch (Exception e) {
                return result;
            }
        }
        return result;
    }

    public static String encodeNonASCII(String v) {
        String result = "";
        for (char c : v.toCharArray()) {
            if (!asciiEncoder.canEncode(c)) {
                result += getCodeFromChar(c + "");
            } else {
                result += c;
            }
        }
        return result;
    }

}
