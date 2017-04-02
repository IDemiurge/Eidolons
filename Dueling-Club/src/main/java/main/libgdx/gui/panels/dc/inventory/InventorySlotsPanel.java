package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.containers.InventoryValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventorySlotsPanel extends TablePanel {

    private static final int ROWS = 3;
    private static final int COLUMNS = 8;
    public static final int SIZE = ROWS*COLUMNS;

    public InventorySlotsPanel() {

    }

    @Override
    public void afterUpdateAct(float delta) {
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
            if ((i+1) % COLUMNS == 0) {
                row();
            }
        }
    }
}
