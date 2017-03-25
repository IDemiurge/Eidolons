package main.libgdx.gui.panels.dc.simple_layout;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.unitinfo.datasource.OffWeaponDataSource;
import main.libgdx.texture.TextureCache;

public class OffWeaponPanel extends TablePanel {
    public OffWeaponPanel() {
        super();
        TextureRegion textureRegion = new TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/off_weapon.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        OffWeaponDataSource source = (OffWeaponDataSource) getUserObject();

        addElement(source.getOffWeapon());
        source.getOffWeaponDetailInfo().forEach(this::addElement);

        row();

        addElement(source.getNaturalOffWeapon());
        source.getNaturalOffWeaponDetailInfo().forEach(this::addElement);
    }
}
