package eidolons.libgdx.gui.panels.dc.inventory.shop;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.DC_InventoryManager.OPERATIONS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerClickHandler;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMasterDirect;
import eidolons.macro.entity.shop.Shop;
import eidolons.macro.entity.shop.ShopTransactions;
import eidolons.system.audio.DC_SoundMaster;
import main.entity.Entity;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.AudioEnums;

/**
 * Created by JustMe on 10/13/2018.
 */
public class ShopClickHandler extends ContainerClickHandler {

    public static boolean stashOpen;
    private final Shop shop;

    public ShopClickHandler(Shop shop, Unit unit) {
        super(HqDataMasterDirect.getInstance(unit),
         shop.getImagePath(), shop.getItems(), shop);
        this.shop = shop;
    }

    public static void setStashOpen(boolean stashOpen) {
        ShopClickHandler.stashOpen = stashOpen;
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents, boolean ctrlClick) {
        if (cellContents == null) {
            singleClick(cell_type, null);
            return false;
        }

        OPERATIONS operation = null;
        if (clickCount > 1 || rightClick) {
            if (cell_type == CELL_TYPE.CONTAINER) {
                operation = OPERATIONS.BUY;
            } else if (stashOpen) {
                operation = OPERATIONS.STASH;
            } else {
                if (rightClick)
                    operation = OPERATIONS.SELL;
            }
        }
        if (ctrlClick) {
            if (cell_type == CELL_TYPE.CONTAINER) {
                operation = OPERATIONS.BUY;
            } else {
                if (cell_type == CELL_TYPE.INVENTORY) {
                    operation = OPERATIONS.SELL;
                } else
                    operation = OPERATIONS.UNEQUIP;
            }
        }
        if (operation == null) {
            operation = getInvOperation(cell_type, clickCount, rightClick, altClick, ctrlClick, cellContents);
        }

        if (operation == OPERATIONS.DROP) {
            if (cell_type == CELL_TYPE.CONTAINER) {
                operation = OPERATIONS.BUY;
            } else if (cell_type == CELL_TYPE.STASH) {
                operation = OPERATIONS.UNSTASH;
            } else {
                operation = OPERATIONS.SELL;
            }
        } else if (operation == OPERATIONS.EQUIP || operation == OPERATIONS.EQUIP_QUICK_SLOT) {
            if (cell_type == CELL_TYPE.CONTAINER) {
                operation = OPERATIONS.BUY;
            } else if (stashOpen)
                operation = OPERATIONS.STASH;
        }
        if (operation == null) {
            if (stashOpen) {
                operation = OPERATIONS.UNSTASH;
            }
        }
        return handleOperation(operation, cell_type, cellContents);
    }

    @Override
    protected OPERATIONS getInvOperation(CELL_TYPE cell_type,
                                         int clickCount, boolean rightClick, boolean altClick, boolean ctrlClick, Entity cellContents) {
        if (stashOpen)
            if (altClick || clickCount > 1) {
                if (cell_type == CELL_TYPE.INVENTORY) {
                    return OPERATIONS.STASH;
                }
            }
        return super.getInvOperation(cell_type, clickCount, rightClick, altClick, ctrlClick, cellContents);
    }

    @Override
    protected OPERATIONS getDragOperation(CELL_TYPE cell_type, Entity cellContents, Entity dragged) {
        switch (cell_type) {
            case INVENTORY:
            case WEAPON_MAIN:
            case WEAPON_OFFHAND:
            case ARMOR:
            case QUICK_SLOT:
            case AMULET:
            case RING:
                if (dragged instanceof DC_HeroItemObj) {
                    switch (((DC_HeroItemObj) dragged).getContainer()) {
                        case EQUIPPED:
                            return OPERATIONS.UNEQUIP;
                        case QUICK_SLOTS:
                            return OPERATIONS.UNEQUIP_QUICK_SLOT;
                        case INVENTORY:
                        case CONTAINER:
                            return null;
                        case STASH:
                            return OPERATIONS.UNSTASH;
                        case SHOP:
                            return OPERATIONS.BUY;
                    }
                }
                break;
            case STASH:
                return OPERATIONS.STASH;
            case CONTAINER:
                return OPERATIONS.SELL;
        }

        return super.getDragOperation(cell_type, cellContents, dragged);
    }

    @Override
    protected void execute(OPERATIONS operation, Entity type, Object arg) {
        dataMaster.operation(getHqOperation(operation), type, arg);
        refreshPanel();
    }

    @Override
    public void refreshPanel() {
        GuiEventManager.trigger(GuiEventType.UPDATE_SHOP);
    }

    @Override
    protected boolean handleOperation(OPERATIONS operation, CELL_TYPE cell_type, Entity cellContents) {
        if (operation == null) {
            if (cellContents == null || getDragged() != null)
                singleClick(cell_type, cellContents);
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

    @Override
    protected boolean isBlocked() {
        return false;
    }

    @Override
    protected boolean canDoOperation(OPERATIONS operation, Entity type, Object arg) {
        DC_HeroItemObj item = (DC_HeroItemObj) type;
        switch (operation) {
            case STASH:
                return !Eidolons.getTown().isStashFull();
            case UNSTASH:
                return checkCanAddInventory();
            case SELL:
                //                if (!getShop().canSellTo(item, dataMaster.getHeroModel(), false)) {
                //                    if (getShop().canSellTo(item, dataMaster.getHeroModel(), true)) {
                //                        if (ShopTransactions.confirmSell())
                //                            return true;
                //                    }
                //                    GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
                //                     "Shopkeeper cannot afford to buy this!");
                //                    return false;
                //                }
                //                return true;
            case BUY:
                boolean heroBuys = operation == OPERATIONS.BUY;
                int debt = getShop().getBalanceForBuy(item, dataMaster.getHeroModel(), heroBuys);
                if (debt < 0) {
                    debt = Math.abs(debt);
                } else debt = 0;
                int max = heroBuys ? shop.getMaxDebt() : shop.getMinBalance();
                if (debt > 0) {
                    if (debt < Math.abs(max)) {
                        if (ShopTransactions.confirm(debt, max, item, shop, heroBuys))
                            return true;
                    }
                    GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
                     heroBuys ? "You cannot afford to buy this!"
                      : "Shopkeeper cannot afford to buy this!");
                    return false;
                }
                return true;
            //                Integer gold = (operation == OPERATIONS.BUY ? dataMaster.getHeroModel() : container)
            //                 .getIntParam(PARAMS.GOLD);
            //                if (gold >= getShop().getPrice((DC_HeroItemObj) type, dataMaster.getHeroModel(),
            //                 operation == OPERATIONS.BUY)) {
            //                    return true;
            //                }
            //                GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
            //                 (operation == OPERATIONS.BUY ?
            //                  "You need " + (type.getIntParam(PARAMS.GOLD_COST) - gold) + " more gold!"
            //                  :
            //                  "Shopkeeper needs " + (type.getIntParam(PARAMS.GOLD_COST) - gold) + " more gold!"
            //                 ));
            //                return false;
        }
        return super.canDoOperation(operation, type, arg);
    }

    public Shop getShop() {
        return (Shop) super.getContainer();
    }

    @Override
    protected Object getSecondArg(OPERATIONS operation, Entity type) {
        switch (operation) {
            case SELL:
            case BUY:
                return container;
        }
        return super.getSecondArg(operation, type);
    }


    public void askRepair() {
        int gold = 0;
        for (DC_HeroSlotItem item : hero.getSlotItems()) {
            int percentage = 100 -
             item.getIntParam(PARAMS.C_DURABILITY) * 100 / item.getIntParam(PARAMS.DURABILITY);
            Integer price = shop.getPrice(item, hero, false) * percentage / 100;
            gold += price;
        }
        if (gold == 0) {
            EUtils.showInfoText("Your equpped items don't need repair yet!");
            return;
        }
        gold -= gold * hero.getIntParam(PARAMS.ARMORER_MASTERY) / 100;

        int gold_ = gold;
        if (hero.checkParameter(PARAMS.GOLD, gold))
            EUtils.onConfirm("Repair all equipped items for " +
             gold +
             " gold?", true, () -> {
                repairEquipped();
                hero.modifyParameter(PARAMS.GOLD, -gold_);
            });
    }

    private void repairEquipped() {
        for (DC_HeroSlotItem item : hero.getSlotItems()) {
            item.setParam(PARAMS.C_DURABILITY, item.getIntParam(PARAMS.DURABILITY));
        }
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CHAIN);
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.BUY);
        update();
    }

    @Override
    protected void update() {
        GuiEventManager.trigger(getGuiEvent(), shop);
    }

    @Override
    protected EventType getGuiEvent() {
        return GuiEventType.UPDATE_SHOP;
    }

}
