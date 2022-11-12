package eidolons.ability.effects.oneshot.attack;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
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
        WeaponItem rangedWeapon = (WeaponItem) ref.getObj(KEYS.RANGED);
        if (add) {
            QuickItem ammo = (QuickItem) ref.getActive().getRef().getObj(KEYS.AMMO);

            rangedWeapon.setAmmo(ammo);
        } else {
            rangedWeapon.setAmmo(null);
        }

        return true;
    }
}
