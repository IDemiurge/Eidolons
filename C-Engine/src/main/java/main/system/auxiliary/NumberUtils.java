package main.system.auxiliary;

import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;
import main.data.xml.XML_Converter;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.math.Formula;
import main.system.math.MathMaster;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by JustMe on 7/24/2018.
 */
public class NumberUtils {

    public static String getNegativeFormula(String formula) {
        return "-1* (" + formula + ")";
    }

    public static boolean isNumber(String value, boolean integer) {
        if (value == null) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }
        int i = 0;
        boolean _float = false;
        for (char c : value.toCharArray()) {
            if (_float) {
                if (integer) {
                    if (c != '0') {
                        return false;
                    }
                }
            }
            if (!Character.isDigit(c)) {
                if (c == '-') {
                    if (i == 0) {
                        continue;
                    }
                }
                if (c == '.') {
                    if (_float) // no two periods
                    {
                        return false;
                    }
                    if (i > 0) {
                        _float = true;
                        continue;
                    } else if (integer) {
                        return false;
                    }
                }

                return false;
            }

            i++;
        }
        return i > 0;
    }

    public static Boolean isIntegerOrNumber(String value) {
        boolean result = isNumber(value, true);
        if (result) {
            return true;
        }
        result = isNumber(value, false);
        if (result) {
            return false;
        }
        return null;

    }

    public static boolean isInteger(String value) {
        return isNumber(value, true);
    }

    public static Integer getIntParse(char value) {
        switch (value) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
        }
        return null;
    }

    public static Integer getIntParse(String value) {
        return getIntParse(value, null);
    }

    public static Integer getInt(String value) {
        return getIntParse(value, null, false);
    }

    public static Integer getIntParse(String value, Ref ref) {
        return getIntParse(value, ref, true);
    }

    public static Integer getIntParse(String value, Ref ref, boolean parse) {
        if (value == null)
            return 0;
            if (!isInteger(value)) {
                if (!parse)
                    return 0;
                return new Formula(value).getInt(ref == null ? new Ref() : ref);
            }
        if (value.contains(".")) {
            value = value.split(Pattern.quote("."))[0];
        }

        if (isFAST_INTEGER_MODE()) {
            int result = 0;
            boolean negative = false;
            for (int i = 0; i < value.length(); i++) {
                char c = value.toCharArray()[i];
                if (c == ('.')) {
                    break;
                }
                if (c == ('-')) {
                    negative = true;
                } else {
                    result += getIntParse(c) * Math.pow(10, value.length() - i - 1);
                }
            }
            if (negative) {
                result = -result; // for length
            }
            return result;
        } else {
            StringBuilder result = new StringBuilder();
            for (char c : value.toCharArray()) {
                if (c == ('.')) {
                    break;
                }
                if (c == ('-') || Character.isDigit(c)) {

                    result.append(c);
                }
            }
            if (result.length() > 0) {
                return Integer.valueOf(result.toString());
            }
        }

        return 0;
    }

    private static boolean isFAST_INTEGER_MODE() {
        return true;
    }

    public static String getOrdinal(int number) {
        return number + getOrdinalEnding(number);
    }

    public static String getOrdinalEnding(int number) {
        int lastDigit = number % 10;
        if (lastDigit == 1) {
            return "st";
        }
        if (lastDigit == 2) {
            return "nd";
        }
        if (lastDigit == 3) {
            return "rd";
        }
        return "th";
    }

    public static int getLastAlphabeticIndex(String name) {
        int i = 0;
        for (char l : name.toCharArray()) {
            if (!Character.isAlphabetic(l)) {
                return i;
            }
            i++;
        }
        return name.length() - 1;
    }

    public static int getFirstNumberIndex(String name) {
        int i = 0;
        for (char l : name.toCharArray()) {
            if (Character.isDigit(l)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static String getXmlNode(String xml, String nodeName) {
        String closeXmlFormatted = XML_Converter.closeXmlFormatted(nodeName);
        xml = xml.substring(xml.indexOf(XML_Converter.openXmlFormatted(nodeName)),
                closeXmlFormatted.length() + xml.lastIndexOf(closeXmlFormatted));
        return xml;
    }

    public static Double getDouble(String doubleParam) {
        if (StringMaster.isEmpty(doubleParam)) {
            return 0.0;
        }
        doubleParam = doubleParam.replace("(", "").replace(")", "");
        try {
            return Double.valueOf(doubleParam);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return 0.0;
    }

    public static Float getFloat(String floatParam) {
        if (StringMaster.isEmpty(floatParam)) {
            return 0f;
        }
        floatParam = floatParam.replace("(", "").replace(")", "");
        try {
            return Float.valueOf(floatParam);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return 0.0f;
    }

    public static String getNumericSuffix(String indexString) {
        StringBuilder result = new StringBuilder();
        for (int i = indexString.length() - 1; i >= 0; i--) {
            char c = indexString.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            } else
                break;
        }
        return result.reverse().toString();
    }

    public static String prependZeroes(int number, int digits) {
        return getFormattedTimeString(number, digits);
    }

    public static String getFormattedTimeString(int number, int digits) {
        StringBuilder result = new StringBuilder("" + number);
        if (digits < result.length()) {
            while (digits < result.length()) {
                result = new StringBuilder(result.substring(1));
            }
        } else {
            while (digits > result.length()) {
                result.insert(0, "0");
            }
        }
        return result.toString();
    }

    public static String getRoman(int level) {
        int x = level / 10;
        int v = level / 5;
        int i = level % 5;
        String result = "";
        result += StringMaster.getStringXTimes(x, "X");
        result += StringMaster.getStringXTimes(v, "V");
        if (i == 4)
            result += "IV";
        else
            result += StringMaster.getStringXTimes(i, "I");
        return result;
    }

    public static String getCurrentOutOfBaseVal(Entity entity, PARAMETER parameter) {
        return entity.getIntParam(ContentValsManager.getCurrentParam(parameter)) + "/"
                + entity.getIntParam(parameter);
    }

    public static String formatFloat(int digitsAfterPeriod, float v) {
        return
                String.format(java.util.Locale.US, "%." +
                        digitsAfterPeriod +
                        "f", v);
    }

    public static float getFloatWithDigitsAfterPeriod(float time, int i) {
        return
                MathMaster.getFloatWithDigitsAfterPeriod(i, time);
    }

    public static Set<Integer> toIntegers(Collection<String> ids) {
        Set<Integer> set = new LinkedHashSet<>();
        for (String id : ids) {
            set.add(Integer.valueOf(id));
        }
        return set;
    }

    //TODO  >>> Review <<<
    public static int[] getCoordinatesFromString(String s) {
         getIntParse(Coordinates. splitCoordinateString(s)[0].trim());
         getIntParse(Coordinates.splitCoordinateString(s)[1].trim());
        return new int[0];
    }
}
