package main.ability.conditions.special;

import main.elements.conditions.MicroCondition;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;

public class CanActCondition extends MicroCondition {

    private String key;

    public CanActCondition(KEYS key) {
        this.key = key.toString();
    }

    public CanActCondition(String key) {
        this.key = key;
    }

    @Override
    public boolean check() {
        if (ref.getObj(key) instanceof Unit) {
            Unit obj = (Unit) ref.getObj(key);
            return obj.canActNow();
        }
        return false;
    }

}
