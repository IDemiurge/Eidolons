package eidolons.ability.conditions;

import eidolons.entity.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class IncapacitatedCondition extends MicroCondition {

    private KEYS key;

    public IncapacitatedCondition() {
        key = KEYS.TARGET;
    }

    public IncapacitatedCondition(KEYS key) {
        this.key = key;
    }

    public IncapacitatedCondition(Boolean match) {
        if (match) {
            key = KEYS.MATCH;
        } else {
            key = KEYS.TARGET;
        }
    }

    @Override
    public boolean check(Ref ref) {
        Unit unit = (Unit) ref.getObj(key);
        return unit.isIncapacitated();
    }

}
