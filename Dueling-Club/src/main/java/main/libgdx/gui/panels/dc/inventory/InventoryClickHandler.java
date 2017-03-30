package main.libgdx.gui.panels.dc.inventory;

import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.entity.Entity;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 3/30/2017.
 */
public class InventoryClickHandler {
    public void cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                            boolean altClick     , Entity cellContents) {

        OPERATIONS operation=getOperation(cell_type, clickCount, rightClick,
         altClick, cellContents);
        String arg=getArg(cell_type, clickCount, rightClick,
         altClick, cellContents);
        Eidolons.game.getInventoryManager().tryExecuteOperation(operation, arg);


    }

    private String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return cellContents.getName();
    }

    public OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                            boolean altClick     , Entity cellContents) {
        switch (cell_type) {
            case AMULET:
                break;
            case RING:
                break;
            case QUICK_SLOT:
                break;
            case WEAPON_MAIN:
                break;
            case WEAPON_OFFHAND:
                break;
            case ARMOR:
                break;
            case INVENTORY:
                break;
        }
        return null;
    }
    public enum CELL_TYPE {
        AMULET,
        RING, QUICK_SLOT,
        WEAPON_MAIN,
        WEAPON_OFFHAND,
        ARMOR,
        INVENTORY
    }
}
