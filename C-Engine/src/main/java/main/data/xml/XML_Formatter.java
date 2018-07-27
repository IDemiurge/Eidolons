package main.data.xml;

import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.DataModel;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.NumberUtils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by JustMe on 5/20/2017.
 */
public class XML_Formatter {
    public static final String ASCII = "ASCII";
    public static final String ASCII_OPEN = "ASCII_OPEN";
    public static final String ASCII_CLOSE = "ASCII_CLOSE";
    private static final CharSequence COMMA_CODE = "765";
    private static final CharSequence QUOTE_CODE = "986";
    private static final CharSequence COLON_CODE = "846";
    private static final CharSequence SEMICOLON_CODE = "845";
    private static final CharSequence OPEN_PARANTHESIS_CODE = "91";
    private static final CharSequence CLOSE_PARANTHESIS_CODE = "92";
    private static final String FIRST_CHAR = "FIRST_CHAR";
    protected static String replacedTextContent = "&";
    static String replaced = "~?[]><!@#$%^&*()-=\\/;+',\"`";
    static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    private static Map<String, String> xmlFormatReplacements = new HashMap<>();
    private static Map<String, String> cache = new HashMap<>();

    // or "ISO-8859-1" for ISO Latin 1

    static {
        int i = 0;
        for (char key : (replaced).toCharArray()) {
            String value = NumberUtils.getCodeFromChar("" + key);
            xmlFormatReplacements.put(("" + key), value);
        }
    }

    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
    }

    public static String getCodeFromChar(String key) {
        return ASCII_OPEN + key.codePointAt(0) + ASCII_CLOSE;
        // return constructContainer(ListMaster.toStringList(key.getBytes()));
    }

    public static String getStringFromCode(String key) {
        List<String> list = ContainerUtils.openContainer(key);
        String result = "";
        for (String o : list) {
            o = StringMaster.getSubStringBetween(o, ASCII_OPEN, ASCII_CLOSE);
            try {
                // Character.toChars((int) StringMaster.getInteger(o)).
                result += Character.toString((char) (int) NumberUtils.getInteger(o));
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

    public static String formatXmlTextContent(String string, VALUE value) {
        String result = string.replace(replacedTextContent, NumberUtils
         .getCodeFromChar(replacedTextContent));
        result = encodeNonASCII(result);
        if (isRepairMode())
            result = repair(result);
        if (isValueWrappedInCDATA(value))
            result = checkWrapInCDATA(result);
        return result;
    }

    private static boolean isValueWrappedInCDATA(VALUE value) {
        if (value instanceof PARAMETER)
            return false;
        return value != G_PROPS.ABILITIES;
    }

    private static String checkWrapInCDATA(String result) {
        if (result.length() < 80) return result;
        return wrapInCDATA(result);
    }

    public static String wrapInCDATA(String result) {
        return "<![CDATA[" + result + "]]>";
    }


    private static String repair(String result) {
        return result.replaceAll("\\s+", " ");//.replace("\n", "").replace("  ", " ");
    }

    private static boolean isRepairMode() {
        return true;
    }

    public static String restoreXmlNodeText(String s) {
        while (true) {
            if (!s.contains(ASCII_OPEN)) {
                break;
            }
            String code = StringMaster.getSubStringBetween(s, ASCII_OPEN,
             ASCII_CLOSE);
            try {
                s = s.replace(ASCII_OPEN + code + ASCII_CLOSE, NumberUtils
                 .getStringFromCode(code));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return s;
            }
        }
        return s;
    }

    public static String restoreXmlNodeName(String s) {
        for (String x : xmlFormatReplacements.keySet()) {
            String code = xmlFormatReplacements.get(x);
            s = s.replace(code, NumberUtils.getStringFromCode(code));
        }
        if (s.startsWith(FIRST_CHAR)) {
            s = s.substring(FIRST_CHAR.length());
        }
        return s.replace("_", " ");

    }

    public static String restoreXmlNodeNameOld(String s) {
        return s.replace(COMMA_CODE, ",").replace(COLON_CODE, ":").replace(SEMICOLON_CODE, ";")
         .replace(StringMaster.CODE_SLASH, "/").replace(StringMaster.CODE_BACK_SLASH, "\\");
    }

    public static String formatStringForXmlNodeName(String s) {
        if (StringMaster.isEmpty(s)) {
            return "";
        }

        String name = cache.get(s);
        if (name != null)
            return name;
        if (!Character.isAlphabetic(s.charAt(0))) {
            s = FIRST_CHAR + s;
        }
        s = s.replace("\uFFFD", "-");
        for (String x : xmlFormatReplacements.keySet()) {
            s = s.replace(x, xmlFormatReplacements.get(x));
            s = s.replace(Pattern.quote(x), xmlFormatReplacements.get(x));
        }
        name = encodeNonASCII(s.replace(" ", "_"));
        cache.put(s, name);
        return name;
        // if (s.contains("'"))
        // main.system.auxiliary.LogMaster.log(1, s);
        // return s.replace(",", COMMA_CODE).replace("'", "_").replace(":",
        // COLON_CODE).replace(";",
        // SEMICOLON_CODE).replace("#", "_").replace("!", "_").replace("(",
        // "_").replace(")",
        // "_").replace(" ", "_");

        // return s.replace(",", COMMA_CODE).replace("'",
        // QUOTE_CODE).replace(":", COLON_CODE)
        // .replace("(", OPEN_PARANTHESIS_CODE)
        // .replace(")", CLOSE_PARANTHESIS_CODE).replace(" ", "_");
    }

    private static String restoreXmlTextContent(String string) {
        return string.replace(NumberUtils.getCodeFromChar(replacedTextContent),
         replacedTextContent);
    }

    public static String getValueNode(DataModel obj, VALUE value) {
//        if (value instanceof  PROPERTY) {
//            PROPERTY property = ((PROPERTY) value);
//        }
//        else {
//        }
        return XML_Converter.wrap(value.getName(),
         formatXmlTextContent(obj.getValue(value), value));
    }
}
