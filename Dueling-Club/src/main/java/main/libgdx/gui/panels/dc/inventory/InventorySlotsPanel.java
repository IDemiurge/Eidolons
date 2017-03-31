package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventorySlotsPanel extends TablePanel {

    public InventorySlotsPanel() {

    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
        final List<InventoryValueContainer> inventorySlots = ((InventoryTableDataSource) getUserObject()).getInventorySlots();
        for (int i = 0; i < inventorySlots.size(); i++) {
            ValueContainer valueContainer = inventorySlots.get(i);
            if (valueContainer == null) {
                valueContainer = new ValueContainer(getOrCreateR("UI/empty_pack.jpg"));
            }
            add(valueContainer).expand(0, 0);
            if (i % 7 == 0) {
                row();
            }
        }
    }
}
