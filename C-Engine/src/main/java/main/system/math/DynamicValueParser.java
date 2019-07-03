package main.system.math;

import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.Loop;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.LogMaster;
import main.system.text.TextParser;

/**
 * Created by JustMe on 3/21/2017.
 */
public class DynamicValueParser {
    private Ref ref;

    public DynamicValueParser() {
    }

    /**
     * Replaces all {dynamicValues} with plain integers in a given formula, using @ref to evaluate them
     *
     * @param formula
     * @param ref
     * @return
     */
    public String parseDynamicValues(String formula, Ref ref) {
        this.ref = ref;
        formula = formula.toUpperCase();
        Loop.startLoop(1000);
        while (!Loop.loopEnded()) {
            if (!formula.contains(StringMaster.FORMULA_REF_OPEN_CHAR)) {
                if (!formula.contains(StringMaster.FORMULA_FUNC_OPEN_CHAR)) {
                    break;
                }
            }
            char[] charArray = formula.toCharArray();

            for (char ch : charArray) {

                if (ch == StringMaster.FORMULA_FUNC_OPEN_CHAR.charAt(0)) {

                    String func_substring = StringMaster.getSubString(formula,
                     StringMaster.FORMULA_FUNC_OPEN_CHAR,
                     StringMaster.FORMULA_FUNC_CLOSE_CHAR);
                    String replacement = FunctionManager.evaluateFunction(ref, func_substring);
                    formula = StringMaster.replaceFirst(formula, func_substring, replacement);
                    break;

                }

                if (ch == StringMaster.FORMULA_REF_OPEN_CHAR.charAt(0)) {
                    String ref_substring = StringMaster.getSubString(formula,
                     StringMaster.FORMULA_REF_OPEN_CHAR,
                     StringMaster.FORMULA_REF_CLOSE_CHAR, true);

                    int result = 0;
                    try {
                        result = getIntFromDynamicValue(ref_substring);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                        LogMaster.log(LogMaster.MATH_DEBUG, ref_substring
                         + " failed to parse ref for formula: " + formula);
                    }
                    formula = formula.replace(ref_substring, String.valueOf(result));
                    LogMaster.log(0, "Ref parsed: " + ref_substring + " = "
                     + result);
                    break;
                }

            }
        }
        return formula;
    }

    private int getIntFromDynamicValue(String ref_substring ) {
        String obj_ref;
        String param;
        boolean base = false;
        if (ref_substring.contains(StringMaster.FORMULA_BASE_CHAR)) {
            ref_substring = ref_substring.replace(StringMaster.FORMULA_BASE_CHAR, "");
            base = true;
        }
        if (this.ref != null) {
            this.ref.setBase(base);
        } else {
           this.ref = new Ref();
        }
        if (ref_substring.contains("_")) {

            obj_ref = ref_substring.substring(ref_substring.indexOf('{') + 1, ref_substring
             .indexOf('_'));
            if (obj_ref.length() == 0) {
                obj_ref = "SOURCE";
            }

            param = StringMaster.getSubString(ref_substring, "_", "}", false);

        } else {
            obj_ref = null;
            param = StringMaster.getSubString(ref_substring, "" + '{', "" + '}', false);
        }
        if (NumberUtils.isInteger(param)) {
            String parsedString = TextParser.parse(ref_substring, this.ref);
            if (!TextParser.isRef(parsedString)) {
                return new Formula(parsedString).getInt(this.ref);
            }
        }

        try {
            int val = new Parameter(obj_ref, param).getInt(this.ref);
            return val;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            int val = new Parameter(KEYS.SOURCE.toString(),
             obj_ref + " " + param)
             .getInt(this.ref);
            return val;
        }

    }


}
