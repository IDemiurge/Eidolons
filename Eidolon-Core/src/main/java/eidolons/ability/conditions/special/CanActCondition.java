package eidolons.ability.conditions.special;

import eidolons.entity.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class CanActCondition extends MicroCondition {

    private String key;

    public CanActCondition(KEYS key) {
        this.key = key.toString();
    }

    public CanActCondition(String key) {
        this.key = key;
    }

    @Override
    public boolean check(Ref ref) {
        if (ref.getObj(key) instanceof Unit) {
            Unit obj = (Unit) ref.getObj(key);
            return obj.canActNow();
        }
        return false;
    }

}
