package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickAttackRadial;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;

/**
 * Created by JustMe on 6/30/2018.
 *
 * radial always visible and very custom...
 */
public class UnitInfoWeapon extends QuickWeaponPanel{
    public UnitInfoWeapon(boolean offhand) {
        super(offhand);
    }

    @Override
    protected QuickAttackRadial createRadial(boolean offhand) {
        return new InfoAttackRadial(this, offhand);
    }
}
