package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickAttackRadial;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponTooltip;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
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
        setUpdateRequired(false);
//        TextureRegion texture;
//        setBackground(new TextureRegionDrawable(
//         texture = offhand? TextureCache.getOrCreateR("ui/components/dc/infopanel/weapon background offhand.png")
//          : TextureCache.getOrCreateR("ui/components/dc/infopanel/weapon background.png")
//        ));
        setSize(455, 340);

        float xDif =48;
        float yDif=104;

        background.offset(xDif, yDif);
        weapon.offset(xDif, yDif);
        border.offset(xDif, yDif);
        toggleUnarmed.setPosition(toggleUnarmed.getX() + xDif, toggleUnarmed.getY() + yDif);

//        weapon.setVisible(false);
        radial.setVisible(false);
//        border.setVisible(false);
//        toggleUnarmed.setVisible(false);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    protected EventListener getListener() {
        if (getUserObject() == null) {
            return null;
        }
        HeroDataModel source = ((HqHeroDataSource) super.getUserObject()).getEntity();
        return new WeaponTooltip( source.getWeapon(offhand)).getController();
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
        //TODO Gdx Review - radial wasn't working, and it ain't looking top?
        if (super.getUserObject() == null) {
            return;
        }
        weapon.addListener(getListener());
        setVisible(true);
        super.updateAct(delta);
        if (!isVisible())
            return;
          radial.openMenu();
    }

    @Override
    protected QuickAttackRadial createRadial(boolean offhand) {
        return new InfoAttackRadial(this, offhand);
    }
}
