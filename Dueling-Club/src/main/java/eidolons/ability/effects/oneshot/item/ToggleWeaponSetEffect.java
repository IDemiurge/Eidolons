package eidolons.ability.effects.oneshot.item;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.unit.Unit;

public class ToggleWeaponSetEffect extends DC_Effect {


    @Override
    public boolean applyThis() {
        Unit hero = (Unit) ref.getSourceObj();
//        mainWeapon = hero.getReserveWeapon(false);
//        mainWeapon = hero.getReserveWeapon(false);



        return false;
    }
}
