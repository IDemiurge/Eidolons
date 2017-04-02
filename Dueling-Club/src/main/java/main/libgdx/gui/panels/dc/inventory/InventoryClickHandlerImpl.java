package main.libgdx.gui.panels.dc.inventory;

import main.ability.InventoryTransactionManager;
import main.client.cc.CharacterCreator;
import main.client.cc.HeroManager;
import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.Eidolons;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 3/30/2017.
 */
public class InventoryClickHandlerImpl implements InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!

    Unit unit;
    ObjType buffer;
    boolean dirty;

    public InventoryClickHandlerImpl(Unit unit) {
        this.unit = unit;
        buffer = new ObjType(unit.getType());
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

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                               boolean altClick, Entity cellContents) {

        OPERATIONS operation = getOperation(cell_type, clickCount, rightClick,
         altClick, cellContents);
        if (operation == null) return false;
//        String arg = getArg(cell_type, clickCount, rightClick,
//         altClick, cellContents);
//        if (arg == null) return false;
        if (Eidolons.game.getInventoryManager().tryExecuteOperation
         (operation, cellContents)) {
            dirty = true;
            return true;
        }
        return false;

    }


    private OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                                    boolean altClick, Entity cellContents) {
        if (cellContents == null) return null;
        switch (cell_type) {
            case AMULET:
            case RING:
            case WEAPON_MAIN:
            case WEAPON_OFFHAND:
                if (altClick)
                    return OPERATIONS.DROP;
                if (rightClick || clickCount > 1)
                    return OPERATIONS.UNEQUIP;
                return null;

            case QUICK_SLOT:
                if (rightClick)
                    return OPERATIONS.UNEQUIP_QUICK_SLOT;
                if (clickCount > 1) {
                    if (HeroManager.isQuickSlotWeapon(cellContents))
                        return OPERATIONS.EQUIP;
                    else return OPERATIONS.UNEQUIP_QUICK_SLOT;
                }
                if (altClick)
                    return OPERATIONS.DROP;
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

    @Override
    public boolean itemDragAndDropped(CELL_TYPE cell_type,
                                      Entity cellContents, Entity droppedItem
    ) {

        return false;
    }

    @Override
    public void undoClicked() {
//        inventoryManager.getInvListManager().setOperationsLeft(getOperationsLeft());
        if (CharacterCreator.getHeroManager().undo(unit)) {
//         modifications --;
            Integer op = unit.getGame().getInventoryManager().getOperationsLeft();
            op--;
            if (op == unit.getGame().getInventoryManager().getOperationsPool())
                dirty = false;
            refreshPanel();
        }
    }

    public void refreshPanel() {
        GuiEventManager.trigger(GuiEventType.REFRESH_INVENTORY_DIALOG,
         new EventCallbackParam(new InventoryDataSource(unit)));
    }

    @Override
    public void doneClicked() {
//        InventoryTransactionManager.updateType(unit); ???
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);
        CharacterCreator.getHeroManager().removeHero(unit);
        GuiEventManager.trigger(GuiEventType.CLOSE_INVENTORY_DIALOG, null);
    }

    @Override
    public void cancelClicked() {
        unit.applyType(buffer);
//        cell.setProperty(PROPS.DROPPED_ITEMS, cachedValue);TODO
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, false);
        CharacterCreator.getHeroManager().removeHero(unit);
    }

    @Override
    public boolean isUndoEnabled() {
        return dirty;
    }

    @Override
    public boolean isDoneEnabled() {
        return dirty;
    }

    @Override
    public boolean isCancelEnabled() {
        return true;
    }

    private String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return cellContents.getName();
    }

}
