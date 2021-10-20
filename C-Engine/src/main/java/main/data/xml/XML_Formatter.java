package main.data.xml;

import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.DataModel;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by JustMe on 5/20/2017.
 */
public class XML_Formatter {

    private static final String UNICODE = "UNICODE";
    private static final String CODEEND = "CODEEND";
    
    private static final CharSequence COMMA_CODE = "765";
    private static final CharSequence QUOTE_CODE = "986";
    private static final CharSequence COLON_CODE = "846";
    private static final CharSequence SEMICOLON_CODE = "845";
    private static final CharSequence OPEN_PARANTHESIS_CODE = "91";
    private static final CharSequence CLOSE_PARANTHESIS_CODE = "92";
    private static final String FIRST_CHAR = "FIRST_CHAR";
    protected static String replacedTextContent = "&";
    static String replaced = "~?[]><!@#$%^&*()-=//;+',\"`";
    static CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
    private static final Map<String, String> xmlFormatReplacements = new HashMap<>();
    private static final Map<String, String> cache = new HashMap<>();

    // or "ISO-8859-1" for ISO Latin 1

    static {
        for (char key : (replaced).toCharArray()) {
            String value =  getCodeFromChar("" + key);
            xmlFormatReplacements.put(("" + key), value);
        }
    }

    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
    }

    public static String getCodeFromChar(String key) {
        return UNICODE + key.codePointAt(0) + CODEEND;
        // Character.toChars(int).
    }

    public static String getStringFromCode(String key) {
        List<String> list = ContainerUtils.openContainer(key);
        StringBuilder result = new StringBuilder();
        for (String o : list) {
            o = StringMaster.getSubStringBetween(o, UNICODE, CODEEND);
            try {
                // Character.toChars((int) StringMaster.getInteger(o)).
                result.append((char) (int)NumberUtils.getIntParse(o));
            } catch (Exception e) {
                return result.toString();
            }
        }
        return result.toString();
    }

    public static String replaceNonASCII(String text, String v) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (!asciiEncoder.canEncode(c)) {
                result.append(v);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
        public static String encodeNonASCII(String v) {
        StringBuilder result = new StringBuilder();
        for (char c : v.toCharArray()) {
            if (!asciiEncoder.canEncode(c)) {
                result.append(getCodeFromChar(c + ""));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String formatDialogueLineContent(String string ) {
        return checkWrapInCDATA(replaceNonASCII(string, ("'"))); //Pattern.quote
    }
        public static String formatXmlTextContent(String string, VALUE value) {
        if (string.contains("/s")) {
            string = string.replace("/s", "\\s");
        }
        String result = string.replace(replacedTextContent,  getCodeFromChar(replacedTextContent));
        result = encodeNonASCII(result);
        if (isValueWrappedInCDATA(value))
            result = checkWrapInCDATA(result);
//        else
        if (isRepairMode())
            result = repair(result);
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
        return result.replaceAll("/s+", " ");//.replace("\n", "").replace("  ", " ");
    }

    private static boolean isRepairMode() {
        return true;
    }

    public static String restoreXmlNodeText(String s) {
        while (true) {
            if (!s.contains(UNICODE)) {
                break;
            }
            String code = StringMaster.getSubStringBetween(s, UNICODE,
                    CODEEND);
            try {
                s = s.replace(UNICODE + code + CODEEND, getStringFromCode(code));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                return s;
            }
        }
        return s;
    }

    public static String restoreXmlNodeName(String s) {
        if (s.contains(UNICODE)) {
        for (String x : xmlFormatReplacements.keySet()) {
            String code = xmlFormatReplacements.get(x);
            s = s.replace(code,  getStringFromCode(code));
        }
        }
        if (s.startsWith(FIRST_CHAR)) {
            s = s.substring(FIRST_CHAR.length());
        }
        return s.replace("_", " ");

    }

    public static String restoreXmlNodeNameOld(String s) {
        return s.replace(COMMA_CODE, ",").replace(COLON_CODE, ":").replace(SEMICOLON_CODE, ";")
                .replace(Strings.CODE_SLASH, "/").replace(Strings.CODE_BACK_SLASH, "/");
    }

    public static String formatStringForXmlNodeName(String s) {
        if (StringMaster.isEmpty(s)) {
            return "";
        }
        s = s.trim();
        String name = cache.get(s);
        if (name != null)
            return name;
        if (!Character.isAlphabetic(s.charAt(0))) {
            s = FIRST_CHAR + s;
        }
        s = s.replace("\uFFFD", "-");

        for (String x : xmlFormatReplacements.keySet()) {
            if (s.contains(x)   ) {
                s = s.replace(x, xmlFormatReplacements.get(x));
            } else
            if (s.contains(Pattern.quote(x))   ) {
                s = s.replace(Pattern.quote(x), xmlFormatReplacements.get(x));
            }
        }
        name = encodeNonASCII(s.replace(" ", "_"));
        cache.put(s, name);
        return name;
        // if (s.contains("'"))
        // main.system.auxiliary.LogMaster.src.main.system.log(1, s);
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
        return string.replace( getCodeFromChar(replacedTextContent),
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

    // public static String getStringFromCode(String key) {
    //     List<String> list = ContainerUtils.openContainer(key);
    //     StringBuilder result = new StringBuilder();
    //     for (String o : list) {
    //         o = StringMaster.getSubStringBetween(o, UNICODE, CODEEND);
    //         try {
    //             // Character.toChars((int) StringMaster.getInteger(o)).
    //             result.append((char) (int) NumberUtils.getInteger(o));
    //         } catch (Exception e) {
    //             return result.toString();
    //         }
    //     }
    //     return result.toString();
    // }
}
