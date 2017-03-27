package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.MainWeaponDataSource;
import main.libgdx.texture.TextureCache;

import java.util.List;

public class MainWeaponPanel extends TabbedPanel {
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

        TablePanel weapon = new TablePanel();
        final List<ValueContainer> mainWeaponInfo = source.getMainWeaponDetailInfo();
        final List<ValueContainer> naturalWeaponInfo = source.getNaturalMainWeaponDetailInfo();

        for (ValueContainer valueContainer : mainWeaponInfo) {
            weapon.addElement(valueContainer).padLeft(5);
        }

/*        final int sizeDiff = naturalWeaponInfo.size() - mainWeaponInfo.size();
        if (sizeDiff > 0){
            for (int i = 0; i < sizeDiff; i++) {
                addElement(null).expand(0, 0).fill(true);
            }
        }*/


        weapon.addElement(source.getMainWeapon()).padLeft(5);

        addTab(weapon, "weapon");

        weapon = new TablePanel();

        for (ValueContainer valueContainer : naturalWeaponInfo) {
            weapon.addElement(valueContainer).padLeft(5);
        }


        weapon.addElement(source.getNaturalMainWeapon()).padLeft(5);

        addTab(weapon, "unarmed");

        resetCheckedTab();
    }
}
