package eidolons.libgdx.gui.panels.dc.inventory.datasource;

import eidolons.libgdx.gui.panels.dc.inventory.InventoryValueContainer;

import java.util.List;

public interface InventoryTableDataSource {
    List<InventoryValueContainer> getInventorySlots();
}
