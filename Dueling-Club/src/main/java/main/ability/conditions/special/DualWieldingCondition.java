package main.ability.conditions.special;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.UnitAnalyzer;

public class DualWieldingCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        // if (natural)
        // return UnitAnalyzer.checkDualWielding((DC_HeroObj)
        //ref.getSourceObj());
        // return UnitAnalyzer.checkDualWielding((DC_HeroObj)
        //ref.getObj(key));
        return UnitAnalyzer.checkDualWielding((Unit) ref.getSourceObj());
    }

}
