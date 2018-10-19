package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.system.auxiliary.data.ListMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class InventorySlotsPanel extends TablePanelX {

    public static final int ROWS = 3;
    public static final int COLUMNS = 8;
    public static final int SIZE = ROWS * COLUMNS;
    List<InvItemActor> slots;
    private int rows;
    private int cols;

    public InventorySlotsPanel() {
        this(ROWS, COLUMNS);
    }

    public InventorySlotsPanel(int rows, int cols) {
        this.setRows(rows);
        this.setCols(cols);
        setFixedSize(true);
        setSize(COLUMNS * 64, ROWS * 64);
        defaults().space(0).size(64, 64);
    }

    @Override
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.afterUpdateAct(delta);
        final List<InvItemActor> inventorySlots = getSlotActors();
        if(slots!=null )
        if (new ListMaster<InvItemActor>().compare(inventorySlots, slots))
            return;
        clear();
        slots = new ArrayList<>();
        for (int i = 0; i < getRows() * getCols(); i++) {
            InvItemActor actor = null;
            if (inventorySlots.size() > i) {
                actor = inventorySlots.get(i);
            }
            if (actor == null) {
                actor = createEmptySlot();
            } else {
                if (actor.getUserObject() != getUserObject()) {
                    actor.setUserObject(getUserObject());
                }
            }
            slots.add(actor);
            add(actor);

            if ((i + 1) % getCols() == 0) {
                row();
            }
        }
        for (int i = slots.size() - 1; i >= 0; i--) {
            slots.get(i).setZIndex(Integer.MAX_VALUE);

        }

    }

    protected InvItemActor createEmptySlot() {
        return new InvItemActor(null, getCellType(), getUserObject().getClickHandler());
    }

    protected CELL_TYPE getCellType() {
        return CELL_TYPE.INVENTORY;
    }

    protected List<InvItemActor> getSlotActors() {
        return getUserObject().getInventorySlots();
    }

    @Override
    public InventoryTableDataSource getUserObject() {
        return (InventoryTableDataSource) super.getUserObject();
    }


    @Override
    public void setUserObject(Object userObject) {
        if (userObject instanceof ImmutablePair) {
            if (((ImmutablePair) userObject).getKey() instanceof InventoryTableDataSource) {
                setUserObject(((ImmutablePair) userObject).getKey());
                return;
            }
            if (((ImmutablePair) userObject).getValue() instanceof InventoryTableDataSource) {
                setUserObject(((ImmutablePair) userObject).getValue());
                return;
            }
        }
        super.setUserObject(userObject);
    }

    @Override
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }
}
