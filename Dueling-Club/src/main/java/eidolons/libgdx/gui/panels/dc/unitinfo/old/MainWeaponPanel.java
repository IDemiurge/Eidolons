package eidolons.libgdx.gui.panels.dc.unitinfo.old;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.MainWeaponDataSource;
import eidolons.libgdx.texture.TextureCache;

public class MainWeaponPanel extends WeaponPanel {
    public MainWeaponPanel() {
        super();
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/main_weapon.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        MainWeaponDataSource src = (MainWeaponDataSource) getUserObject();

        addWeapons(src.getMainWeapon(), src.getMainWeaponDetailInfo(), "weapon");

        addWeapons(src.getNaturalMainWeapon(), src.getNaturalMainWeaponDetailInfo(), "unarmed");

        resetCheckedTab();
    }

}
