package eidolons.libgdx.gui.panels.dc.inventory.datasource;

import eidolons.libgdx.gui.panels.dc.inventory.InventoryValueContainer;

import java.util.List;

public interface EquipDataSource {
    InventoryValueContainer mainWeapon();

    InventoryValueContainer offWeapon();

    InventoryValueContainer armor();

    InventoryValueContainer avatar();

    InventoryValueContainer amulet();

    List<InventoryValueContainer> rings();
}
