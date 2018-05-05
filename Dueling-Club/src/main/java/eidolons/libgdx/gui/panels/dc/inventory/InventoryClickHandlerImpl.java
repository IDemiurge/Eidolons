package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.entity.item.DC_InventoryManager;
import eidolons.entity.item.DC_InventoryManager.OPERATIONS;
import eidolons.entity.item.DC_JewelryObj;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.HeroManager;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 3/30/2017.
 */
public class InventoryClickHandlerImpl implements InventoryClickHandler {
    protected final HqDataMaster dataMaster;
    protected final DC_InventoryManager manager;
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    protected HeroDataModel sim;
    protected boolean dirty;

    public InventoryClickHandlerImpl(HqDataMaster dataMaster, HeroDataModel sim) {
        this.dataMaster = dataMaster;
        this.sim = sim;
        manager = Eidolons.game.getInventoryManager();
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                               boolean altClick, Entity cellContents) {

        boolean result = false;
        OPERATIONS operation = getOperation(cell_type, clickCount, rightClick,
         altClick, cellContents);
        if (operation == null) {
            result = false;
        } else {

                if (manager.canDoOperation(operation, cellContents)) {

                    execute(operation, cellContents);
                    manager.operationDone(operation);
                    dirty = true;
                    result = true;
                }
        }

        if (result) {
            refreshPanel();
        }

        return result;

    }

    private void execute(OPERATIONS operation, Entity type) {
        Object secondArg = getSecondArg(operation, type);
        HqDataMaster.operation(sim, getHqOperation(operation), type, secondArg);
//         new EnumMaster<HQ_OPERATION>().retrieveEnumConst(HQ_OPERATION.class, operation.name()),
//         item);
    }

    private Object getSecondArg(OPERATIONS operation, Entity type) {
        if (operation == OPERATIONS.EQUIP) {
            if (type instanceof DC_JewelryObj) {
                return null;
            }
            return HeroManager.getItemSlot(dataMaster.getHeroModel(), type);
        }
        return null;
    }

    private HQ_OPERATION getHqOperation(OPERATIONS operation) {
        switch (operation) {
            case PICK_UP:
                return HQ_OPERATION.PICK_UP;
            case DROP:
                return HQ_OPERATION.DROP;
            case UNEQUIP:
                return HQ_OPERATION.UNEQUIP;
            case UNEQUIP_QUICK_SLOT:
                return HQ_OPERATION.UNEQUIP_QUICK_SLOT;
            case EQUIP:
                return HQ_OPERATION.EQUIP;
            case EQUIP_QUICK_SLOT:
                return HQ_OPERATION.EQUIP_QUICK_SLOT;
        }
        return null;
    }


    protected OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                                      boolean altClick, Entity cellContents) {
        if (cell_type == null) {
            return null;
        }
        if (cellContents == null) {
            return null;
        }
        switch (cell_type) {
            case AMULET:
            case RING:
            case WEAPON_MAIN:
            case WEAPON_OFFHAND:
                if (altClick) {
                    return OPERATIONS.DROP;
                }
                if (rightClick || clickCount > 1) {
                    return OPERATIONS.UNEQUIP;
                }
                return null;

            case QUICK_SLOT:
                if (rightClick) {
                    return OPERATIONS.UNEQUIP_QUICK_SLOT;
                }
                if (clickCount > 1) {
                    if (HeroManager.isQuickSlotWeapon(cellContents)) {
                        return OPERATIONS.EQUIP;
                    } else {
                        return OPERATIONS.UNEQUIP_QUICK_SLOT;
                    }
                }
                if (altClick) {
                    return OPERATIONS.DROP;
                }
                return null;
            case ARMOR:
                //preCheck can be unequipped

                break;
            case INVENTORY:
                if (altClick) {
                    return OPERATIONS.EQUIP_QUICK_SLOT;
                }
                if (rightClick) {
                    return OPERATIONS.DROP;
                }
                if (clickCount > 1) {
                    if (HeroManager.isQuickSlotOnly(cellContents))
                        return OPERATIONS.EQUIP_QUICK_SLOT;
                    return OPERATIONS.EQUIP;
                }
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
        if (!isUndoEnabled()) {
            return;
        }
        dataMaster.undo_();

        Integer op = manager.getOperationsLeft();
        op--;
        if (op == manager.getOperationsPool()) {
            dirty = false;
        }
        refreshPanel();

    }

    public void refreshPanel() {
        GuiEventManager.trigger(GuiEventType.UPDATE_INVENTORY_PANEL);
//        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, sim.getHero());
    }

    @Override
    public void doneClicked() {
        if (!isDoneEnabled()) {
            return;
        }
        apply();
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, true);
    }

    @Override
    public void apply() {
        dataMaster.applyModifications();
        GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO,
         dataMaster.getHeroModel().getHero());
    }

    @Override
    public void cancelClicked() {
        if (!isCancelEnabled()) {
            return;
        }
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, false);

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

    protected String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return cellContents.getName();
    }

}
