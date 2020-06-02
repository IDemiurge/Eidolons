package eidolons.ability.effects.containers.group;

import eidolons.entity.obj.unit.Unit;
import main.content.DC_TYPE;
import main.elements.conditions.Condition;
import main.entity.obj.Obj;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
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
        if (objName.contains(StringMaster.VERTICAL_BAR)) {
            List<Obj> list = new ArrayList<>();
            for (String sub : ContainerUtils.open(objName)) {
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
