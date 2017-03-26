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

        pad(50, 10, 5, 10);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        MainWeaponDataSource source = (MainWeaponDataSource) getUserObject();

        for (main.libgdx.gui.panels.dc.ValueContainer valueContainer : source.getMainWeaponDetailInfo()) {
            addElement(valueContainer).expand(0, 0).fill(false).right().bottom().padLeft(5);
        }
        add(source.getMainWeapon()).expand(1, 1).fill(0, 1).right().padLeft(5);

        row();

        for (main.libgdx.gui.panels.dc.ValueContainer valueContainer : source.getNaturalMainWeaponDetailInfo()) {
            addElement(valueContainer).expand(0, 0).fill(false).right().bottom().padLeft(5);
        }
        add(source.getNaturalMainWeapon()).expand(1, 1).fill(0, 1).right().padLeft(5);
    }
}
