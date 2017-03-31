package main.libgdx.gui.panels.dc.inventory.datasource;

import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.QuickSlotsDataSource;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import main.libgdx.gui.panels.dc.inventory.InventoryValueContainer;

import java.util.List;

public class InventoryDataSource implements
        InventoryTableDataSource, QuickSlotsDataSource, InventoryClickHandler,
        EquipDataSource {

    private Unit unit;

    public InventoryDataSource(Unit unit) {
        this.unit = unit;
    }


    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return false;
    }

    @Override
    public List<ActionValueContainer> getQuickSlotActions() {
        return null;
    }

    @Override
    public InventoryValueContainer mainWeapon() {
        return null;
    }

    @Override
    public InventoryValueContainer offWeapon() {
        return null;
    }

    @Override
    public InventoryValueContainer armor() {
        return null;
    }

    @Override
    public InventoryValueContainer avatar() {
        return null;
    }

    @Override
    public InventoryValueContainer amulet() {
        return null;
    }

    @Override
    public List<InventoryValueContainer> rings() {
        return null;
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        return null;
    }
}
