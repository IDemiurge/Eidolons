package eidolons.libgdx.gui.panels.dc.inventory.datasource;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.inventory.*;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

public class InventoryDataSource implements QuickSlotDataSource,
 InventoryTableDataSource,
 EquipDataSource {

    private final HqDataMaster dataMaster;
    private InventoryFactory factory;
    private HeroDataModel unit;
    private InventoryClickHandler handler;

    public InventoryDataSource(Unit unit) {
        if (HqPanel.getActiveInstance()==null )
             dataMaster =HqDataMaster.createAndSaveInstance(unit);
        else
            dataMaster =HqDataMaster.getInstance(unit);
        this.unit = dataMaster.getHeroModel();
        handler = new InventoryClickHandlerImpl(
         dataMaster, dataMaster.getHeroModel());
        factory = new InventoryFactory(handler);
    }

    public InventoryClickHandler getHandler() {
        return handler;
    }

    @Override
    public InventoryValueContainer mainWeapon() {
        return factory.get(unit.getMainWeapon(), CELL_TYPE.WEAPON_MAIN);
    }

    @Override
    public InventoryValueContainer offWeapon() {
        return factory.get(unit.getOffhandWeapon(), CELL_TYPE.WEAPON_OFFHAND);
    }

    @Override
    public InventoryValueContainer armor() {
        return factory.get(unit.getArmor(), CELL_TYPE.ARMOR);
    }

    @Override
    public InventoryValueContainer avatar() {
        return factory.get(unit, null);
    }

    @Override
    public InventoryValueContainer amulet() {
        return factory.get(unit.getAmulet(), CELL_TYPE.AMULET);
    }

    @Override
    public List<InventoryValueContainer> getQuickSlots() {
        List<DC_HeroItemObj> slots = new ArrayList<>(  unit.getQuickItems());
        ListMaster.fillWithNullElements(slots
         , unit.getQuickSlotsMax());
        return factory.getList(slots, CELL_TYPE.QUICK_SLOT);
    }

    @Override
    public List<InventoryValueContainer> rings() {
        List<InventoryValueContainer> list = factory.getList(unit.getRings(),
         CELL_TYPE.RING);
        ListMaster.fillWithNullElements(list, 8);
        return list;
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        List<DC_HeroItemObj> inv = new ArrayList<>(  unit.getInventory());
        ListMaster.fillWithNullElements(inv
         , InventorySlotsPanel.SIZE);
        List<InventoryValueContainer> list =
         factory.getList(inv, CELL_TYPE.INVENTORY);
        return list;
    }

    public Runnable getDoneHandler() {
        return  ()-> handler.doneClicked();
    }

    public Runnable getUndoHandler() {
        return  ()-> handler.undoClicked();
    }

    public Runnable getCancelHandler() {
        return  ()-> handler.cancelClicked();
    }

    public boolean isDoneDisabled() {
        return !handler.isDoneEnabled();
    }

    public boolean isCancelDisabled() {
        return !handler.isCancelEnabled();
    }

    public boolean isUndoDisabled() {
        return !handler.isUndoEnabled();
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
    public String getGoldInfo() {
        return  unit.getIntParam(PARAMS.GOLD) +"gp";
    }
    public String getSlotsInfo() {
        return unit.getOccupiedQuickSlots()+"/" +
         unit.getIntParam(PARAMS.QUICK_SLOTS) ;
    }
}
