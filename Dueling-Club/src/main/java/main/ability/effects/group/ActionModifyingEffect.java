package main.ability.effects.group;

import main.content.DC_TYPE;
import main.elements.conditions.Condition;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class ActionModifyingEffect extends HeroObjectModifyingEffect {
    public ActionModifyingEffect(String actionName, String modString, boolean prop) {
        super(actionName, modString);
        this.prop = prop;
    }

    public ActionModifyingEffect(String actionName, String modString) {
        super(actionName, modString);
    }

    @Override
    protected List<Obj> getObjectsByName(String objName) {
        Unit hero = (Unit) ref.getSourceObj();
        if (objName.contains(StringMaster.AND_SEPARATOR)) {
            List<Obj> list = new LinkedList<>();
            for (String sub : StringMaster.openContainer(objName)) {
                list.add(hero.getAction(sub));
            }
            return list;
        }
        return new ListMaster<Obj>().getList(hero.getAction(objName));
    }

    @Override
    protected Condition getSpecialConditions() {
        return null;
    }

    @Override
    protected DC_TYPE getTYPE() {
        return DC_TYPE.ACTIONS;
    }
}
