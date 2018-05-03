package eidolons.libgdx.gui.panels.dc.inventory.container;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryValueContainerFactory;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerDataSource implements InventoryTableDataSource {

    private List<DC_HeroItemObj> items;
    private InventoryValueContainerFactory factory;
    private DC_Obj obj;
    private ContainerClickHandler handler;

    public ContainerDataSource(DC_Obj obj, Unit unit) {
        this.obj = obj;
        items = null;
        if (obj instanceof ContainerObj) {
            items =  ((ContainerObj) obj).getItems();
        } else {
            obj.getGame().getDroppedItemManager().reset(obj.getX(), obj.getY());
            items = obj.getGame().getDroppedItemManager().getDroppedItems(obj);
        }
        handler = new ContainerClickHandler(obj.getImagePath(), items, unit, obj);// obj.getGame().getInventoryManager().getClickHandler();
        factory = new InventoryValueContainerFactory(handler);
    }

    public ContainerClickHandler getHandler() {
        return handler;
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        ListMaster.fillWithNullElements(items
         , InventorySlotsPanel.SIZE);
        return factory.getList(items, CELL_TYPE.INVENTORY);
    }
}
