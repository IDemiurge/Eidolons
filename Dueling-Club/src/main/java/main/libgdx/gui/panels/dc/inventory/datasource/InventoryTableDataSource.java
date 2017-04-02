package main.libgdx.gui.panels.dc.inventory.datasource;

import main.libgdx.gui.panels.dc.inventory.containers.InventoryValueContainer;

import java.util.List;

public interface InventoryTableDataSource {
    List<InventoryValueContainer> getInventorySlots();
}
