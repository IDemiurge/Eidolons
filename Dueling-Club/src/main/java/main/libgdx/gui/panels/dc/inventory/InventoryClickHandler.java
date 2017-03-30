package main.libgdx.gui.panels.dc.inventory;

import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 3/30/2017.
 */
public class InventoryClickHandler {

    //IDEA: FOR NON-COMBAT, DROP == SELL!
    Unit unit;
    Unit buffer;

    public InventoryClickHandler(Unit unit) {
        this.unit = unit;
//        buffer = unit.getGame().getState().getManager()
//         .getKeeper().getCloner().clone()

//        InventoryTransactionManager.updateType(hero);
//        bufferedType = hero.getType();
//        heroModel = hero;
//        inventoryManager.getInvListManager().setHero(heroModel);
//        CharacterCreator.getHeroManager().addHero(heroModel);


//        operationsData = "";
//        cachedValue = cell.getProperty(PROPS.DROPPED_ITEMS);
    }

    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                               boolean altClick, Entity cellContents) {

        OPERATIONS operation = getOperation(cell_type, clickCount, rightClick,
         altClick, cellContents);
        if (operation == null) return false;
        String arg = getArg(cell_type, clickCount, rightClick,
         altClick, cellContents);
        if (arg == null) return false;
      return   Eidolons.game.getInventoryManager().tryExecuteOperation(operation, arg);

    }
public boolean itemDragAndDropped(CELL_TYPE cell_type,
 Entity cellContents, Entity droppedItem
){

    return false;
}

    public void undoClicked(){
//        inventoryManager.getInvListManager().setNumberOfOperations(getNumberOfOperations());

    }
    public void doneClicked(){
//        InventoryTransactionManager.updateType(getHero());
//        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);
//        CharacterCreator.getHeroManager().removeHero(heroModel);
    }
    public void cancelClicked(){

//        cell.setProperty(PROPS.DROPPED_ITEMS, cachedValue);
//        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, false);
//        CharacterCreator.getHeroManager().removeHero(heroModel);
    }
    public boolean isUndoEnabled(){

        return true;
    }
    public boolean isDoneEnabled(){

        return true;
    }
    public boolean isCancelEnabled(){

        return true;
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
                return null ;

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

    public enum CELL_TYPE {
        AMULET,
        RING, QUICK_SLOT,
        WEAPON_MAIN,
        WEAPON_OFFHAND,
        ARMOR,
        INVENTORY
    }
}
