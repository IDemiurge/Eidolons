package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

public class BaseSlotPanel extends TablePanel {
    protected final int imageSize;

    public BaseSlotPanel(int imageSize) {
        this.imageSize = imageSize;
        left().bottom();
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
