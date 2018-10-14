package eidolons.libgdx.gui.panels.dc.inventory.datasource;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.inventory.*;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel.ITEM_FILTERS;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InventoryDataSource implements QuickSlotDataSource,
 InventoryTableDataSource,
 EquipDataSource {

    protected final HqDataMaster dataMaster;
    private InventoryFactory factory;
    protected HeroDataModel unit;
    private InventoryClickHandler handler;
    protected ITEM_FILTERS filter;

    public InventoryDataSource(Unit unit) {
        if (HqPanel.getActiveInstance()==null
         && TownPanel.getActiveInstance()==null)
             dataMaster =HqDataMaster.createAndSaveInstance(unit);
        else
            dataMaster =HqDataMaster.getInstance(unit);
        this.unit = dataMaster.getHeroModel();
        setHandler(new InventoryClickHandlerImpl(
         dataMaster, dataMaster.getHeroModel()));
        setFactory(new InventoryFactory(getHandler()));
    }

    public InventoryClickHandler getHandler() {
        return handler;
    }


    @Override
    public InventoryValueContainer mainWeapon() {
        return getFactory().get(unit.getMainWeapon(), CELL_TYPE.WEAPON_MAIN);
    }

    @Override
    public InventoryValueContainer offWeapon() {
        return getFactory().get(unit.getOffhandWeapon(), CELL_TYPE.WEAPON_OFFHAND);
    }

    @Override
    public InventoryValueContainer armor() {
        return getFactory().get(unit.getArmor(), CELL_TYPE.ARMOR);
    }

    @Override
    public InventoryValueContainer avatar() {
        return getFactory().get(unit, null);
    }

    @Override
    public InventoryValueContainer amulet() {
        return getFactory().get(unit.getAmulet(), CELL_TYPE.AMULET);
    }

    @Override
    public List<InventoryValueContainer> getQuickSlots() {
        List<DC_HeroItemObj> slots = new ArrayList<>(  unit.getQuickItems());
        ListMaster.fillWithNullElements(slots
         , unit.getQuickSlotsMax());
        return getFactory().getList(slots, CELL_TYPE.QUICK_SLOT);
    }

    @Override
    public List<InventoryValueContainer> rings() {
        List<InventoryValueContainer> list = getFactory().getList(unit.getRings(),
         CELL_TYPE.RING);
        ListMaster.fillWithNullElements(list, 8);
        return list;
    }

    @Override
    public void setFilter(ITEM_FILTERS filter) {
        this.filter=filter;
    }

    public List<InventoryValueContainer> getInventorySlots() {
        return getInventorySlots(unit.getInventory());
    }
    public List<InventoryValueContainer> getInventorySlots(Collection<DC_HeroItemObj> items) {
        List<DC_HeroItemObj> inv = applyFilter(items, filter);
        ListMaster.fillWithNullElements(inv
         , InventorySlotsPanel.SIZE);
        List<InventoryValueContainer> list =
         getFactory().getList(inv, CELL_TYPE.INVENTORY);
        return list;
    }

    public Runnable getDoneHandler() {
        return  ()-> getHandler().doneClicked();
    }

    public Runnable getUndoHandler() {
        return  ()-> getHandler().undoClicked();
    }

    public Runnable getCancelHandler() {
        return  ()-> getHandler().cancelClicked();
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
        return unit.getHero() .getGame ().getInventoryManager().getOperationsLeft() + "/" +
         unit.getHero().getGame().getInventoryManager().getOperationsPool();
    }

    public Unit getUnit() {
        return unit;
    }

    public String getWeightInfo() {
        return unit.calculateCarryingWeight()+"/" +
         unit.getIntParam(PARAMS.CARRYING_CAPACITY) +
         "lb";
    }
    public String  getGoldInfo() {
        return  unit.getIntParam(PARAMS.GOLD) +"gp";
    }


    public String getSlotsInfo() {
        return unit.getOccupiedQuickSlots()+"/" +
         unit.getIntParam(PARAMS.QUICK_SLOTS) ;
    }

    public InventoryFactory getFactory() {
        return factory;
    }

    public void setFactory(InventoryFactory factory) {
        this.factory = factory;
    }

    public void setHandler(InventoryClickHandler handler) {
        this.handler = handler;
    }
}
