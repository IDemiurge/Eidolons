package eidolons.entity.obj.attach;

import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/7/2018.
 */
public class HeroClass extends DC_FeatObj {
    public HeroClass(ObjType type, Unit hero) {
        super(type, hero.getRef());
    }
}
