package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.GDX;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqInvPanel extends HqElement{

    private InventoryPanel inventoryPanel;
    private HqWeaponAttacksPanel weaponAttacksPanel;

    public HqInvPanel() {

        setFixedSize(true);
        setSize(GDX.size(HqMaster.TAB_WIDTH) ,
         GDX.size(HqMaster.TAB_HEIGHT) );

        inventoryPanel = new InventoryPanel();
        inventoryPanel.setBackground((Drawable) null);
        addElement(inventoryPanel);
        row();

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        inventoryPanel.setUserObject(new InventoryDataSource(getUserObject().getEntity().getHero()));
    }

    @Override
    protected void update(float delta) {

    }
}
