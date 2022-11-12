package eidolons.entity.unit.attach;

import eidolons.entity.unit.Unit;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/7/2018.
 */
public class Perk extends DC_PassiveObj {
    public Perk(ObjType type, Unit hero) {
        super(type, hero.getRef());
    }

    @Override
    public void apply() {
        super.apply();
    }
}
