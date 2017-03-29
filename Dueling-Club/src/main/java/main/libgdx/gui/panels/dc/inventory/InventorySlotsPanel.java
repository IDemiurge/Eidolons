package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventorySlotsPanel extends TablePanel {

    public InventorySlotsPanel() {

        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 7; row++) {
                add(new ValueContainer(getOrCreateR("UI/empty_pack.jpg")))
                        .expand(0, 0);
            }
            row();
        }
    }

    @Override
    public void clear() {

    }
}
