package main.libgdx.gui.panels.dc.inventory;

import main.entity.Entity;

public interface InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                        boolean altClick, Entity cellContents);

    public enum CELL_TYPE {
        AMULET,
        RING, QUICK_SLOT,
        WEAPON_MAIN,
        WEAPON_OFFHAND,
        ARMOR,
        INVENTORY
    }
}
