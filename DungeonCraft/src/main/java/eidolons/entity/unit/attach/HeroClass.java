package eidolons.entity.unit.attach;

import eidolons.entity.unit.Unit;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/7/2018.
 */
public class HeroClass extends DC_PassiveObj {
    public HeroClass(ObjType type, Unit hero) {
        super(type, hero.getRef());
    }
}
