package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.OffWeaponDataSource;
import main.libgdx.texture.TextureCache;

public class OffWeaponPanel extends TablePanel {
    public OffWeaponPanel() {
        super();
        TextureRegion textureRegion = new TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/off_weapon.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        pad(50, 10, 5, 10);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        OffWeaponDataSource source = (OffWeaponDataSource) getUserObject();

        add(source.getOffWeapon()).expand(1, 1).fill(0, 1).right().padLeft(5);
        for (main.libgdx.gui.panels.dc.ValueContainer valueContainer : source.getOffWeaponDetailInfo()) {
            addElement(valueContainer).expand(0, 0).fill(false).right().bottom().padLeft(5);
        }

        row();

        add(source.getNaturalOffWeapon()).expand(1, 1).fill(0, 1).right().padLeft(5);
        for (main.libgdx.gui.panels.dc.ValueContainer valueContainer : source.getNaturalOffWeaponDetailInfo()) {
            addElement(valueContainer).expand(0, 0).fill(false).right().bottom().padLeft(5);
        }
    }
}
