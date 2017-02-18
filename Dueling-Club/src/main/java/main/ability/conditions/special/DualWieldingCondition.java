package main.ability.conditions.special;

import main.elements.conditions.MicroCondition;
import main.entity.obj.unit.Unit;
import main.rules.UnitAnalyzer;

public class DualWieldingCondition extends MicroCondition {

    @Override
    public boolean check() {
        // if (natural)
        // return UnitAnalyzer.checkDualWielding((DC_HeroObj)
        // getRef().getSourceObj());
        // return UnitAnalyzer.checkDualWielding((DC_HeroObj)
        // getRef().getObj(key));
        return UnitAnalyzer.checkDualWielding((Unit) getRef().getSourceObj());
    }

}
