package main.elements.conditions;

import main.data.ability.AE_ConstrArgs;
import main.entity.Ref;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.math.Property;

public class StringComparison extends ConditionImpl {
    protected Property arg1;
    protected Property arg2;
    protected boolean strict = false;
    protected String str1;
    protected String str2;
    protected String val1;
    protected String val2;

    @AE_ConstrArgs(argNames = {"string#1", "string#2", "strict?"})
    public StringComparison(String str1, String str2, Boolean strict) {
        this.str1 = str1;
        this.str2 = str2;
        this.strict = strict;
    }

    @Override
    public String toString() {
        return str1 + " VS " + str2;
    }

    // if 2 units share race... or classification... if a unit has a
    // classification
    @Override
    public boolean check(Ref ref) {
        if (arg1 == null) {
            this.arg1 = new Property(str1);
        }
        if (arg2 == null) {
            this.arg2 = new Property(str2);
        }

        val1 = arg1.getStr(ref);
        val2 = arg2.getStr(ref);
        boolean result;
        if (val2.contains(Strings.VERTICAL_BAR)) {
            for (String s : ContainerUtils.open(val2,
             Strings.VERTICAL_BAR)) {
                if (compare(val1, s, strict)) {
                    return true;
                }
            }
        }
        if (val1.contains(Strings.VERTICAL_BAR))// shouldn't happen
        {
            for (String s : ContainerUtils.open(val1,
             Strings.VERTICAL_BAR)) {
                if (compare(s, val2, strict)) {
                    return true;
                }
            }
        }

        result = compare(val1, val2, strict);
        return result;

    }

    private boolean compare(String val1, String val2, boolean strict) {
        boolean result = StringMaster.compareByChar(val1, val2, false);
        if (!result) {
            if (!strict) {
                result = StringMaster.compare(val1, val2, false);
            }
        }
        if (!result) {
            // LogMaster.src.main.system.log(LogMaster.CONDITION_DEBUG, "Comparing "
            // + ((strict) ? " strictly " : "") + this.val1 + " to "
            // + this.val2 + " => ");
        }
        return result;
    }

    public Property getArg1() {
        return arg1;
    }

    public void setArg1(Property arg1) {
        this.arg1 = arg1;
    }

    public Property getArg2() {
        return arg2;
    }

    public void setArg2(Property arg2) {
        this.arg2 = arg2;
    }

}
