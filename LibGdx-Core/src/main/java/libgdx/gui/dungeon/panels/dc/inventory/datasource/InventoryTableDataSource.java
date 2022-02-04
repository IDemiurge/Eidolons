package libgdx.gui.dungeon.panels.dc.inventory.datasource;

import eidolons.entity.item.HeroItem;
import eidolons.entity.mngr.item.ItemMaster;
import libgdx.gui.dungeon.panels.dc.inventory.InvItemActor;
import libgdx.gui.dungeon.panels.dc.inventory.InventoryClickHandler;
import eidolons.content.consts.VisualEnums.CELL_TYPE;
import eidolons.content.consts.VisualEnums.ITEM_FILTERS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface InventoryTableDataSource {
    void setFilter(ITEM_FILTERS filter);

    default List<HeroItem> applyFilter(Collection<HeroItem> items, ITEM_FILTERS filter) {
        List<HeroItem> list = new ArrayList<>(items);
        if (filter != null)
            list.removeIf(item -> !ItemMaster.checkFilter(item, filter));
        return list;
    }

    List<InvItemActor> getInventorySlots();

    InventoryClickHandler getClickHandler();

     int getPrice(HeroItem model, CELL_TYPE cellType);
}
