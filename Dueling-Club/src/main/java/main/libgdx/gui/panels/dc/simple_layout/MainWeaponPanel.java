package main.libgdx.gui.panels.dc.simple_layout;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.unitinfo.datasource.MainWeaponDataSource;
import main.libgdx.texture.TextureCache;

public class MainWeaponPanel extends TablePanel {
    public MainWeaponPanel() {
        super();
        TextureRegion textureRegion = new
                TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/main_weapon.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        MainWeaponDataSource source = (MainWeaponDataSource) getUserObject();

        source.getMainWeaponDetailInfo().forEach(this::addElement);
        addElement(source.getMainWeapon());

        row();

        source.getNaturalMainWeaponDetailInfo().forEach(this::addElement);
        addElement(source.getNaturalMainWeapon());
    }
}
