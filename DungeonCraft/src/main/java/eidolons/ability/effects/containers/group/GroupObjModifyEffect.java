package eidolons.ability.effects.containers.group;

import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.Condition;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;

import java.util.List;

public class GroupObjModifyEffect extends HeroObjectModifyingEffect {

    public GroupObjModifyEffect(DC_TYPE type, String conditionString,
                                String modString) {
        super(type, "string" + StringMaster.wrapInParenthesis(conditionString),
         modString);
    }

    @AE_ConstrArgs(argNames = {"perc or const?", "obj type", "filter prop",
     "filter value", " param", " amount", "add buff?"})
    public GroupObjModifyEffect(Boolean mod, DC_TYPE type, String prop,
                                String value, String p, String amount, Boolean buff) {
        this(type, prop, value, p, amount, buff, false);
        code = (mod ? MOD.MODIFY_BY_PERCENT
         : MOD.MODIFY_BY_CONST);
    }

    @AE_ConstrArgs(argNames = {"obj type", "filter prop", "filter value",
     " param", " amount", "add buff?", "prop?", "filter conditions"})
    public GroupObjModifyEffect(DC_TYPE type, String prop, String value,
                                String p, String amount, Boolean buff, Boolean propEffect,
                                Condition c) {
        super(type, "string"
         + StringMaster.wrapInParenthesis("{"
         + ConditionMaster.checkAddRef(prop) + "}," + value), p
         + StringMaster.wrapInParenthesis(amount), buff, false, c);

    }

    @AE_ConstrArgs(argNames = {"obj type", "filter prop", "filter value",
     " param", " amount", "add buff?", "prop?"})
    public GroupObjModifyEffect(DC_TYPE type, String prop, String value,
                                String p, String amount, Boolean buff, Boolean propEffect) {
        super(type, "string"
         + StringMaster.wrapInParenthesis("{"
         + ConditionMaster.checkAddRef(prop) + "}," + value), p
         + StringMaster.wrapInParenthesis(amount), buff, false);
    }

    @AE_ConstrArgs(argNames = {"obj type", "filter prop", "filter value",
     " param", " amount", "add buff?"})
    public GroupObjModifyEffect(DC_TYPE type, String prop, String value,
                                String p, String amount, Boolean buff) {
        this(type, prop, value, p, amount, buff, false);
    }

    // TODO '|' support?

    @AE_ConstrArgs(argNames = {"obj type", "filter prop", "filter value",
     "buff name", " param", " amount", "property?"})
    public GroupObjModifyEffect(DC_TYPE type, String prop, String value,
                                String buffName, String p, String amount, Boolean propEffect) {
        this(type, prop, value, p, amount, !StringMaster.isEmpty(buffName),
         propEffect);
        this.buffName = buffName;

    }

    @AE_ConstrArgs(argNames = {"obj type", "filter prop", "filter value",
     " param", " amount", "add buff?"})
    public GroupObjModifyEffect(DC_TYPE type, PROPERTY prop, String value,
                                PARAMETER p, String amount, Boolean buff) {
        this(type, prop.getName(), value, p.getName(), amount, buff);
    }

    @Override
    protected Condition getSpecialConditions() {
        return null;
    }

    @Override
    protected List<Obj> getObjectsByName(String objName) {
        return null;
    }

}
