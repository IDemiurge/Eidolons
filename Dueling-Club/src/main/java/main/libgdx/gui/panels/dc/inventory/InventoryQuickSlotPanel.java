package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventoryQuickSlotPanel extends TablePanel {


    public InventoryQuickSlotPanel() {
        for (int i = 0; i < 6; i++) {
            addElement(new ValueContainer(getOrCreateR("UI/empty_pack.jpg")))
                    .fill(1, 0).expand(1, 0).center();
            row();
        }
    }

    @Override
    public void clear() {

    }
}
