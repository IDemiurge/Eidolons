package main.elements.conditions;

import main.data.ability.AE_ConstrArgs;
import main.data.ability.OmittedConstructor;
import main.system.math.Formula;

public class NumericCondition extends ConditionImpl {
    protected Formula greater;
    protected Formula than;
    protected boolean equal = false;
    protected Boolean strict = true;

    @AE_ConstrArgs(argNames = {"comparedValue", "comparingValue", "equal?"})
    public NumericCondition(Formula comparedValue, Formula comparingValue,
                            Boolean equal) {
        this.greater = comparedValue;
        this.than = comparingValue;
        this.equal = equal;
    }

    public NumericCondition(Boolean strict, Formula greater, Formula than) {
        this(greater, than, false);
        this.strict = strict;
    }

    @OmittedConstructor
    public NumericCondition(Boolean strict, String greater, String than) {
        this(strict, new Formula(greater), new Formula(than));
    }

    @OmittedConstructor
    public NumericCondition(String greater, String than) {
        this(new Formula(greater), new Formula(than), false);
    }

    @OmittedConstructor
    public NumericCondition(String string, String string2, boolean equal) {
        this(new Formula(string), new Formula(string2), equal);
    }

    @Override
    public boolean check() {

        int a = greater.getInt(ref);
        int b = than.getInt(ref);
        boolean result = (!equal) ? (!strict) ? a >= b : a > b : a == b;
        if ((!strict)) {
            if (a == 0 && b == 0) {
                result = false;
            }
        }
        // LogMaster.log(LogMaster.CONDITION_DEBUG, "Numeric Condition "
        // + comparedValue + " = " + a
        // + ((!equal) ? " greater than " : " equal to ") + comparingValue
        // + " = " + b + " => " + result);
        return result;
    }

    @Override
    public String toString() {

        return greater.toString()
                + ((!equal) ? (" greater than " + ((!strict) ? "or equal to "
                : ""))

                : " equal to ")

                + than.toString();
    }

    public Formula getComparedValue() {
        return greater;
    }

    public void setComparedValue(Formula comparedValue) {
        this.greater = comparedValue;
    }

    public Formula getComparingValue() {
        return than;
    }

    public void setComparingValue(Formula comparingValue) {
        this.than = comparingValue;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual(boolean equal) {
        this.equal = equal;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }
}
