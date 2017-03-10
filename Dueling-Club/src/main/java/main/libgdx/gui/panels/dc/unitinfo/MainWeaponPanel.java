package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.MainWeaponDataSource;
import main.libgdx.texture.TextureCache;

public class MainWeaponPanel extends TablePanel {
    public MainWeaponPanel() {
        super();
        TextureRegion textureRegion = new
                TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/main_weapon.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());
        rowDirection = TOP_LEFT;
        fill().right().bottom();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            clear();
            MainWeaponDataSource source = (MainWeaponDataSource) getUserObject();

            source.getMainWeaponDetailInfo().forEach(this::addElement);
            addElement(source.getMainWeapon());

            addCol();

            source.getNaturalMainWeaponDetailInfo().forEach(this::addElement);
            addElement(source.getNaturalMainWeapon());

            updatePanel = false;
        }
    }

}
