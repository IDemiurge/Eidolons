package libgdx.gui.panels.dc.inventory.datasource;

import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import libgdx.gui.panels.dc.inventory.*;
import libgdx.gui.panels.dc.inventory.InvItemActor;
import libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import eidolons.content.consts.VisualEnums.CELL_TYPE;
import libgdx.gui.panels.dc.inventory.InventoryFactory;
import libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;
import libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InventoryDataSource implements QuickSlotDataSource,
 InventoryTableDataSource,
 EquipDataSource {

    protected final HqDataMaster dataMaster;
    protected HeroDataModel unit;
    protected ITEM_FILTERS filter;
    private InventoryFactory factory;
    private InventoryClickHandler handler;

    public InventoryDataSource(Unit unit) {
        dataMaster =getDataMaster(unit);
        this.unit = dataMaster.getHeroModel();
        setHandler(new InventoryClickHandlerImpl(
         dataMaster, dataMaster.getHeroModel()));
        setFactory(new InventoryFactory(getHandler()));
    }

    protected HqDataMaster getDataMaster(Unit unit) {
        return HqDataMaster.getOrCreateInstance( unit);
    }

    public InventoryClickHandler getHandler() {
        return handler;
    }

    public void setHandler(InventoryClickHandler handler) {
        this.handler = handler;
    }

    @Override
    public InvItemActor mainWeapon() {
        return getFactory().get(unit.getMainWeapon(), VisualEnums.CELL_TYPE.WEAPON_MAIN);
    }

    @Override
    public InvItemActor offWeapon() {
        return getFactory().get(unit.getOffhandWeapon(), VisualEnums.CELL_TYPE.WEAPON_OFFHAND);
    }
    @Override
    public InvItemActor offWeaponReserve() {
        return getFactory().get(unit.getReserveOffhandWeapon(), VisualEnums.CELL_TYPE.WEAPON_OFFHAND_RESERVE);
    }
    @Override
    public InvItemActor mainWeaponReserve() {
        return getFactory().get(unit.getReserveMainWeapon(), VisualEnums.CELL_TYPE.WEAPON_MAIN_RESERVE);
    }


    @Override
    public InvItemActor armor() {
        return getFactory().get(unit.getArmor(), VisualEnums.CELL_TYPE.ARMOR);
    }

    @Override
    public InvItemActor avatar() {
        return getFactory().get(unit, null);
    }

    @Override
    public InvItemActor amulet() {
        return getFactory().get(unit.getAmulet(), VisualEnums.CELL_TYPE.AMULET);
    }

    @Override
    public List<InvItemActor> getQuickSlots() {
        List<DC_HeroItemObj> slots = new ArrayList<>(unit.getQuickItems());
        ListMaster.fillWithNullElements(slots
         , unit.getQuickSlotsMax());
        return getFactory().getList(slots, VisualEnums.CELL_TYPE.QUICK_SLOT);
    }

    @Override
    public List<InvItemActor> rings() {
        List<InvItemActor> list = getFactory().getList(unit.getRings(),
         VisualEnums.CELL_TYPE.RING);
        ListMaster.fillWithNullElements(list, 8);
        return list;
    }

    @Override
    public void setFilter(ITEM_FILTERS filter) {
        this.filter = filter;
    }

    public List<InvItemActor> getInventorySlots() {
        return getInventorySlots(unit.getInventory());
    }

    @Override
    public InventoryClickHandler getClickHandler() {
        return handler;
    }

    @Override
    public int getPrice(DC_HeroItemObj model, CELL_TYPE cellType) {
        return model.getIntParam(PARAMS.GOLD_COST);
    }

    public List<InvItemActor> getInventorySlots(Collection<DC_HeroItemObj> items) {
        List<DC_HeroItemObj> inv = applyFilter(items, filter);
        ListMaster.fillWithNullElements(inv
         , InventorySlotsPanel.SIZE);
        return getFactory().getList(inv, VisualEnums.CELL_TYPE.INVENTORY);
    }

    public Runnable getDoneHandler() {
        return () -> getHandler().doneClicked();
    }

    public Runnable getUndoHandler() {
        return () -> getHandler().undoClicked();
    }

    public Runnable getCancelHandler() {
        return () -> getHandler().cancelClicked();
    }

    public boolean isDoneDisabled() {
        return !getHandler().isDoneEnabled();
    }

    public boolean isCancelDisabled() {
        return !getHandler().isCancelEnabled();
    }

    public boolean isUndoDisabled() {
        return !getHandler().isUndoEnabled();
    }

    public String getOperationsString() {
        return unit.getHero().getGame().getInventoryManager().getOperationsLeft() + "/" +
         unit.getHero().getGame().getInventoryManager().getOperationsPool();
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isOverburdened() {
        return unit.calculateCarryingWeight() >=
         unit.getIntParam(PARAMS.CARRYING_CAPACITY);
    }

    public String getWeightInfo() {
        return unit.calculateCarryingWeight() + "/" +
         unit.getIntParam(PARAMS.CARRYING_CAPACITY) +
         "lb";
    }

    public String getGoldInfo() {
        return unit.getIntParam(PARAMS.GOLD) + "gp";
    }

    public String getSlotsInfo() {
        return unit.getOccupiedQuickSlots() + "/" +
         unit.getIntParam(PARAMS.QUICK_SLOTS);
    }

    public InventoryFactory getFactory() {
        return factory;
    }

    public void setFactory(InventoryFactory factory) {
        this.factory = factory;
    }
}
