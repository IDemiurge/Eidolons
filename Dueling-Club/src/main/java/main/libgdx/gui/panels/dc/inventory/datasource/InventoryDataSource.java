package main.libgdx.gui.panels.dc.inventory.datasource;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.obj.unit.Unit;
import main.game.core.Eidolons;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandlerImpl;
import main.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import main.libgdx.gui.panels.dc.inventory.containers.InventoryValueContainer;
import main.libgdx.gui.panels.dc.inventory.containers.InventoryValueContainerFactory;
import main.system.auxiliary.data.ListMaster;

import java.util.LinkedList;
import java.util.List;

public class InventoryDataSource implements QuickSlotDataSource,
        InventoryTableDataSource,
        EquipDataSource {

    private InventoryValueContainerFactory factory;
    private Unit unit;
    private InventoryClickHandlerImpl handler;

    public InventoryDataSource(Unit unit) {
        this.unit = unit;
        handler = new InventoryClickHandlerImpl(unit);
        factory = new InventoryValueContainerFactory(handler);
    }

    public InventoryClickHandlerImpl getHandler() {
        return handler;
    }

    @Override
    public InventoryValueContainer mainWeapon() {
        return factory.get(unit.getMainWeapon(), CELL_TYPE.WEAPON_MAIN);
    }

    @Override
    public InventoryValueContainer offWeapon() {
        return factory.get(unit.getSecondWeapon(), CELL_TYPE.WEAPON_OFFHAND);
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
        return factory.getList(unit.getQuickItems(), CELL_TYPE.QUICK_SLOT);
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
        List<InventoryValueContainer> list = new LinkedList<>(factory.getList(unit.getInventory(), CELL_TYPE.INVENTORY));
        ListMaster.fillWithNullElements(list
                , InventorySlotsPanel.SIZE);
        return list;
    }

    public ClickListener getDoneHandler() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handler.doneClicked();
            }
        };
    }

    public ClickListener getUndoHandler() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handler.undoClicked();
            }
        };
    }

    public ClickListener getCancelHandler() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handler.cancelClicked();
            }
        };
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
        return Eidolons.game.getInventoryManager().getOperationsLeft() + "/" +
                Eidolons.game.getInventoryManager().getOperationsPool();
    }
}
