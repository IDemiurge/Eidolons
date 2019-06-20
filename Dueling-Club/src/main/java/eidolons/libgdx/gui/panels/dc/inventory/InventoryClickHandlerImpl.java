package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_InventoryManager;
import eidolons.entity.item.DC_InventoryManager.OPERATIONS;
import eidolons.entity.item.DC_JewelryObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.HeroManager;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.ItemEnums.JEWELRY_TYPE;
import main.content.values.properties.G_PROPS;
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
    protected HeroDataModel hero;
    protected boolean dirty;

    public InventoryClickHandlerImpl(HqDataMaster dataMaster, HeroDataModel hero) {
        this.dataMaster = dataMaster;
        this.hero = hero;
        manager = Eidolons.game.getInventoryManager();
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                               boolean altClick, Entity cellContents, boolean ctrlClick) {

        OPERATIONS operation = getInvOperation(cell_type, clickCount, rightClick,
                altClick, ctrlClick, cellContents);
        return handleOperation(operation, cell_type, cellContents);
    }

    protected boolean handleOperation(OPERATIONS operation, CELL_TYPE cell_type, Entity cellContents) {
        if (operation == null) {
            //empty Clicked 
            if (cellContents == null || getDragged() != null)
                singleClick(cell_type, cellContents);
            //                        if (!rightClick) {
            //                            singleClick(cell_type, cellContents);
            //                            return true;
            //                        }
        } else {
            Object arg = getSecondArg(operation, cellContents);
            if (!isBlocked())
                if (canDoOperation(operation, cellContents, arg)) {
                    execute(operation, cellContents, arg);
                    return true;
                }
        }
        return false;
    }

    public void singleClick(CELL_TYPE cell_type, Entity cellContents) {
        if (getDragged() != null) {
            if (getDragged() == cellContents) {
                setDragged(null);
                return;
            }
            OPERATIONS operation = getDragOperation(cell_type, cellContents, getDragged());
            if (operation == null) {
                if (cellContents == null) {
                    //error
                } else {
                    setDragged(cellContents);
                }
                return;
            }
            Object arg = getSlotArg(cell_type);
            if (arg == null)
                arg = getSecondArg(operation, getDragged());

            if (!isBlocked()) {
                if (canDoOperation(operation, getDragged(), arg)) {
                    execute(operation, getDragged(), arg);
                    setDragged(cellContents);
                } else {
                    if (cellContents == null) {
                        setDragged(null);
                    }
                }
            }
        } else {
            setDragged(cellContents);
        }

    }

    protected boolean checkCanAddInventory() {
        if (hero.isInventoryFull()) {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.DEFAULT,
                    "Inventory is full!", hero);
            EUtils.showInfoText("Inventory is full!");
            return false;
        }
        return true;
    }

    protected boolean isBlocked() {
        if (!ExplorationMaster.isExplorationOn())
            if (manager != null)
                if (!manager.hasOperations()) {
                    return true;
                }
        return false;
    }

    protected boolean canDoOperation(OPERATIONS operation, Entity type, Object arg) {
        if (arg == null) {
            switch (operation) {
                case EQUIP:
                case EQUIP_RESERVE:
                    if (type.getOBJ_TYPE_ENUM() != DC_TYPE.JEWELRY)
                        return false;
            }
        }
        switch (operation) {
            case EQUIP_QUICK_SLOT:
                if (!HeroManager.isQuickItem(type)) {
                    return false;
                }
                if (hero.isQuickSlotsFull()) {
                    GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
                            "Quick slots are full!");
                    return false;
                }
                break;
        }
        if (manager == null) {
            return true;
        }
        return manager.hasOperations();
    }

    protected ITEM_SLOT getSlotArg(CELL_TYPE cell_type) {
        switch (cell_type) {
            case WEAPON_MAIN:
                return ITEM_SLOT.MAIN_HAND;
            case WEAPON_OFFHAND:
                return ITEM_SLOT.OFF_HAND;
            case ARMOR:
                return ITEM_SLOT.ARMOR;
            case WEAPON_MAIN_RESERVE:
                return ITEM_SLOT.RESERVE_MAIN_HAND;
            case WEAPON_OFFHAND_RESERVE:
                return ITEM_SLOT.RESERVE_OFF_HAND;
        }
        return null;
    }

    protected OPERATIONS getDragOperation(CELL_TYPE cell_type, Entity cellContents, Entity dragged) {
        switch (cell_type) {
            case QUICK_SLOT:
                return OPERATIONS.EQUIP_QUICK_SLOT;
            case CONTAINER:
                //                if (cellContents == null) should we be selective?
                return OPERATIONS.DROP;
            case STASH:
                //                if (cellContents == null)
                return OPERATIONS.STASH;
            case INVENTORY:
                //                if (cellContents == null)
                return OPERATIONS.UNEQUIP;
        }
        if (checkContentMatches(cell_type, dragged)) {
            if (cell_type == CELL_TYPE.WEAPON_MAIN_RESERVE) {
                return OPERATIONS.EQUIP_RESERVE;
            }
            if (cell_type == CELL_TYPE.WEAPON_OFFHAND_RESERVE) {
                return OPERATIONS.EQUIP_RESERVE;
            }
            return OPERATIONS.EQUIP;
        }
        return null;
    }


    protected boolean checkContentMatches(CELL_TYPE cell_type, Entity dragged) {
        switch (cell_type) {
            case WEAPON_OFFHAND:
            case WEAPON_OFFHAND_RESERVE:
            case WEAPON_MAIN_RESERVE:
                //                if (dragged)
            case WEAPON_MAIN:
                if (dragged instanceof DC_WeaponObj)
                    return HeroManager.canEquip(dataMaster.getHeroModel(), dragged,
                            getSlotArg(cell_type));

            case ARMOR:
                return dragged instanceof DC_ArmorObj;
            case QUICK_SLOT:
                return HeroManager.isQuickItem(dragged);

            case AMULET:
                return dragged instanceof DC_JewelryObj
                        && dragged.getProperty(G_PROPS.JEWELRY_TYPE).
                        equalsIgnoreCase(JEWELRY_TYPE.AMULET.toString());

            case RING:
                return dragged instanceof DC_JewelryObj
                        && dragged.getProperty(G_PROPS.JEWELRY_TYPE).
                        equalsIgnoreCase(JEWELRY_TYPE.RING.toString());


        }
        return false;
    }

    protected void execute(OPERATIONS operation, Entity type, Object arg) {


        HqDataMaster.operation(hero, getHqOperation(operation), type, arg);
        if (manager != null)
            manager.operationDone(operation);
        dirty = true;
        refreshPanel();
        //         new EnumMaster<HQ_OPERATION>().retrieveEnumConst(HQ_OPERATION.class, operation.name()),
        //         item);
    }

    protected Object getSecondArg(OPERATIONS operation, Entity type) {
        if (operation == OPERATIONS.EQUIP || operation == OPERATIONS.EQUIP_RESERVE) {
            if (type instanceof DC_JewelryObj) {
                return null;
            }
            ITEM_SLOT slot = HeroManager.getItemSlot(dataMaster.getHeroModel(), type);
            if (operation == OPERATIONS.EQUIP_RESERVE) {
                return slot.getReserve();
            }
            return slot;
        }
        return null;
    }

    protected HERO_OPERATION getHqOperation(OPERATIONS operation) {
        switch (operation) {
            case PICK_UP:
                return HERO_OPERATION.PICK_UP;
            case DROP:
                return HERO_OPERATION.DROP;
            case UNEQUIP:
                return HERO_OPERATION.UNEQUIP;
            case UNEQUIP_QUICK_SLOT:
                return HERO_OPERATION.UNEQUIP_QUICK_SLOT;
            case EQUIP_RESERVE:
                return HERO_OPERATION.EQUIP_RESERVE;
            case EQUIP:
                return HERO_OPERATION.EQUIP;
            case EQUIP_QUICK_SLOT:
                return HERO_OPERATION.EQUIP_QUICK_SLOT;
            case BUY:
                return HERO_OPERATION.BUY;
            case SELL:
                return HERO_OPERATION.SELL;
            case STASH:
                return HERO_OPERATION.STASH;
            case UNSTASH:
                return HERO_OPERATION.UNSTASH;
        }
        return null;
    }


    protected OPERATIONS getInvOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                                         boolean altClick, boolean ctrlClick, Entity cellContents) {
        if (cell_type == null) {
            return null;
        }
        if (cellContents == null) {
            return null;
        }
        switch (cell_type) {
            case AMULET:
            case RING:
            case WEAPON_OFFHAND_RESERVE:
            case WEAPON_MAIN_RESERVE:
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
                    //                  dangerous!!!  if (HeroManager.isQuickSlotWeapon(cellContents)) {
                    //                        return OPERATIONS.EQUIP;
                    //                    } else {
                    return OPERATIONS.UNEQUIP_QUICK_SLOT;
                    //                    }
                }
                if (altClick) {
                    return OPERATIONS.DROP;
                }
                return null;
            case ARMOR:
                //preCheck can be unequipped
                if (!ExplorationMaster.isExplorationOn())
                    GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Cannot (un)equip armor in combat!");
                if (rightClick || clickCount > 1) {
                    return OPERATIONS.UNEQUIP;
                }
                break;
            case STASH:
                if (rightClick || clickCount > 1) {
                    return OPERATIONS.UNSTASH;
                }
                if (altClick)
                    return OPERATIONS.DESTROY;
                break;
            case CONTAINER:
            case INVENTORY:
                if (altClick) {
                    if (hero.getRemainingQuickSlots() > 0)
                        return OPERATIONS.EQUIP_QUICK_SLOT;
                    else {
                        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Not enough quick slots!");
                        return null;
                    }
                }
                if (rightClick || ctrlClick) {
                    return OPERATIONS.DROP;
                }
                if (clickCount > 1) {
                    if (HeroManager.isQuickSlotOnly(cellContents))
                        if (hero.getRemainingQuickSlots() > 0) {
                            return OPERATIONS.EQUIP_QUICK_SLOT;
                        } else {
                            GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT, "Not enough quick slots!");
                            return null;
                        }

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
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY,
                new InventoryDataSource(dataMaster.getHeroModel()));
        //        refreshPanel();

    }

    public void refreshPanel() {
        GuiEventManager.trigger(GuiEventType.UPDATE_INVENTORY_PANEL);
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY,  new InventoryDataSource(hero) );
    }

    @Override
    public void doneClicked() {
        if (!isDoneEnabled()) {
            GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, false);
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

    @Override
    public Entity getDragged() {
        return Eidolons.getScreen().getGuiStage().getDraggedEntity();
    }

    @Override
    public void setDragged(Entity dragged) {
        try {
            Eidolons.getScreen().getGuiStage().setDraggedEntity(dragged);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }
}
