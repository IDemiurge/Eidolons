package main.system.math;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 3/21/2017.
 */
public class FormulaFactory {


    public static Formula getFormulaByAppend(Object... objects) {
        String formula = "";
        for (Object o : objects) {
            formula += StringMaster.wrapInParenthesis(o.toString()) + "+";
        }

        return new Formula(StringMaster.cropLast(formula, 1));
    }
}
