package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventorySlotsPanel extends TablePanel {

    public static final int ROWS = 3;
    public static final int COLUMNS = 7;
    public static final int SIZE = ROWS * COLUMNS;

    public InventorySlotsPanel() {

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
                valueContainer = new ValueContainer(getOrCreateR("UI/empty_pack.jpg"));
            }
            add(valueContainer).expand(0, 0);
            if ((i + 1) % COLUMNS == 0) {
                row();
            }
        }
    }
}
