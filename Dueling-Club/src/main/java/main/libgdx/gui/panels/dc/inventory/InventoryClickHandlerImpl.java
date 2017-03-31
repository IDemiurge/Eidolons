package main.libgdx.gui.panels.dc.inventory;

import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.entity.Entity;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 3/30/2017.
 */
public class InventoryClickHandlerImpl implements InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                               boolean altClick, Entity cellContents) {

        OPERATIONS operation = getOperation(cell_type, clickCount, rightClick,
                altClick, cellContents);
        if (operation == null) return false;
        String arg = getArg(cell_type, clickCount, rightClick,
                altClick, cellContents);
        if (arg == null) return false;
        return Eidolons.game.getInventoryManager().tryExecuteOperation(operation, arg);

    }

    private String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return cellContents.getName();
    }

    private OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                                    boolean altClick, Entity cellContents) {
        if (cellContents == null) return null;
        switch (cell_type) {
            case AMULET:
            case RING:
            case QUICK_SLOT:
            case WEAPON_MAIN:
            case WEAPON_OFFHAND:
                if (altClick)
                    return OPERATIONS.DROP;
                if (rightClick || clickCount > 1)
                    return OPERATIONS.UNEQUIP;
                return null;

            case ARMOR:
                //check can be unequipped

                break;
            case INVENTORY:
                if (altClick)
                    return OPERATIONS.EQUIP_QUICK_SLOT;
                if (rightClick)
                    return OPERATIONS.DROP;
                if (clickCount > 1)
                    return OPERATIONS.EQUIP;
        }
        return null;
    }

}
