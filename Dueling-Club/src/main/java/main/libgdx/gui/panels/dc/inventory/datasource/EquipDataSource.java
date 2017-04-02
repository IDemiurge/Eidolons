package main.libgdx.gui.panels.dc.inventory.datasource;

import main.libgdx.gui.panels.dc.inventory.containers.InventoryValueContainer;

import java.util.List;

public interface EquipDataSource {
    InventoryValueContainer mainWeapon();

    InventoryValueContainer offWeapon();

    InventoryValueContainer armor();

    InventoryValueContainer avatar();

    InventoryValueContainer amulet();

    List<InventoryValueContainer> rings();
}
