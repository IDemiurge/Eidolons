package libgdx.gui.dungeon.panels.headquarters.tabs.inv;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import libgdx.GDX;
import libgdx.gui.dungeon.panels.dc.inventory.InventoryPanel;
import libgdx.gui.dungeon.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.dungeon.panels.headquarters.HqElement;
import libgdx.gui.dungeon.panels.headquarters.HqMaster;

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
