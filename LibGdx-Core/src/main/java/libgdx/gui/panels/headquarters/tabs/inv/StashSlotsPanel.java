package libgdx.gui.panels.headquarters.tabs.inv;

import eidolons.content.consts.VisualEnums;
import libgdx.gui.panels.dc.inventory.InvItemActor;
import eidolons.content.consts.VisualEnums.CELL_TYPE;
import libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import libgdx.gui.panels.dc.inventory.shop.ShopDataSource;

import java.util.List;

/**
 * Created by JustMe on 4/18/2018.
 *
 */
public class StashSlotsPanel extends InventorySlotsPanel  {


    public StashSlotsPanel(int rows, int cols  ) {
        super(rows, cols);
    }

    @Override
    protected List<InvItemActor> getSlotActors() {
        return getUserObject().getStashSlots();

    }

    @Override
    protected CELL_TYPE getCellType() {
        return VisualEnums.CELL_TYPE.STASH;
    }

    @Override
    public ShopDataSource getUserObject() {
        return (ShopDataSource) super.getUserObject();
    }
}
