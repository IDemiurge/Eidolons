package eidolons.libgdx.gui.panels.dc.inventory.datasource;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.module.herocreator.logic.items.ItemMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InvItemActor;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface InventoryTableDataSource {
    void setFilter(ITEM_FILTERS filter);

    default List<DC_HeroItemObj> applyFilter(Collection<DC_HeroItemObj> items, ITEM_FILTERS filter) {
        List<DC_HeroItemObj> list = new ArrayList<>(items);
        if (filter != null)
            list.removeIf(item -> !ItemMaster.checkFilter(item, filter));
        return list;
    }

    List<InvItemActor> getInventorySlots();

    InventoryClickHandler getClickHandler();

     int getPrice(DC_HeroItemObj model, CELL_TYPE cellType);
}
