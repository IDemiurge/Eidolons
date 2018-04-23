package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqElement;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqInvPanel extends HqElement{

    private InventoryPanel inventoryPanel;
    private HqWeaponAttacksPanel weaponAttacksPanel;

    public HqInvPanel() {

        inventoryPanel = new InventoryPanel();
        inventoryPanel.setBackground((Drawable) null);
        addElement(inventoryPanel);
        row();
    }

    @Override
    protected void update(float delta) {
        inventoryPanel.setUserObject(new InventoryDataSource(dataSource.getEntity().getHero()));
        inventoryPanel.updateAct(delta);
    }
}
