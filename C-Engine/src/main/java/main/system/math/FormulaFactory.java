package main.system.math;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 3/21/2017.
 */
public class FormulaFactory {


    public static Formula getFormulaByAppend(Object... objects) {
        StringBuilder formula = new StringBuilder();
        for (Object o : objects) {
            formula.append(StringMaster.wrapInParenthesis(o.toString())).append("+");
        }

        return new Formula(StringMaster.cropLast(formula.toString(), 1));
    }
}
