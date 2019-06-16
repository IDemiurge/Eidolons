package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import eidolons.libgdx.gui.panels.ScrollPaneX;
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
    private final TablePanelX table;
    private List<InvItemActor> slots;
    private ScrollPane scroll;
    private int rows;
    private int cols;

    public InventorySlotsPanel() {
        this(ROWS, COLUMNS);
    }

    public InventorySlotsPanel(int rows, int cols) {
        this.setRows(rows);
        this.setCols(cols);
        setFixedSize(true);
        table = new TablePanelX<>();
        table.setSize(cols * 64 + 20, rows * 64);
        table.defaults().space(0).size(64, 64);

        if (isScrolled()) {
            add(scroll = new ScrollPaneX(table));
            scroll.setForceScroll(false, true);
        } else {
            add(table);
        }
    }

    protected void updateAct() {
        scroll.setBounds(5, 5, getWidth(), getHeight() - 10);
    }

    protected boolean isScrolled() {
        return false;
    }

    @Override
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.afterUpdateAct(delta);
        final List<InvItemActor> inventorySlots = getSlotActors();
        if (slots != null)
            if (new ListMaster<InvItemActor>().compare(inventorySlots, slots))
                return;
        table.clear();
        slots = new ArrayList<>();
        int max = getRows() * getCols();
        if (scroll != null) {
            max = Math.max(max, inventorySlots.size());
        }


        for (int i = 0; i < max; i++) {
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
            table.add(actor);

            if ((i + 1) % getCols() == 0) {
                table.row();
            }
        }
        for (int i = slots.size() - 1; i >= 0; i--) {
            slots.get(i).setZIndex(Integer.MAX_VALUE);

        }
        if (scroll != null)
            scroll.setActor(table);
    }


    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            return null;
        }

        //        if (actor instanceof Image) {
        //            return null;
        //        }
        return actor;
    }

    protected InvItemActor createEmptySlot() {
        return new InvItemActor(null, getCellType(), getUserObject().getClickHandler());
    }

    protected CELL_TYPE getCellType() {
        return CELL_TYPE.INVENTORY;
    }

    protected List<InvItemActor> getSlotActors() {
        List<InvItemActor> inventorySlots = getUserObject().getInventorySlots();
        inventorySlots.removeIf(slot-> slot.getParent()!=null );
        return inventorySlots;
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
