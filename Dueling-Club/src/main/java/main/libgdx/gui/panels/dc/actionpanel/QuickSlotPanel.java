package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class QuickSlotPanel extends BaseSlotPanel {

    public QuickSlotPanel(int imageSize) {
        super(imageSize);
    }

    public QuickSlotPanel() {
        super(0);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final ActiveQuickSlotsDataSource source = (ActiveQuickSlotsDataSource) getUserObject();

        final List<ActionValueContainer> sources = source.getQuickSlotActions();
        final int tempLimit = Math.min(sources.size(), 6);
        for (int i = 0; i < tempLimit; i++) {
            addValueContainer(sources.get(i), getOrCreateR("UI/empty_pack.jpg"));
        }

        for (int i = tempLimit; i < 6; i++) {
            final ValueContainer container = new ValueContainer(getOrCreateR("UI/disabled_pack.jpg"));
            container.overrideImageSize(imageSize, imageSize);
            add(container).left().bottom();
        }
    }

    protected void addValueContainer(ValueContainer valueContainer, TextureRegion emptySlotTexture) {
        if (valueContainer == null) {
            valueContainer = new ValueContainer(emptySlotTexture);
        }
        if (imageSize > 0) {
            valueContainer.overrideImageSize(imageSize, imageSize);
        }
        add(valueContainer).left().bottom();
    }

}
