package main.system.math;

import com.graphbuilder.math.Expression;
import com.graphbuilder.math.ExpressionTree;
import com.graphbuilder.math.FuncMap;
import com.graphbuilder.math.VarMap;
import main.entity.Ref;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Regulus
 */
public class Formula {
    //TODO extract parsing into Parser!

    private static FuncMap functionMap;
    private String formula;
    private static Map<String, Expression> expressionMap = new HashMap<>();

    public Formula(String formula) {
        this.formula = formula;
        if (formula.equals("")) {
            this.formula = "0";
        }
    }

    public static FuncMap getFunctionMap() {
        if (functionMap == null) {
            functionMap = new FuncMap(false);
            functionMap.loadDefaultFunctions();
        }
        return functionMap;
    }

    public Number evaluate(Ref ref) {
        if (StringMaster.isEmpty(formula)) {
            return 0;
        }
        if (NumberUtils.isInteger(formula)) {
            return NumberUtils.getInteger(formula);
        }

        if (NumberUtils.isNumber(formula, false)) {
            return new Double(formula);
        }

        String buffer = formula;
        VarMap vm = new VarMap(false /* case sensitive */);

        buffer = new DynamicValueParser().parseDynamicValues(buffer, ref);
        buffer = MathMaster.formatFormula(buffer);

        Expression expression = expressionMap.get(buffer);
        if (expression == null) {
            expression = ExpressionTree.parse(buffer);
            expressionMap.put(buffer, expression);
        }

        double result = expression.eval(vm, getFunctionMap());
        return result;
    }

    public Integer getInt(Ref ref) {
        Number number;
        try {
            number = evaluate(ref);
        } catch (Exception e) {
            LogMaster.log(1, "Formula failed to evaluate: " + formula);
            main.system.ExceptionMaster.printStackTrace(e);
            throw e;
        }
        if (number instanceof Double) {
            return (int) Math.round((Double) number);
        }

        return (Integer) number;
    }

    public int getInt() {
        return getInt(null);
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public String toString() {
        return formula;
    }

    public Double getDouble(Ref ref) {
        return Double.valueOf(evaluate(ref).toString());
    }

    public Double getDouble() {
        return Double.valueOf(evaluate(null).toString());
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

    public Formula wrapObjRef() {
        return new Formula("{" + formula + "}");
    }

    public Formula applyFactor(Object mod) {
        setFormula("((" + this.formula + ")*100+" + this.formula + "*" + "(" + mod.toString()
                + "))/100");
        return this;
    }

    public Formula applyModifier(Object mod) {
        setFormula("(" + this.formula + "*" + "(" + mod.toString() + "))/100");
        return this;
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

    public Formula getAppendedByMultiplier(Object mod) {
        if (mod == null) {
            return this;
        }
        return new Formula("(" + this.formula + ")" + "*" + "(" + mod.toString() + ")");

    }

    public Formula getAppendedByFactor(Object mod) {
        if (mod == null) {
            return this;
        }
        return new Formula("" + this.formula + "+" + "(" + this.formula + ")*" + "("
                + mod.toString() + ")");
    }

}
