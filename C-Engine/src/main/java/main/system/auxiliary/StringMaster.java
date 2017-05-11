package main.system.auxiliary;

import main.content.ContentManager;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Converter;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.data.ListMaster;
import main.system.math.Formula;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StringMaster {

    public static final String SEPARATOR = ";";
    public static final String VAR_STRING = "^VAR";
    public static final String VAR_SEPARATOR = ",";
    public static final String PAIR_SEPARATOR = ":";
    public static final String ALT_PAIR_SEPARATOR = "=";
    public static final String CONTAINER_SEPARATOR = SEPARATOR;
    public static final String NETCODE_SEPARATOR = "~=~";
    public static final String FORMULA_REF_OPEN_CHAR = "{";
    public static final String FORMULA_REF_CLOSE_CHAR = "}";
    public static final String VARIABLES_OPEN_CHAR = "(";
    public static final String VARIABLES_CLOSE_CHAR = ")";
    public static final String FORMULA_FUNC_OPEN_CHAR = "[";
    public static final String FORMULA_FUNC_CLOSE_CHAR = "]";
    public static final String VAR_REF_OPEN_CHAR = "[";
    public static final String VAR_REF_CLOSE_CHAR = "]";
    public static final String FORMULA_COUNTER_OPEN_CHAR = "'";
    public static final String FORMULA_COUNTER_CLOSE_CHAR = "'";
    public static final String FORMULA_BASE_CHAR = "@";
    public static final String PARSE_REF_CHAR = "&";
    public static final String FORMULA_REF_SEPARATOR = "_";
    public static final String CURRENT = "C_";
    public static final String REGEN = "_REGEN";
    public static final String PERCENTAGE = "_PERCENTAGE";
    public static final String GATEWAY = "Gateway";
    public static final String BASE = "BASE_";
    public static final String BASE_FORMATTED = "Base ";
    public static final String PER_LEVEL = "_PER_LEVEL";
    public static final String COST = "_COST";
    public static final String FORMAT_CHAR = ".";
    public static final String REQUIREMENT = "_REQUIREMENT";
    public static final String OR = " OR ";
    public static final String REQ_VALUE_SEPARATOR = "=";
    public static final String COUNTER_CHAR = "$";
    public final static String BASE_CHAR = "@";
    public static final String COUNTER = " Counter";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String LINE_WRAP = "<%n>";
    public static final String DEFAULT = "DEFAULT_";
    public static final String REDUCTION = "_REDUCTION";
    public static final String PARTY_SUFFIX = "'s Party";
    public static final String PLAYTEST = "Playtest";
    public static final String ARCADE = "Arcade";
    public static final String PRESET = "Preset";
    public static final String CUSTOM = "Custom";
    public static final String PLAYABLE = "Playable";
    public static final String ADVENTURE = "Adventure";
    public static final String BATTLE_READY = "Battle Ready";
    public static final String BACKGROUND = "Background";
    public static final String VALUE_GROUP_CLOSE_CHAR = "]";
    public static final String VALUE_GROUP_OPEN_CHAR = "[";
    public static final CharSequence INVISIBLE_BUFF = " invisible";
    public static final CharSequence INVISIBLE_BUFF_CODE = "-";
    public static final String FEMALE_PREFIX = "w_";
    public static final String MASTERY = "Mastery";
    public static final String WOMAN = "WOMAN";
    public static final String STANDARD = "Standard";
    public static final String AND_SEPARATOR = "|";
    public static final String AND_PROPERTY_SEPARATOR = "]|[";
    public static final String DOUBLE_OR_SEPARATOR = "||";
    public static final String XOR_SEPARATOR = "xor";
    public static final String CONDITION_SEPARATOR = "//";
    public static final String NET_DATA_SEPARATOR = ">><<";
    public static final String UPGRADE_SEPARATOR = ">|<";
    public static final String SCORE = " Score";
    public static final String AND = " AND ";
    public static final String CODE_BACK_SLASH = "CODE_BACK_SLASH";
    public static final String CODE_SLASH = "CODE SLASH";
    public static final String COMMA_CODE = "COMMA CODE";
    public static final String VARIABLES_CLOSE_CHAR_CODE = "CLOSE PARENTHESIS";
    public static final String VARIABLES_OPEN_CHAR_CODE = "OPEN PARENTHESIS";
    public static final String FORMULA_REF_CLOSE_CHAR_CODE = "CLOSE CURLY BRACES";
    public static final String FORMULA_REF_OPEN_CHAR_CODE = "OPEN CURLY BRACES";
    public final static String MESSAGE_PREFIX_SUCCESS = "[+] ";
    public final static String MESSAGE_PREFIX_FAIL = "[-] ";
    public final static String MESSAGE_PREFIX_INFO = "[*] ";
    public final static String MESSAGE_PREFIX_ALERT = "[!] ";
    public final static String MESSAGE_PREFIX_ALARM = "[!!!] ";
    public final static String MESSAGE_PREFIX_MISC = "[...] ";
    public final static String MESSAGE_PREFIX_UNKNOWN = "[?] ";
    public final static String MESSAGE_PREFIX_PROCEEDING = "[>] ";
    public static final String MOD = "[mod]";
    public static final String SET = "[set]";
    public static final String REMOVE = "[remove]";
    public static final String VERSION_SEPARATOR = " - ";
    public static final String XML = ".xml";
    public static final String DATA_FORMAT = XML;
    public static final String IDENTITY = " Identity";
    public static final String ALIGNMENT = " Alignment";
    public static final String MAP_VALUE_KEY_SEPARATOR = null;
    private static final String UNICODE = "UNICODE";
    private static final String CODEEND = "CODEEND";
    private static final String COORDINATES_SEPARATOR = "-";
    private static final String ALT_SEPARATOR = ",";
    private static final String PATH_SEPARATOR = System.getProperty("file.separator");
    private static final String standard_symbols = "'-(),";
    private static final String PREFIX_SEPARATOR = "::";
    static Pattern pattern_ = Pattern.compile("_");
    static Pattern pattern_space = Pattern.compile(" ");
    public static final String UPGRADE = getWellFormattedString("UPGRADE");

    public static boolean compare(String string, String string2) {
        return compare(string, string2, false);
    }

    public static String capitalizeFirstLetter(String string) {
        if (!Character.isAlphabetic(string.charAt(0))) {
            return string;
        }
        try {
            String first = String.valueOf(string.charAt(0));
            return string.replaceFirst(first, first.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return string;
        }
    }

    public static boolean checkContainer(String container, String string) {
        return checkContainer(container, string, false);
    }

    public static boolean checkContainer(String container, String string, boolean strict) {
        for (String s1 : StringMaster.openContainer(container)) {
            if (compareByChar(s1, string, strict)) {
                return true;
            }
        }
        return false;

    }

    public static boolean contains(String string, String string2, boolean ignoreCase, boolean strict) {
        if (ignoreCase) {
            string = string.toUpperCase();
            string2 = string2.toUpperCase();
        }
        if (string.contains(string2)) {
            return true;
        }
        if (strict) {
            return false;
        }
        if (getWellFormattedString(string).contains(string2)) {
            return true;
        }
        if (string.contains(getWellFormattedString(string2))) {
            return true;
        }
        return getWellFormattedString(string).contains(getWellFormattedString(string2));

    }

    public static boolean contains(String string, String string2) {
        return contains(string, string2, true, true);
    }

    public static boolean compareByChar(String string, String anotherString) {
        return compareByChar(string, anotherString, true);
    }

    public static boolean compareByChar(String string, String anotherString, boolean strict) {
        return compareByChar(string, anotherString, strict, null);
    }

    public static boolean compareByChar(String string, String anotherString, boolean strict,
                                        String... ignored) {
        // a non-wellformatted version of compare?

        // Math.max(v1.length, v2.length);
        if (anotherString == null) {
            return string == null;
        }
        if (string == null) {
            return anotherString == null;
        }
        if (anotherString.isEmpty()) {
            return string.isEmpty();
        }
        if (string.isEmpty()) {
            return anotherString.isEmpty();
        }

        if (anotherString.length() != string.length()) {
//            if (strict) {
//                return false;
//            }
            // anotherString.replace("_", " "); costly!
            // string.replace("_", " ");

            if (anotherString.startsWith(" ")) {
                anotherString = anotherString.substring(1, anotherString.length());
                return compareByChar(string, anotherString, false);
            }
            if (string.startsWith(" ")) {
                string = string.substring(1, string.length());
                return compareByChar(string, anotherString, false);
            }
            if (anotherString.endsWith(" ")) {
                anotherString = anotherString.substring(0, anotherString.length() - 1);
                return compareByChar(string, anotherString, false);

            }
            if (string.endsWith(" ")) {
                string = string.substring(0, string.length() - 1);
                return compareByChar(string, anotherString, false);
            }
            return false;
        }
        char v1[] = string.toCharArray();
        char v2[] = anotherString.toCharArray();
        int n = v1.length;

        int i = -1;
        int j = -1;
        while (n-- != 0) { // length?
            i++;
            j++;
            if (v1[i] != v2[j]) { // string cache?
                String char1 = "" + v1[i];
                String char2 = "" + v2[j];
                if (!char1.toLowerCase().equals(char2.toLowerCase())) {// to
                    // char
                    // comparison?
                    if (ignored != null) {
                        for (String c : ignored) {
                            if (c.equalsIgnoreCase("" + v1[i])) {
                                continue;
                            } else if (c.equalsIgnoreCase("" + v2[j])) {
                                continue;
                            }
                        }
                    }
                    if (v1[i] == '_') {
                        if (v2[j] == ' ') {
                            continue;
                        }
                    }
                    if (v1[i] == ' ') {
                        if (v2[j] == '_') {
                            continue;
                        }
                    }
                    // preCheck special chars
                    return false;
                }

            }
        }
        return true;

    }

    public static boolean compareContainers(String val1, String val2, boolean strictContents) {
        return compareContainers(val1, val2, strictContents, CONTAINER_SEPARATOR);
    }

    public static boolean compareContainersIdentical(String val1, String val2,
                                                     boolean strictContents, String delimiter) {
        List<String> container = StringMaster.openContainer(val1, delimiter);
        List<String> container2 = StringMaster.openContainer(val2, delimiter);

        if (container.size() != container2.size()) {
            return false;
        }

        for (String s1 : container) {
            boolean result = false;
            for (String s : container2) {
                if (StringMaster.compareByChar(s1, s, strictContents)) {
                    result = true;
                }
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareContainers(String val1, String val2, boolean strictContents,
                                            String delimiter) {
        for (String s1 : StringMaster.openContainer(val1, delimiter)) {
            for (String s : StringMaster.openContainer(val2, delimiter)) {
                if (StringMaster.compareByChar(s1, s, strictContents)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean compare(String string, String string2, boolean strict) {
        if (isEmpty(string2)) {
            if (strict) {
                return isEmpty(string);
            }
            return true;
        }
        if (isEmpty(string)) {
            return isEmpty(string2);
        }

        boolean result = compareByChar(string, string2, strict);
        if (strict) {
            return result;
        } else if (result) {
            return true;
        } else {

            if (string.contains(CONTAINER_SEPARATOR) || string2.contains(CONTAINER_SEPARATOR)) {
                if (compareContainers(string, string2, true, CONTAINER_SEPARATOR)) {
                    return true;
                }
            }
            if (string.contains(AND_SEPARATOR) || string2.contains(AND_SEPARATOR)) {
                if (compareContainers(string, string2, true, AND_SEPARATOR)) {
                    return true;
                }
            }
            if (string.endsWith("s")) {
                if (removePlularEnding(string).equals(string2)) {
                    return true;
                }
            }
            if (string2.endsWith("s")) {
                if (removePlularEnding(string2).equals(string)) {
                    return true;
                }
            }

            if (contains(string, string2)) // TODO make it not strict if you
            // dare!
            {
                return true;
            }
            if (contains(string2, string)) {
                return true;
            }
        }
        return false;
    }

    public static String removePlularEnding(String textContent) {
        String str = "";
        int x = textContent.length();
        if (textContent.endsWith("s")) {
            str = textContent.substring(0, x - 1);
        }
        if (textContent.endsWith("ies")) {
            str = textContent.substring(0, x - 3);
            str += "y";
        }
        return str;
    }

    public static String toFormattedString(Object o) {
        if (o == null) {
            return "";
        }

        return getWellFormattedString(o.toString());
    }

    public static String getWellFormattedString(String s) {
        return StringMaster.getWellFormattedString(s, false);
    }

    public static String getCamelCase(String name) {
        String formatted = StringMaster.getWellFormattedString(name);
        return formatted.substring(0, 1).toLowerCase()
         + formatted.substring(1).replace(" ", "");
    }

    public static String getWellFormattedString(String s, boolean insertSpaceAfterCapitals) {
        if (isEmpty(s)) {
            return "";
        }
        String string = null;

        // if (s.contains("_") || s.contains(" ")) {
        if (s.contains(" ")) {
            StringBuilder builder = new StringBuilder(s.length() + 5);
            s = s.trim();
            for (String str : pattern_space.split(s)) {
                builder.append(getWellFormattedString(str) + " ");
            }
            string = builder.toString();
            string = string.substring(0, string.length() - 1);
            return string;
        }
        if (s.contains("_")) {
            StringBuilder builder = new StringBuilder(s.length() + 5);
            for (String str : pattern_.split(s)) {
                builder.append(getWellFormattedString(str) + " ");
            }
            string = builder.toString();
            string = string.substring(0, string.length() - 1);
            return string;
        }
        // } else {
        if (insertSpaceAfterCapitals) {
            StringBuilder builder = new StringBuilder(s.length() + 5);
            string = "";
            for (char c : s.toCharArray()) {
                if (!string.isEmpty()) {
                    if (Character.isUpperCase(c)) {
                        builder.append(" ");

                    }
                }
                builder.append(String.valueOf(c));
            }
            string = builder.toString();
        } else {
            string = capitalizeFirstLetter(s.toLowerCase());
        }
        // }
        return string;
    }

    public static String getXMLTypeName(String typename) {
        return typename.replace(" ", "_");
    }

    public static String getSeparator() {
        return SEPARATOR;
    }

    public static String getVarString() {
        return VAR_STRING;
    }

    public static String getVarSeparator() {
        return VAR_SEPARATOR;
    }

    public static String getSeparator(Boolean format) {
        if (format == null) {
            return "";
        }
        return format ? getSeparator() : getAltSeparator();
    }

    public static String getPairSeparator(Boolean format) {
        if (format == null) {
            return "";
        }
        return format ? getPairSeparator() : getAltPairSeparator();
    }

    public static String getPairSeparator() {
        return PAIR_SEPARATOR;
    }

    public static String getFormattedEnumString(String name) {
        return getWellFormattedString(name);
    }

    public static String getCoordinatesSeparator() {
        return COORDINATES_SEPARATOR;
    }

    public static String getDataUnitSeparator() {
        return getVarSeparator();
    }

    public static String getAltPairSeparator() {
        return ALT_PAIR_SEPARATOR;
    }

    public static String getAltSeparator() {
        return ALT_SEPARATOR;
    }

    public static List<String> split(String subString, String delimiter) {
        return split(subString, delimiter, true);

    }

    public static List<String> split(String containerString, String delimiter, boolean strict) {
        if (isEmpty(containerString)) {
            return Collections.emptyList();
        }
        if (!containerString.contains(delimiter)) {
            if (strict) {
                LinkedList<String> linkedList = new LinkedList<>();
                linkedList.add(containerString);
                return linkedList;
            } else {
                delimiter = delimiter.toUpperCase();
                if (!containerString.contains(delimiter)) {
                    delimiter = delimiter.toLowerCase();
                }
                if (!containerString.contains(delimiter)) {
                    delimiter = getWellFormattedString(delimiter);
                }
            }
        }
        List<String> list = new LinkedList<>(Arrays
 .asList(containerString.split(Pattern.quote(delimiter))));
        list.removeIf(s -> isEmpty(s));
        return list;
    }

    public static List<String> openContainer(String containerString, String separator) {
        return split(containerString, separator);
    }

    public static List<String> openFormattedContainer(String containerString) {
        return split(containerString, getFormattedContainerSeparator());
    }

    public static List<String> openContainer(String containerString) {
        return openContainer(containerString, CONTAINER_SEPARATOR);
    }

    public static String getContainerSeparator() {
        return getSeparator();
    }

    public static boolean isEmptyOrZero(String string) {
        if (string == null) {
            return true;
        }
        if (string.equals("0")) {
            return true;
        }
        return isEmpty(string);
    }

    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        string = string.trim();
        if (string.equals(ContentManager.DEFAULT_EMPTY_VALUE)) {
            return true;
        }
        if (string.equals(ContentManager.NEW_EMPTY_VALUE)) {
            return true;
        }
        return string.equals(ContentManager.OLD_EMPTY_VALUE);
    }

    public static void formatList(List<String> listData) {
        int i = 0;
        for (String item : listData) {

            listData.set(i, getWellFormattedString(item));
            i++;
        }
    }

    public static void formatResList(List<String> listData) {
        int i = 0;
        for (String item : listData) {
            item = item.substring(item.indexOf("\\", item.length()));
            listData.set(i, item);
            i++;
        }
    }

    public static String getNegativeFormula(String formula) {
        return "-1* (" + formula + ")";
    }

    public static String getPercentageAppend(int amount) {
        // TODO Auto-generated method stub
        return "*(100+" + amount + ")/100";
    }

    public static String wrapInCurlyBraces(String arg) {
        return "{" + arg + "}";
    }

    public static String wrapInBraces(String arg) {
        return "[" + arg + "]";
    }

    public static String wrapInParenthesis(String value) {
        return "(" + value + ")";
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
        return true;
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

    public static Integer getInteger(char value) {
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

    public static Integer getInteger(String value) {
        return getInteger(value, null);
    }
        public static Integer getInteger(String value, Ref ref) {
        if (!isInteger(value)) {
            try {
                return new Formula(value).getInt(ref==null  ? new Ref(): ref);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
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
                    result += getInteger(c) * Math.pow(10, value.length() - i - 1);
                }
            }
            if (negative) {
                result = -result; // for length
            }
            return result;
        } else {
            try {
                String result = "";
                for (char c : value.toCharArray()) {
                    if (c == ('.')) {
                        break;
                    }
                    if (c == ('-') || Character.isDigit(c)) {

                        result += c;
                    }
                }
                if (!result.isEmpty()) {
                    return Integer.valueOf(result);
                }

                // value = value.trim();
                // return Integer.valueOf(value);
            } catch (Exception e) {
                // int index = value.indexOf('.');
                // if (index != -1)
                // if (StringMaster.isInteger(value))
                // return Integer.valueOf(value.substring(0, index));
                e.printStackTrace();
            }
        }

        return 0;
    }

    private static boolean isFAST_INTEGER_MODE() {
        return true;
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

    // public static String getOrdinal(int i) {
    // switch (i){
    // case 1: return "First";
    // case 2: return "Second";
    // case 3: return "Third";
    // case 5: return "Fifth";
    // case 11: return "Eleventh";
    // }
    // if (i>20){
    //
    // }
    // return null;
    // }
    public static String removeParenthesis(String string) {
        return string.replace("(", "").replace(")", "");
    }

    public static String getObjTypeName(String name) {
        return getWellFormattedString(name).toLowerCase();
    }

    public static String toEnumFormat(String name) {
        return getEnumFormat(name);
    }

    public static String getEnumFormat(String name) {
        return name.toUpperCase().replace(" ", "_");
    }

    public static String getPathSeparator() {
        return PATH_SEPARATOR;
    }

    public static String clipEnding(String str, String CHAR) {
        if (!str.contains(CHAR)) {
            return str;
        }
        return str.substring(0, str.indexOf(CHAR));
    }

    public static String getStringXTimes(int i, String s) {
        StringBuilder string = new StringBuilder();
        while (i > 0) {
            string.append(s);
            i--;
        }
        return string.toString();
    }

    public static String getWhiteSpaces(int i) {
        return getStringXTimes(i, " ");
    }

    public static int toInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (Exception e) {

        }
        return -1;
    }

    public static String toStringForm(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            return entity.getName();
        }
        if (obj instanceof Class<?>) {
            Class<?> class1 = (Class<?>) obj;
            return class1.getSimpleName();
        }
        return obj.toString();
    }

    public static String cropFormat(String str) {
        if (str.indexOf(".") < 1) {
            return str;
        }
        return str.substring(0, str.lastIndexOf("."));
    }

    public static String getFormat(String str) {
        if (str.indexOf(".") < 1) {
            return "";
        }
        return str.substring(str.lastIndexOf("."));
    }

    public static String joinStringList(List<String> list, String divider, boolean cropLastDivider) {
        if (list == null) {
            return "";
        }
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        for (String str : list) {
            builder.append(str + divider);
        }
        String result = builder.toString();
        return (cropLastDivider) ? result.substring(0, result.lastIndexOf(divider)) : result;
    }

    public static String joinStringList(List<String> list, String divider) {
        return joinStringList(list, divider, true);
    }

    public static String getValueRef(KEYS objRef, VALUE valRef) {
        return getValueRef(objRef + "", valRef + "");
    }

    public static String getValueRef(String objRef, String valRef) {
        return FORMULA_REF_OPEN_CHAR + objRef + FORMULA_REF_SEPARATOR + valRef
         + FORMULA_REF_CLOSE_CHAR;
    }

    public static String constructContainer(List<String> list) {
        return joinStringList(list, getContainerSeparator(), false);
    }

    public static String constructStringContainer(List<?> list) {
        return constructStringContainer(list, getContainerSeparator());
    }

    public static String constructStringContainer(List<?> list, String separator) {
        return joinStringList(ListMaster.toStringList(list.toArray()), separator, false);
    }

    public static String constructEntityNameContainer(List<? extends Entity> list

    ) {
        return joinStringList(ListMaster.toStringList(true, list.toArray()),
         getContainerSeparator(), false);
    }

    public static List<String> convertToStringList(Collection<?> values) {
        List<String> list = new LinkedList<>();
        for (Object object : values) {
            if (object != null) {
                list.add(object.toString());
            }
        }

        return list;
    }

    public static List<String> convertToIdList(Collection<?> list) {

        List<String> idList = new LinkedList<>();
        if (list != null) {
            for (Object object : list) {
                if (object != null) {
                    idList.add(((Entity) object).getId() + "");
                }
            }
        }

        return idList;
    }

    public static List<String> toNameList(List<? extends Entity> list) {
        return toNameList(false, list);
    }

    public static List<String> toNameList(boolean base, List<? extends Entity> list) {
        List<String> nameList = new LinkedList<>();
        if (list != null) {
            for (Entity object : list) {
                if (object != null) {
                    nameList.add(base ? object.getProperty(G_PROPS.NAME, true) : object.getName());
                }
            }
        }
        return nameList;
    }

    public static Collection<Integer> convertToIdIntList(Collection<? extends Entity> list) {
        List<Integer> idList = new LinkedList<>();
        for (Entity object : list) {
            if (object != null) {
                idList.add(object.getId());
            }
        }

        return idList;
    }

    public static String getFormattedContainerString(String container) {
        if (container.endsWith(StringMaster.getContainerSeparator())) {
            container = container.substring(0, container.length() - 1);
        }
        return container.replace(StringMaster.getContainerSeparator(),
         getFormattedContainerSeparator());
    }

    public static String getFormattedContainerSeparator() {
        return ", ";
    }

    public static String getSubString(String string, String open, String close) {
        return getSubString(string, open, close, true);
        // try {
        // return string
        // .substring(string.indexOf(open), string.indexOf(close) + 1);
        // } catch (Exception e) {
        // return string;
        // }

    }

    public static String openNextParenthesis(String string) {
        // for each "(" inside the string, ignore the next ")"
        // String subString = getSubString(string, "(", ")", false);
        int indexOf = string.indexOf("(");
        if (indexOf == -1) {
            return string;
        }
        int endIndex = string.indexOf(")") + 1;
        if (endIndex == 0) {
            return string;
        }

        String subString = string.substring(indexOf, endIndex);

        while (getCount(subString, '(') > getCount(subString, ')')) { //
            endIndex = string.indexOf(")", endIndex) + 1;
            if (endIndex == 0) {
                break;
            }
            subString = string.substring(indexOf, endIndex);
        }
        return subString;

    }

    private static int getCount(String subString, char c) {
        int i = 0;
        for (char CHAR : subString.toCharArray()) {
            if (c == CHAR) {
                i++;
            }
        }
        return i;
    }

    public static String getSubString(boolean addTrailing, String string, String open,
                                      String close, Boolean inclusive) {
        int indexOf = string.indexOf(open);

        if (indexOf == -1) {
            return string;
        }
        if (inclusive == null) {
            indexOf = indexOf + open.length();
        } else if (!inclusive) {
            indexOf++;
        }
        int endIndex = string.indexOf(close);
        if (endIndex == -1) {
            return string;
        }
        if (inclusive != null) {
            if (inclusive) {
                endIndex++;
            }
        }

        if (addTrailing) {
            while (string.length() > endIndex) {
                if ((string.charAt(endIndex) + "").equals(close)) {
                    endIndex++;
                } else {
                    break;
                }
            }
        }

        return string.substring(indexOf, endIndex);
    }

    public static String getSubStringBetween(String string, String open, String close) {
        int indexOf = string.indexOf(open);

        if (indexOf == -1) {
            return string;
        }
        int endIndex = string.indexOf(close);
        if (endIndex == -1) {
            return string;
        }

        indexOf += open.length();

        return string.substring(indexOf, endIndex);
    }

    public static String getSubString(String string, char open, char close, boolean inclusive) {
        return getSubString(false, string, "" + open, "" + close, inclusive);

    }

    public static String getSubString(String string, String open, String close, boolean inclusive) {
        return getSubString(false, string, open, close, inclusive);

    }

    public static String cropParenthesises(String ref_substring) {
        return replaceLast(ref_substring.replaceFirst(Pattern.quote("("), ""), (")"), "");

    }

    public static String cropRef(String ref_substring) {
        return ref_substring.replace(FORMULA_REF_OPEN_CHAR, "").replace(FORMULA_REF_CLOSE_CHAR, "");
    }

    public static String cropVarRef(String ref_substring) {
        return ref_substring.replace(VAR_REF_OPEN_CHAR, "").replace(VAR_REF_CLOSE_CHAR, "");
    }

    public static String cropValueGroup(String string) {
        return string.replace(VALUE_GROUP_CLOSE_CHAR, "").replace(VALUE_GROUP_OPEN_CHAR, "");

    }

    public static boolean getBoolean(String value) {
        if (isEmpty(value)) {
            return false;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        return value.equals("1");
    }

    public static String getLastPathSegment(String path) {
        LinkedList<String> segments = new LinkedList<>(splitPath(path));
        return segments.getLast();
    }

    public static String getLastPart(String string, String separator) {
        if (!string.contains(separator)) {
            return "";
        }
        LinkedList<String> segments = new LinkedList<>(Arrays.asList(string.split(separator)));
        if (segments.isEmpty()) {
            return "";
        }
        return segments.getLast();
    }

    public static String cropLastPathSegment(String path, boolean cropPathSeparator) {
        String cropped = replaceLast(path, getLastPathSegment(path), "");
        if (!cropPathSeparator) {
            return cropped;
        }
        return replaceLast(cropped, "\\", "");
    }

    public static String cropLastPathSegment(String path) {
        return replaceLast(path, getLastPathSegment(path), "");
    }

    public static String cropLast(String name, String string) {
        int lastIndexOf = name.lastIndexOf(string);
        if (lastIndexOf > -1) {
            return replaceLast(name, name.substring(lastIndexOf), "");
        }
        return name;
    }

    public static List<String> splitPath(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.contains("/")) {
            if (!path.contains(Pattern.quote(PATH_SEPARATOR))) {
                return Arrays.asList(path.split("/"));
            }
        }
        return Arrays.asList(path.split(Pattern.quote(PATH_SEPARATOR)));
    }

    public static List<String> getPathSegments(String path) {
        return splitPath(path);
    }

    public static String replaceLast(String string, String regex, String replacement) {
        return replace(string, regex, replacement, true);
    }

    public static String replaceFirst(String string, String regex, String replacement) {
        return replace(string, regex, replacement, false);
    }

    public static String replace(String string, String regex, String replacement, boolean last) {
        int index = (last) ? string.lastIndexOf(regex) : string.indexOf(regex);
        if (index == -1) {
            index = (last) ? string.lastIndexOf(regex) : string.indexOf(regex.toUpperCase());
        }
        if (index == -1) {
            index = (last) ? string.lastIndexOf(regex) : string.indexOf(regex.toLowerCase());
        }
        if (index == -1) {
            index = (last) ? string.lastIndexOf(regex) : string.indexOf(regex.trim());
        }
        if (index == -1) {
            index = (last) ? string.lastIndexOf(regex) : string
             .indexOf(getWellFormattedString(regex));
        }
        if (index == -1) {
            return string;
        }
        String prefix = string.substring(0, index);
        String suffix = string.substring(index + regex.length(), string.length());
        return prefix + replacement + suffix;
    }

    public static String replace(boolean all, String string, String regex, String replacement) {
        if (all) {
            try {
                return string.replace(regex, replacement).replace(getWellFormattedString(regex),
                 replacement).replace(regex.toLowerCase(), replacement).replace(
                 regex.toUpperCase(), replacement);
            } catch (Exception e) {
                return string.replace(Pattern.quote(regex), replacement);
            }
        } else {
            return replaceFirst(string, regex, replacement);
        }

    }

    public static String cropVersion(String string) {
        while (!isEmpty(string)) {
            char c = string.charAt(string.length() - 1);
            if (Character.isDigit(c) || c == 'v') {
                string = string.substring(0, string.length() - 1);
            } else {
                break;
            }
        }
        return string;
    }

    public static String cropFileVariant(String string) {
        while (!isEmpty(string)) {
            char c = string.charAt(string.length() - 1);
            if (Character.isDigit(c) || c == ' ' || c == '_' || c == '(' || c == ')') {
                string = string.substring(0, string.length() - 1);
            } else {
                break;
            }
        }

        return string;
    }

    public static String buildPath(String... strings) {
        String result = "";
        for (String s : strings) {
            result += s + PATH_SEPARATOR;
        }
        return result.substring(0, result.length() - 1);
    }

    public static String build(String... strings) {
        String result = "";
        for (String s : strings) {
            result += s;
        }
        return result;
    }

    public static String cropLast(String str1, int i, String suffix) {
        while (i > 0 && str1.endsWith(suffix)) {
            str1 = cropLast(str1, 1);
            i--;
        }
        return str1;
    }

    public static String cropFirstSegment(String str1, String separator) {
        return cropFirst(str1, str1.indexOf(separator) + 1);
    }

    public static String cropFirst(String str1, int i) {
        if (isEmpty(str1)) {
            return str1;
        }
        if (str1.length() < i) {
            return str1;
        }
        return str1.substring(i, str1.length());
    }

    public static String cropLast(String str1, int i) {
        if (isEmpty(str1)) {
            return str1;
        }
        if (str1.length() < i) {
            return str1;
        }
        return str1.substring(0, str1.length() - i);
    }

    public static int compareSimilar(String firstString, String secondString) {
        int weight = 0;
        String longer = firstString.length() > secondString.length() ? firstString : secondString;
        String shorter = firstString.length() <= secondString.length() ? firstString : secondString;
        longer = longer.toLowerCase();
        shorter = shorter.toLowerCase();
        int i = 0;
        for (char c : longer.toCharArray()) {
            if (shorter.length() <= i) {
                break;
            }
            if (c == shorter.charAt(i)) {
                weight++;
            }
            i++;
        }
        return weight;
    }

    public static boolean checkSymbolsStandard(String newName) {
        for (char c : newName.toCharArray()) {
            if (!Character.isDigit(c)) {
                if (!Character.isAlphabetic(c)) {
                    if (c != ' ') {
                        if (!standard_symbols.contains(c + "")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static Integer getWeight(String string) {
        return getWeight(string, false);
    }

    public static Integer getWeight(String string, boolean inverse) {
        if (inverse) {
            return getInteger(getWeightItem(string, false));
        }
        return getInteger(StringMaster.cropParenthesises(VariableManager.getVarPartLast(string)));
    }

    public static String getWeightItem(String string, boolean inverse) {
        if (inverse) {
            return VariableManager.getVarPartLast(string);
        }
        return string.replace(VariableManager.getVarPartLast(string), "");
    }

    public static String getParamModString(String string, String mod) {
        return string + wrapInParenthesis(mod);
    }

    public static String getPossessive(String name) {
        if (name.endsWith("s")) {
            return name + "'";
        }
        return name + "'s";
    }

    public static String getLongestString(List<String> textLines) {
        int N = -1;
        String longest = null;
        for (String line : textLines) {
            int n = line.length();
            if (n > N) {
                N = n;
                longest = line;
            }
        }

        return longest;
    }

    public static String getModifierString(Integer mod) {
        String s = "";
        if (mod > 0) {
            s = "+";
        }
        s += mod + "%";
        return s;
    }

    public static String getBonusString(Integer bonus) {
        String s = "";
        if (bonus > 0) {
            s = "+";
        }
        s += bonus + "";
        return s;
    }

    public static String cropByLength(int maxLength, String string) {
        if (string.length() < maxLength) {
            return string;
        }
        return string.substring(0, maxLength);
    }

    public static Object getPrefix(String string) {
        String[] array = string.split(PREFIX_SEPARATOR);
        return array[0];
    }

    public static String getMessagePrefix(boolean fail, boolean me) {
        if (!me) {
            fail = !fail;
        }
        return fail ? StringMaster.MESSAGE_PREFIX_FAIL : StringMaster.MESSAGE_PREFIX_SUCCESS;
    }

    public static String getStringBeforeNumerals(String name) {
        int firstNumberIndex = getFirstNumberIndex(name);
        if (firstNumberIndex == -1) {
            firstNumberIndex = name.length() - 1;
        }
        return name.substring(0, firstNumberIndex);
    }

    public static String getStringBeforeNumeralsAndSymbols(String name) {
        return name.substring(0, getLastAlphabeticIndex(name));
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

    public static String getSegment(int i, String str, String delimiter) {
        String[] array = str.split(delimiter);
        if (array.length < i) {
            return null;
        }
        return array[i];
    }

    public static String getFormattedTypeName(String typeName) {
        if (typeName.endsWith(";")) {
            typeName = typeName.substring(0, typeName.length() - 1);
        }

        return typeName;
    }

    public static String getXmlNode(String xml, String nodeName) {
        String closeXmlFormatted = XML_Converter.closeXmlFormatted(nodeName);
        xml = xml.substring(xml.indexOf(XML_Converter.openXmlFormatted(nodeName)),
         closeXmlFormatted.length() + xml.lastIndexOf(closeXmlFormatted));
        return xml;
    }

    public static Double getDouble(String doubleParam) {
        doubleParam = doubleParam.replace("(", "").replace(")", "");
        try {
            return Double.valueOf(doubleParam);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return 0.0;
    }

    public static String getCodeFromChar(String key) {
        return UNICODE + key.codePointAt(0) + CODEEND;
        // Character.toChars(int).
    }

    public static String getStringFromCode(String key) {
        List<String> list = StringMaster.openContainer(key);
        String result = "";
        for (String o : list) {
            o = StringMaster.getSubStringBetween(o, UNICODE, CODEEND);
            try {
                // Character.toChars((int) StringMaster.getInteger(o)).
                result += Character.toString((char) (int) StringMaster.getInteger(o));
            } catch (Exception e) {
                return result;
            }
        }
        return result;
    }

    public static String getFormattedTimeString(int number, int digits) {
        String result = "" + number;
        if (digits < result.length()) {
            while (digits < result.length()) {
                result = result.substring(1);
            }
        } else {
            while (digits > result.length()) {
                result = "0" + result;
            }
        }
        return result;
    }

    public static String getFirstConsonants(String name, int n) {
        String string = "";
        for (String sub : StringMaster.openContainer(name, " ")) {
            string += ("" + sub.charAt(0));
        }
        return string.toUpperCase();
    }

    // public static String getTypeNameFormat(String generic) {
    // //1.
    // cropFormat(str)
    // return null;
    // }

    public static String getAbbreviation(String name) {
        String string = "";
        for (String sub : StringMaster.openContainer(name, " ")) {
            string += ("" + sub.charAt(0));
        }
        return string.toUpperCase();
    }

    public static String formatDisplayedName(String name) {
        if (name.contains(VERSION_SEPARATOR)) {
            return name.split(VERSION_SEPARATOR)[0];
        }
        return name;
    }

    public static boolean isFemalePortrait(String newPortrait) {
        return getLastPathSegment(newPortrait).startsWith(FEMALE_PREFIX);
    }

    public static String getStringFromEntityList(Collection<? extends Obj> engagers) {
        return constructContainer(DataManager.convertObjToStringList(engagers));
    }

    public static String getFirstItem(String string) {
        if (isEmpty(string)) {
            return "";
        }
        return openContainer(string).get(0);
    }

    public static String formatComparedProperty(String property) {
        return property.replace(";", "");
    }



    public static String removePreviousPathSegments(String string, String path) {
        String p = string.toLowerCase();
        path = path.toLowerCase();
        String prefix = "";
        if (p.contains(path)) {
            prefix = path;
        } else {
            for (String sub : getPathSegments(path)) {
                if (p.contains(path)) {
                    prefix = path;
                    break;
                }

                path = cropFirstSegment(path, PATH_SEPARATOR);

            }
        }


//        for (String sub : getPathSegments(path)) {
//            if (portrait.startsWith(sub)) {
//                break;
//            }
//            prefix += sub + PATH_SEPARATOR;
//        }


//        if (string.startsWith(PATH_SEPARATOR)) {
//            string = string.substring(1);
//        }
//        final String portrait = string.toLowerCase();
//        path = path.toLowerCase();
//        final List<String> segments = getPathSegments(path);
//        String prefix = buildPartsIf(segments,
//         PATH_SEPARATOR, false, (String sub) ->
//          getPathSegments(portrait).indexOf(sub) < 0
//           ||
//           getPathSegments(portrait).indexOf(sub) < segments.indexOf(sub)
//         //TODO THIS IS NOT GUARANTEED TO WORK!!
//
//        );

        return p.replace(prefix, "");
    }

    public static String addMissingPathSegments(String string, String path) {
        final String p = string.toLowerCase();
        path = path.toLowerCase();
        String prefix = buildPartsIf(getPathSegments(path),
         PATH_SEPARATOR, true, (String sub) -> p.startsWith(sub));

        return prefix + p;
    }

    private static String buildPartsIf(List<String> segments,
                                       String separator,
                                       boolean breakOnFalse,
                                       Predicate<String> predicate) {
        StringBuilder builder = new StringBuilder(50);
        for (String sub : segments) {
            if (predicate.test(sub)) {
                if (breakOnFalse) {
                    break;
                } else {
                    continue;
                }
            }
            builder.append(sub + separator);
        }
        return builder.toString();
    }

    public static String wrap(String wrap, String string) {
        return wrap + string + wrap;
    }

    public enum STD_DEITY_TYPE_NAMES {
        Faithless

    }

    public enum STD_TYPE_NAMES {
        Cell

    }

}
