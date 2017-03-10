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
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());
        rowDirection = TOP_LEFT;
        fill().left().bottom();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            clear();
            OffWeaponDataSource source = (OffWeaponDataSource) getUserObject();

            addElement(source.getOffWeapon());
            source.getOffWeaponDetailInfo().forEach(this::addElement);

            addCol();

            addElement(source.getNaturalOffWeapon());
            source.getNaturalOffWeaponDetailInfo().forEach(this::addElement);

            updatePanel = false;
        }
    }
}
