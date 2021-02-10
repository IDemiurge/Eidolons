package eidolons.ability.effects.oneshot.attack;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import main.entity.Ref.KEYS;

/**
 * Created by JustMe on 3/23/2017.
 */
public class AmmoEffect extends DC_Effect {
    private boolean add;

    public AmmoEffect(Boolean add) {
        this.add = add;
    }

    @Override
    public boolean applyThis() {
        DC_WeaponObj rangedWeapon = (DC_WeaponObj) ref.getObj(KEYS.RANGED);
        if (add) {
            DC_QuickItemObj ammo = (DC_QuickItemObj) ref.getActive().getRef().getObj(KEYS.AMMO);

            rangedWeapon.setAmmo(ammo);
        } else {
            rangedWeapon.setAmmo(null);
        }

        return true;
    }
}
