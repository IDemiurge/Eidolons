package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.unitinfo.datasource.OffWeaponDataSource;
import main.libgdx.texture.TextureCache;

public class OffWeaponPanel extends WeaponPanel {
    public OffWeaponPanel() {
        super();
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/off_weapon.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        OffWeaponDataSource src = (OffWeaponDataSource) getUserObject();

        addWeapons(src.getOffWeapon(), src.getOffWeaponDetailInfo(), "weapon");

        addWeapons(src.getNaturalOffWeapon(), src.getNaturalOffWeaponDetailInfo(), "unarmed");

        resetCheckedTab();
    }
}
