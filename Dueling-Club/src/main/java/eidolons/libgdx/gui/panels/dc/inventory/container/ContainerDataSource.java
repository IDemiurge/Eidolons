package eidolons.libgdx.gui.panels.dc.inventory.container;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerDataSource implements InventoryTableDataSource {

    private List<DC_HeroItemObj> items;
    private InventoryFactory factory;
    private DC_Obj obj;
    private ContainerClickHandler handler;
    private ITEM_FILTERS filter;

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
        factory = new InventoryFactory(handler);
    }

    public ContainerClickHandler getHandler() {
        return handler;
    }

    @Override
    public void setFilter(ITEM_FILTERS filter) {
        this.filter=filter;
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        ListMaster.fillWithNullElements(items
         , InventorySlotsPanel.SIZE);
        return factory.getList(items, CELL_TYPE.CONTAINER);
    }
}
