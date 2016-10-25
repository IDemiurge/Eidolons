package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.obj.DC_HeroObj;

public class ImmobilizedCondition extends MicroCondition {

    @Override
    public boolean check() {
        DC_HeroObj unit = (DC_HeroObj) getRef().getMatchObj();
        return unit.isImmobilized();
    }

}
