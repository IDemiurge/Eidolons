package main.libgdx.gui.panels.dc.actionpanel;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.QuickSlotsDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class QuickSlotPanel extends TablePanel {

    public QuickSlotPanel() {
        left().bottom();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final QuickSlotsDataSource source = (QuickSlotsDataSource) getUserObject();

        final List<ActionValueContainer> sources = source.getQuickSlotActions();
        final int tempLimit = Math.min(sources.size(), 6);
        for (int i = 0; i < tempLimit; i++) {
            final ActionValueContainer valueContainer = sources.get(i);
            if (valueContainer != null) {
                add(valueContainer).left().bottom();
            } else {
                add(new ValueContainer(getOrCreateR("UI/empty_pack.jpg"))).left().bottom();
            }
        }
    }
}
