package eidolons.ability.conditions.special;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

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
