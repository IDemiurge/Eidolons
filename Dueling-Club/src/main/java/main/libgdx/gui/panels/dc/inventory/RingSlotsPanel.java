package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class RingSlotsPanel extends TablePanel {

    public RingSlotsPanel() {
        addElement(new ValueContainer(getOrCreateR("UI/components/ring_empty_slot.png")));
        addElement(new ValueContainer(getOrCreateR("UI/components/ring_empty_slot.png")));
        row();
        addElement(new ValueContainer(getOrCreateR("UI/components/ring_empty_slot.png")));
        addElement(new ValueContainer(getOrCreateR("UI/components/ring_empty_slot.png")));
    }

    @Override
    public void clear() {

    }

    @Override
    public void afterUpdateAct(float delta) {

    }
}
