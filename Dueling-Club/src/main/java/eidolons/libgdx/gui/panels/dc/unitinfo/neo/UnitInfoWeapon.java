package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickAttackRadial;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 6/30/2018.
 *
 * radial always visible and very custom...
 */
public class UnitInfoWeapon extends QuickWeaponPanel{
    public UnitInfoWeapon(boolean offhand) {
        super(offhand);
        updateRequired=false;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    public Object getUserObject() {
        if (super.getUserObject() == null) {
            return null;
        }
        Unit hero = ((HqHeroDataSource) super.getUserObject()).getEntity().getHero();
        Pair<WeaponDataSource, WeaponDataSource> pair =
         new ImmutablePair<>(new WeaponDataSource(hero.getActiveWeapon(offhand)),
          new WeaponDataSource(hero.getNaturalWeapon(offhand)));

        return pair;
    }

    @Override
    public void updateAct(float delta) {
        if (super.getUserObject() == null) {
            return;
        }
        super.updateAct(delta);
          radial.openMenu();
    }

    @Override
    protected QuickAttackRadial createRadial(boolean offhand) {
        return new InfoAttackRadial(this, offhand);
    }
}