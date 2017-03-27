package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.OffWeaponDataSource;
import main.libgdx.texture.TextureCache;

import java.util.List;

public class OffWeaponPanel extends TabbedPanel {
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

        TablePanel weapon = new TablePanel();
        final List<ValueContainer> mainWeaponInfo = source.getOffWeaponDetailInfo();
        final List<ValueContainer> naturalWeaponInfo = source.getNaturalOffWeaponDetailInfo();

        for (ValueContainer valueContainer : mainWeaponInfo) {
            weapon.addElement(valueContainer).padLeft(5);
        }

        weapon.addElement(source.getOffWeapon()).padLeft(5);

        addTab(weapon, "weapon");

        weapon = new TablePanel();

        for (ValueContainer valueContainer : naturalWeaponInfo) {
            weapon.addElement(valueContainer).padLeft(5);
        }


        weapon.addElement(source.getNaturalOffWeapon()).padLeft(5);

        addTab(weapon, "unarmed");

        resetCheckedTab();
    }
}
