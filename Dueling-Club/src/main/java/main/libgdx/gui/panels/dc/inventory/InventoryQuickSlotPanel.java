package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventoryQuickSlotPanel extends TablePanel {


    public InventoryQuickSlotPanel() {
        for (int i = 0; i < 8; i++) {
            addElement(new ValueContainer(getOrCreateR("UI/empty_pack.jpg")))
                    .fill(0, 1).expand(0, 1).center();
        }
    }

    @Override
    public void clear() {

    }
}
