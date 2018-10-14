package eidolons.libgdx.gui.panels.dc.inventory.shop;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_InventoryManager.OPERATIONS;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerClickHandler;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.macro.entity.town.Shop;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/13/2018.
 */
public class ShopClickHandler extends ContainerClickHandler {

    public ShopClickHandler(Shop shop, Unit unit) {
        super(shop.getImagePath(), shop.getItems(), unit, shop);
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        if (cellContents == null) {
            return false;
        }
        if (sim.isInventoryFull()) {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.DEFAULT,
             "Inventory is full!", sim);
            return false;
        }
        DC_HeroItemObj item = (DC_HeroItemObj) cellContents;
        buy(item);

        OPERATIONS operation = getInvOperation(cell_type, clickCount, rightClick, altClick, cellContents);

        if (operation == OPERATIONS.DROP) {
            if (cell_type == CELL_TYPE.CONTAINER) {
                operation = OPERATIONS.BUY;
            } else
                operation = OPERATIONS.SELL;
        }
        if (handleOperation(operation, cell_type, cellContents)) {
            return true;
        }
        return false;
    }

    @Override
    protected void execute(OPERATIONS operation, Entity type, Object arg) {
        HqDataMaster.operation(sim, getHqOperation(operation), type, arg);
        refreshPanel();
    }

    @Override
    public void refreshPanel() {
        GuiEventManager.trigger(GuiEventType.UPDATE_SHOP, container);
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
        switch (operation) {
            case SELL:

            case BUY:
                Integer gold = (operation == OPERATIONS.BUY ? dataMaster.getHeroModel() : container)
                 .getIntParam(PARAMS.GOLD);
                if (gold >= type.getIntParam(PARAMS.GOLD_COST))
                    return true;
                GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
                 (operation == OPERATIONS.BUY ?
                  "You need " + (type.getIntParam(PARAMS.GOLD_COST) - gold) + " more gold!"
                  :
                  "Shopkeeper needs " + (type.getIntParam(PARAMS.GOLD_COST) - gold) + " more gold!"
                 ));
                return false;
        }
        return super.canDoOperation(operation, type, arg);
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

    @Override
    protected OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return super.getOperation(cell_type, clickCount, rightClick, altClick, cellContents);
    }

    public void buy(DC_HeroItemObj item) {

    }

    public void sell() {

    }
}
