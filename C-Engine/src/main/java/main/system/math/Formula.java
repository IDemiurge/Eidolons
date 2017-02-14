package main.system.math;

import com.graphbuilder.math.Expression;
import com.graphbuilder.math.ExpressionTree;
import com.graphbuilder.math.FuncMap;
import com.graphbuilder.math.VarMap;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.Game;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.StringMaster;
import main.system.text.TextParser;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Regulus
 */
public class Formula implements MyMathObj {
    // FORMAT:{REF_VALUE}
    // protected static final Logger logger = Logger.getLogger(Formula.class);
//TODO extract parsing into Parser!

    private static final String RESISTANCE = "{TARGET_RESISTANCE}";
    private static List<String> failedFormulas = new LinkedList<>();
    private String formula;
    private Ref ref;
    private String buffer;

    public Formula(String formula) {
        this.formula = formula;
        if (formula.equals("")) {
            this.formula = "0";
        }
    }

    public static Formula getObjValueReferenceFormula(String obj_ref, String value) {
        return new Formula(StringMaster.FORMULA_REF_OPEN_CHAR + obj_ref + "_" + value
                + StringMaster.FORMULA_REF_CLOSE_CHAR);

    }

    public static Formula getFormulaByAppend(Object... objects) {
        String formula = "";
        for (Object o : objects) {
            formula += StringMaster.wrapInParenthesis(o.toString()) + "+";
        }

        return new Formula(StringMaster.cropLast(formula, 1));
    }

    public Number evaluate() {
        if (StringMaster.isInteger(formula)) {
            return StringMaster.getInteger(formula);
        }

        if (StringMaster.isNumber(formula, false)) {
            return new Double(formula);
        }

        buffer = formula;
        VarMap vm = new VarMap(false /* case sensitive */);

        try {
            buffer = parse(buffer);
        } catch (Exception e) {
            return 0;
        }
        formatBuffer();

        Expression expression;
        try {
            expression = ExpressionTree.parse(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        FuncMap fm = new FuncMap(false);
        fm.loadDefaultFunctions();
        double result = 0;
        try {
            result = expression.eval(vm, fm);
        } catch (Exception e) {
            if (!failedFormulas.contains(toString())) {
                e.printStackTrace();
                failedFormulas.add(toString());
            }
        }

        LogMaster.log(0, "Formula after parsing: " + formula);
        LogMaster.log(0, "Result: " + result);
        return result;
    }

    private void formatBuffer() {
        buffer = MathMaster.formatFormula(buffer);

    }

    @Override
    public int getInt() {
        Number number;
        try {
            number = evaluate();
        } catch (Exception e) {
            LogMaster.log(1, "Formula failed to evaluate: " + formula);
            e.printStackTrace();
            throw e;
        }
        if (number instanceof Double) {
            return (int) Math.round((Double) number);
        }

        return (Integer) number;
    }

    private String parse(String formula) {
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
                    // try {
                    String replacement = FunctionManager.evaluateFunction(getRef(), func_substring);
                    formula = StringMaster.replaceFirst(formula, func_substring, replacement);
                    break;
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // main.system.auxiliary.LogMaster.log(4, func_substring
                    // + " failed to parse function for formula: "
                    // + formula);
                    // break;
                    // }
                }

                if (ch == StringMaster.FORMULA_REF_OPEN_CHAR.charAt(0)) {
                    String ref_substring = StringMaster.getSubString(formula,
                            StringMaster.FORMULA_REF_OPEN_CHAR,
                            StringMaster.FORMULA_REF_CLOSE_CHAR, true);

                    int result = 0;
                    try {
                        result = parseRef(ref_substring);
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private int parseRef(String ref_substring) {
        String obj_ref;
        String param;
        boolean base = false;
        if (ref_substring.contains(StringMaster.FORMULA_BASE_CHAR)) {
            ref_substring = ref_substring.replace(StringMaster.FORMULA_BASE_CHAR, "");
            base = true;
        }
        if (getRef() != null) {
            getRef().setBase(base);
        }
        if (ref_substring.contains("_")) {

            obj_ref = ref_substring.substring(ref_substring.indexOf('{') + 1, ref_substring
                    .indexOf('_'));
            if (obj_ref.length() == 0) {
                obj_ref = "SOURCE";
            }

            param = StringMaster.getSubString(ref_substring, "_", "}", false);
            // ref_substring
            // .substring(ref_substring.indexOf('_') + 1, ref_substring
            // .indexOf('}'));

        } else {
            obj_ref = null;
            param = StringMaster.getSubString(ref_substring, "" + '{', "" + '}', false);
        }
        // parse {n} references to Props.Formula items
        if (StringMaster.isInteger(param)) {
            String parsedString = TextParser.parse(ref_substring, ref);
            if (!TextParser.isRef(parsedString)) {
                return new Formula(parsedString).getInt(ref);
            }
        }

        try {
            return evaluateRefs(obj_ref, param);
        } catch (Exception e) {
            return evaluateRefs(KEYS.SOURCE.toString(), obj_ref + " " + param);
        }

    }

    private int evaluateRefs(String obj_ref, String param) {
        int val = new Parameter(obj_ref, param).getInt(getRef());
        return val;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public String toString() {
        return formula;
    }

    @Override
    public Integer getInt(Ref ref) {

        this.setRef(ref);
        return getInt();
    }

    public Double getDouble(Ref ref) {
        this.setRef(ref);
        return Double.valueOf(evaluate().toString());
    }

    public Double getDouble() {
        return Double.valueOf(evaluate().toString());
    }

    public Formula getNegative() {
        return new Formula("-1*(" + this.formula + ")");
    }

    public Formula append(String string) {
        setFormula("(" + this.formula + ")" + string);
        return this;
    }

    public Formula getAppended(String string) {
        return new Formula("(" + this.formula + ")" + string);
    }

    public Formula getInverted() {
        return new Formula("1/(" + this.formula + ")");
    }

    public void addResistance() {
        this.formula = "((" + this.formula + ")*100-" + this.formula + "*" + RESISTANCE + ")/100";

    }

    public Formula wrapObjRef() {
        return new Formula("{" + formula + "}");
    }

    public void applyFactor(Object mod) {
        setFormula("((" + this.formula + ")*100+" + this.formula + "*" + "(" + mod.toString()
                + "))/100");

    }

    public void applyModifier(Object mod) {
        setFormula("(" + this.formula + "*" + "(" + mod.toString() + "))/100");

    }

    public Formula substituteVarValue(String var, String value) {
        setFormula(toString().replace(var, value));
        return this;
    }

    public Formula getAppendedByModifier(Object mod) {
        if (mod == null) {
            return this;
        }
        return new Formula("((" + this.formula + ")" + "*" + "(" + mod.toString() + "))/100");
    }

    public Formula getAppendedByFactor(Object mod) {
        if (mod == null) {
            return this;
        }
        return new Formula("" + this.formula + "+" + "(" + this.formula + ")*" + "("
                + mod.toString() + ")");
    }

    public Ref getRef() {
        if (ref == null) {
            ref = new Ref(Game.game);
            // ref.setID(KEYS.INFO, TODO
        }
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

}
