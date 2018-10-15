package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import eidolons.libgdx.texture.Images;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class InventorySlotsPanel extends TablePanelX {

    public static final int ROWS = 3;
    public static final int COLUMNS = 8;
    public static final int SIZE = ROWS * COLUMNS;
    int rows;
    int cols;
    private List<ValueContainer> items = new ArrayList<>();

    public InventorySlotsPanel() {
        this(ROWS, COLUMNS);
    }

    public InventorySlotsPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setFixedSize(true);
        setSize(COLUMNS * 64, ROWS * 64);
        defaults().space(0);
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
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        clear();
        super.afterUpdateAct(delta);
        final List<InventoryValueContainer> inventorySlots =
         ((InventoryTableDataSource) getUserObject()).getInventorySlots();
        for (int i = 0; i < rows * cols; i++) {
            ValueContainer valueContainer = null;
            if (inventorySlots.size() > i) {
                valueContainer = inventorySlots.get(i);
            }
            if (valueContainer == null) {
                valueContainer = new ValueContainer(getOrCreateR(Images.EMPTY_LIST_ITEM));
            } else {
                items.add(valueContainer);
            }
            add(valueContainer).size(64, 64);
            //             .expand(0, 0) ;
            if ((i + 1) % cols == 0) {
                row();
            }
        }
    }
}
