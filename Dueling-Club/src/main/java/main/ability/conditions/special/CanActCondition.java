package main.ability.conditions.special;

import main.elements.conditions.MicroCondition;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;

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
        if (ref.getObj(key) instanceof DC_HeroObj) {
            DC_HeroObj obj = (DC_HeroObj) ref.getObj(key);
            return obj.canActNow();
        }
        return false;
    }

}
