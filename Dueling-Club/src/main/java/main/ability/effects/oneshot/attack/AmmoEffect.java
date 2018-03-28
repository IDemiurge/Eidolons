package main.ability.effects.oneshot.attack;

import main.ability.effects.DC_Effect;
import main.entity.Ref.KEYS;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;

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
