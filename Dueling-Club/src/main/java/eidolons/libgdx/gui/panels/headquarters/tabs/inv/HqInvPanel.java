package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryPanel;
import eidolons.libgdx.gui.panels.headquarters.HqElement;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqInvPanel extends HqElement{

    private InventoryPanel inventoryPanel;
    private HqWeaponAttacksPanel weaponAttacksPanel;

    public HqInvPanel() {
QuickWeaponPanel quickWeaponPanel;
        inventoryPanel = new InventoryPanel();
        inventoryPanel.setBackground((Drawable) null);
        addElement(inventoryPanel);
        row();
    }

    @Override
    protected void update(float delta) {

    }
}
