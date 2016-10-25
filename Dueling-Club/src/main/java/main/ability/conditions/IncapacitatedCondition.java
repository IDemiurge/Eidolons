package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;

public class IncapacitatedCondition extends MicroCondition {

    private KEYS key;

    public IncapacitatedCondition() {
        key = KEYS.TARGET;
    }

    public IncapacitatedCondition(KEYS key) {
        this.key = key;
    }

    public IncapacitatedCondition(Boolean match) {
        if (match)
            key = KEYS.MATCH;
        else
            key = KEYS.TARGET;
    }

    @Override
    public boolean check() {
        DC_HeroObj unit = (DC_HeroObj) getRef().getObj(key);
        return unit.isIncapacitated();
    }

}
