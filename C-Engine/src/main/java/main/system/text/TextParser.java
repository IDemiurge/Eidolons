package main.system.text;

import main.ability.AbilityObj;
import main.ability.AbilityType;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.core.game.Game;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;
import main.system.math.Parameter;
import main.system.math.Property;

import java.util.List;

public class TextParser {

    public final static int ACTIVE_PARSING_CODE = 1;
    public final static int ABILITY_PARSING_CODE = 2;
    public final static int INFO_PARSING_CODE = 3;
    public final static int BUFF_PARSING_CODE = 4;
    public final static int TOOLTIP_PARSING_CODE = 5;
    public final static int VARIABLE_PARSING_CODE = 6;
    private static final String DEFAULT_VARS = "Formula;Duration";
    private static boolean activeParsing;
    private static boolean abilityParsing;
    private static boolean infoParsing;
    private static boolean buffParsing;
    private static boolean tooltipParsing;
    private static boolean variableParsing;
    private static boolean xmlParsing; // array
    // ?
    private static Entity entity;

    public synchronized static String parse(String text, Ref ref, Integer... parsingType) {
        for (Integer p : parsingType) {
            switch (p) {
                case ACTIVE_PARSING_CODE:
                    activeParsing = true;
                    break;
                case ABILITY_PARSING_CODE:
                    abilityParsing = true;
                    break;
                case INFO_PARSING_CODE:
                    infoParsing = true;
                    break;
                case BUFF_PARSING_CODE:
                    buffParsing = true;
                    break;
                case TOOLTIP_PARSING_CODE:
                    tooltipParsing = true;
                    break;
                case VARIABLE_PARSING_CODE:
                    variableParsing = true;
                    break;
            }
        }
        String result = null;
        try {
            result = parse(text, ref);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return result;
    }

    private static void resetSwitches() {
        activeParsing = false;
        abilityParsing = false;
        infoParsing = false;
        buffParsing = false;
        tooltipParsing = false;
        variableParsing = false;
        xmlParsing = false;
        entity = null;
    }

    public synchronized static String parseXmlVarRefs(AbilityType newType, String text) {
        xmlParsing = true;
        return parseVarRefs(newType, text);
    }

    public synchronized static String parseVarRefs(AbilityType newType, String text) {
        entity = newType;
        variableParsing = true;
        return parse(text, null);
    }

    public synchronized static String parse(String text, Ref ref) {
        if (ref == null && entity == null) {
            return text;
        }
        if (!activeParsing && !abilityParsing && !buffParsing) // TODO lame!
        {
            infoParsing = true;
        }
        String varPart = VariableManager.getVarPart(text);
        String buffer = text.replace(varPart, "");
        if (!StringMaster.isEmpty(varPart) && variableParsing && !xmlParsing) {
            text = StringMaster.cropParenthesises(varPart);

        }

        try {
            char[] charArray = text.toCharArray();
            for (char ch : charArray) {
                String ref_substring = null;
                String result = null;
                if (ch == StringMaster.VARIABLES_OPEN_CHAR.charAt(0) && !xmlParsing
                 && variableParsing
                ) {
                    ref_substring = StringMaster.openNextParenthesis(text);
                    // StringMaster.getSubString(true, text,
                    // StringMaster.VARIABLES_OPEN_CHAR,
                    // StringMaster.VARIABLES_CLOSE_CHAR, true);
                    if (ref_substring.equals(text)) {
                        continue;
                    }
                    result = parseVariables(ref_substring);
                }
                String str =  xmlParsing ?StringMaster.VAR_REF_OPEN_CHAR : StringMaster.FORMULA_REF_OPEN_CHAR;
                if ( ch == str.charAt(0)) {
                    if (!text.contains(str)) {
                        break;
                    }
                    if (xmlParsing) {
                        ref_substring = StringMaster.getSubString(text,
                         StringMaster.VAR_REF_OPEN_CHAR, StringMaster.VAR_REF_CLOSE_CHAR);
                    } else {
                        ref_substring = StringMaster.getSubString(text,
                         StringMaster.FORMULA_REF_OPEN_CHAR,
                         StringMaster.FORMULA_REF_CLOSE_CHAR);
                    }

                    try {
                        result = parseRef(ref_substring, ref);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                        LogMaster.log(LogMaster.MATH_DEBUG, ref_substring
                         + " failed to parse ref for text: " + text);
                        return buffer;
                    }

                }
                if (result != null) {
                    result = replaceRefBraces(result);
                    text = text.replace(ref_substring, (result));
                }
            }

            if (!StringMaster.isEmpty(varPart) && variableParsing && !xmlParsing) {
                text = buffer + StringMaster.wrapInParenthesis(replaceVarBraceCodes(text));
            }

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            resetSwitches();
        }
        return replaceRefBraceCodes(text);
        // return text;
    }

    public static String extractBraceEnclosed(String text) {
        char[] charArray = text.toCharArray();
        StringBuilder enclosed = new StringBuilder();
        boolean opened = false;
        for (char ch : charArray) {
            if (opened) {
                enclosed.append(ch);
            }
            if (ch == '[') {
                opened = true;
            }
            if (ch == ']') {
                opened = false;
                enclosed.append(";");
            }

        }
        return enclosed.substring(0, enclosed.length() - 1);
    }

	/*
     *
	 * Refactor Multi-layer ability vars - what's the challenge? I have to
	 * replace all commas with codes at each Variable Parsing phase... E.G. -
	 * Roll(Mind Affecting,
	 * Random(ForceAction(TurnClockwise),ForceAction(TurnAnticlockwise))) I do
	 * replace codes back with commas upon var init... So the Roll Effect will
	 * have a normal Random(... , ...), true, and it will parse again and get
	 * the right VarType; but what is ForceAction also had 2+ args? I can see no
	 * problem, honestly, as long as Random or whatever ability I use also
	 * parses correctly in turn! That is, EffectMaster should be smarter!
	 */

    private static String parseVarRef(String ref_substring) {
        String value = (xmlParsing) ? StringMaster.cropVarRef(ref_substring) : StringMaster
         .cropRef(ref_substring);
        if (!NumberUtils.isInteger(value)) {
            return ref_substring;
        }
        int index = NumberUtils.getIntParse(value) - 1;
        String varProp = entity.getProperty(G_PROPS.VARIABLES);
        return ContainerUtils.openContainer(varProp).get(index);
    }

    // perhaps I should refactor into method per parse_type!
    private static String parseRef(String ref_substring, Ref ref) {
        if (ref == null) {
            return parseVarRef(ref_substring);
        }
        String value = StringMaster.cropRef(ref_substring);
        if (!NumberUtils.isInteger(value)) {
            if (isAbilityParsing()) {
                return ref_substring;
            }
        }
        Integer id = null ;
        if (isActiveParsing()) {
            id = ref.getId(KEYS.ACTIVE);
        }
        if (id==null ) {
            try {
                id = ref.getId(KEYS.INFO);
            } catch (Exception e) {

            }
        }
        Game game = ref.getGame();
//        if (id == 0) {
//            if (game.isRunning()) {
//                id = game.getManager().getInfoObj().getId();
//            }
//        }
        // else if (CharacterCreator.isRunning())

        String replacement = ref_substring;
        Entity entity = ref.getInfoEntity();
        if (entity == null) {
            if (ref.getSourceObj()==null) {
                entity = game.getTypeById(ref.getSource());
            } else
            if (ref.getSourceObj().isSimulation() && !isBuffParsing() && !isAbilityParsing()) {
                entity = game.getTypeById(id);
            } else {
                entity = game.getObjectById(id);
            }
        }
        if (entity == null) {
            return ref_substring;
        }
        if (entity instanceof AbilityObj) {
            entity = entity.getType();
        }
        if (NumberUtils.isInteger(value)) {
            int index = NumberUtils.getIntParse(value) - 1;
            String varProp = entity.getProperty(G_PROPS.VARIABLES);
            if (StringMaster.isEmpty(varProp)) {
                varProp = DEFAULT_VARS;
            }
            boolean containerFormula = false;

            if (!entity.getParam("FORMULA").isEmpty()) {
                // StringMaster.compare(varProp.replace(";", ""), "FORMULA",
                // true)) {
                varProp = entity.getParam("FORMULA");
                containerFormula = true;
            }

            List<String> openContainer = ContainerUtils.openContainer(varProp);

            if (openContainer.size() > index) {
                VALUE val = null;
                if (!containerFormula && !isBuffParsing()) {
                    val = ContentValsManager.getValue(openContainer.get(index));
                    replacement = entity.getValue(val);
                } else {
                    replacement = openContainer.get(index);
                }
                if (containerFormula || val instanceof PARAMETER) {
                    if (NumberUtils.isInteger(replacement)) {
                        return replacement;
                    }
                    try {
                        if (isAbilityParsing())
                        // if (replacement.contains(",")) {
                        // // TODO all function names!
                        // if (replacement.toLowerCase().contains("min")
                        // || replacement.toLowerCase().contains(
                        // "max")) {
                        // // get the insides of the formula?
                        // // parts = replacement.split(regex);
                        // // parts[1] = parts[1].
                        // //
                        // // replacement = parts[0] + parts[1];
                        // return replacement.replace(",",
                        // StringMaster.COMMA_CODE);
                        // }
                        // } else
                        {
                            return replacement.replace(",", StringMaster.COMMA_CODE);
                        }
                        if (game.isSimulation()) {
                            replacement = new Formula(replacement).getInt(ref) + " ("
                             + formatFormula(replacement) + ")";
                        } else {
                            replacement = new Formula(replacement).getInt(ref) + "";
                        }
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }

                } else {
                    String result = new Property(true, replacement).getStr(ref);
                    if (StringMaster.isEmpty(result)) {
                        replacement = new Parameter(replacement).getInt(ref) + "";
                    } else {
                        replacement = result;
                    }
                }
            }
        } else {
            if (!isAbilityParsing()) {
                if (NumberUtils.isInteger(replacement)) {
                    return replacement;
                }
                if ((  game.isSimulation()) && !tooltipParsing) {
                    Integer VAL = new Formula(replacement).getInt(ref);
                    replacement = VAL + " (" + replacement + ")";
                } else {
                    replacement = new Formula(replacement).getInt(ref) + "";// TODO
                }
                // props!
            }
        }

        return replacement;
    }

    private static String parseVariables(String varString) {
        // if (isAbilityParsing())
        // if (varString.contains(StringMaster.VAR_SEPARATOR)) {
        // if (varString.toLowerCase().contains("min")
        // || varString.toLowerCase().contains("max")) {
        // get the insides of the formula?
        // parts = replacement.split(regex);
        // parts[1] = parts[1].
        //
        // replacement = parts[0] + parts[1];
        return replaceVarBraces(varString.replace(StringMaster.VAR_SEPARATOR,
         StringMaster.COMMA_CODE));

    }

    private static String replaceCommaCodes(String varString) {
        return replaceVarBraces(varString.replace(StringMaster.COMMA_CODE,
         StringMaster.VAR_SEPARATOR));
    }

    public static String replaceCodes(String varString) {
        return replaceRefBraceCodes(replaceVarBraceCodes(replaceCommaCodes(varString)));
    }

    private static String replaceRefBraceCodes(String string) {
        return string.replace(StringMaster.FORMULA_REF_CLOSE_CHAR_CODE,
         StringMaster.FORMULA_REF_CLOSE_CHAR).replace(
         StringMaster.FORMULA_REF_OPEN_CHAR_CODE, StringMaster.FORMULA_REF_OPEN_CHAR);
    }

    private static String replaceRefBraces(String string) {
        return string.replace(StringMaster.FORMULA_REF_CLOSE_CHAR,
         StringMaster.FORMULA_REF_CLOSE_CHAR_CODE).replace(
         StringMaster.FORMULA_REF_OPEN_CHAR, StringMaster.FORMULA_REF_OPEN_CHAR_CODE);
    }

    private static String replaceVarBraceCodes(String string) {
        return string.replace(StringMaster.VARIABLES_CLOSE_CHAR_CODE,
         StringMaster.VARIABLES_CLOSE_CHAR).replace(StringMaster.VARIABLES_OPEN_CHAR_CODE,
         StringMaster.VARIABLES_OPEN_CHAR);
    }

    private static String replaceVarBraces(String string) {
        return string.replace(StringMaster.VARIABLES_CLOSE_CHAR,
         StringMaster.VARIABLES_CLOSE_CHAR_CODE).replace(StringMaster.VARIABLES_OPEN_CHAR,
         StringMaster.VARIABLES_OPEN_CHAR_CODE);
    }

    private static String formatString(String replacement) {
        return StringMaster.getWellFormattedString(replacement).replace(";", "");
    }

    public static String formatRequirements(String requirements) {
        return "Requirements: "
         + StringMaster.getWellFormattedString(requirements.replace("=", " ")).replace("Or",
         "or").replace("Principles", "Principle -");

    }

    public static String formatFormula(String formula) {
        // TODO {} -> (); CAPS -> Caps; + -> " " + " "
        return StringMaster.getWellFormattedString(formula)

         .replace("Source ", "").replace("source ", "").replace("av", "").replace("(mastery)",
          "mastery").replace("Min(", "(Max ").replace("min(", "(Max ").replace(",", ") ")

         .replace("+", " + ").replace("{", "[").replace("}", "]");
    }

    public static boolean checkHasRefs(String baseValue) {
        return baseValue.contains(StringMaster.FORMULA_REF_OPEN_CHAR);
    }

    public static boolean checkHasVarRefs(String baseValue) {
        for (int i = 1; i < 10; i++) {
            if (baseValue.contains(StringMaster.VAR_REF_OPEN_CHAR + i
             + StringMaster.VAR_REF_CLOSE_CHAR)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkHasValueRefs(String baseValue) {
        for (int i = 1; i < 10; i++) {
            if (baseValue.contains(StringMaster.FORMULA_REF_OPEN_CHAR + i
             + StringMaster.FORMULA_REF_CLOSE_CHAR)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRef(String baseValue) {
        return baseValue.startsWith(StringMaster.FORMULA_REF_OPEN_CHAR)
         && baseValue.endsWith(StringMaster.FORMULA_REF_CLOSE_CHAR);

    }

    public static boolean isActiveParsing() {
        return activeParsing;
    }

    public static void setActiveParsing(boolean activeParsing) {
        TextParser.activeParsing = activeParsing;
    }

    public static boolean isAbilityParsing() {
        return abilityParsing;
    }

    public static void setAbilityParsing(boolean abilityParsing) {
        TextParser.abilityParsing = abilityParsing;
    }

    public static boolean isInfoParsing() {
        return infoParsing;
    }

    public static boolean isBuffParsing() {
        return buffParsing;
    }

    public static void setBuffParsing(boolean buffParsing) {
        TextParser.buffParsing = buffParsing;
    }

}
