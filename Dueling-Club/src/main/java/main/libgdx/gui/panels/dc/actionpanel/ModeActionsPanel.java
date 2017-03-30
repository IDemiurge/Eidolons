package main.libgdx.gui.panels.dc.actionpanel;

import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ModeActionsDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ModeActionsPanel extends BaseSlotPanel {

    public ModeActionsPanel() {
        super(0);
    }

    public ModeActionsPanel(int imageSize) {
        super(imageSize);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final ModeActionsDataSource source = (ModeActionsDataSource) getUserObject();

        final List<ActionValueContainer> sources = source.getModeActions();
        final int tempLimit = Math.min(sources.size(), 6);
        for (int i = 0; i < tempLimit; i++) {
            addValueContainer(sources.get(i), getOrCreateR("UI/EMPTY_LIST_ITEM.jpg"));
        }

        for (int i = tempLimit; i < 6; i++) {
            final ValueContainer container = new ValueContainer(getOrCreateR("UI/EMPTY_LIST_ITEM.jpg"));
            container.overrideImageSize(imageSize, imageSize);
            add(container).left().bottom();
        }
    }
}
