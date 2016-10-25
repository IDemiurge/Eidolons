package main.elements.conditions.standard;

import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.data.ability.AE_ConstrArgs;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.entity.Entity;
import main.system.math.Formula;

public class ListSizeCondition extends MicroCondition {

    private Condition c;
    private String sizeFormula;
    private Boolean bfObj;
    private Filter<Entity> filter;

    @AE_ConstrArgs(argNames = {"bf obj?", "filter conditions",
            "required list size"})
    public ListSizeCondition(Boolean bfObj, Condition c, String filterFormula) {
        this.c = c;
        this.sizeFormula = filterFormula;
        this.bfObj = bfObj;
    }

    @Override
    public boolean check() {

        OBJ_TYPE TYPE = C_OBJ_TYPE.BF;
        if (bfObj)
            TYPE = C_OBJ_TYPE.BF_OBJ;
        if (filter == null)
            filter = new Filter<Entity>(ref, c, TYPE);
        else
            filter.setRef(ref);

        int size = new Formula(sizeFormula).getInt(ref);

        return filter.getObjects().size() >= size;
    }

}
