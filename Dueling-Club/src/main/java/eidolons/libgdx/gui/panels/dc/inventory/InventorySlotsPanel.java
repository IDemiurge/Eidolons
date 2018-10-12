package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import eidolons.libgdx.texture.Images;

import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class InventorySlotsPanel extends TablePanel {

    public static final int ROWS = 3;
    public static final int COLUMNS = 8;
    public static final int SIZE = ROWS * COLUMNS;
    int rows;
    int cols;

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
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        clear();
        super.afterUpdateAct(delta);
        final List<InventoryValueContainer> inventorySlots =
         ((InventoryTableDataSource) getUserObject()).getInventorySlots();
        for (int i = 0; i < SIZE; i++) {
            ValueContainer valueContainer = inventorySlots.get(i);
            if (valueContainer == null) {
                valueContainer = new ValueContainer(getOrCreateR(Images.EMPTY_ITEM));
            }
            add(valueContainer).size(64,64);
//             .expand(0, 0) ;
            if ((i + 1) % cols == 0) {
                row();
            }
        }
    }
}
