package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ValueTooltip;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class WeaponToolTip extends ValueTooltip {

    public WeaponToolTip() {
        setWidth(400);
    }

    @Override
    public void updateAct(float delta) {
        final List<ValueContainer> valueContainers = (List<ValueContainer>) getUserObject();
        final int size = valueContainers.size();
        int halfSize = size / 2;
        if (size % 2 != 0) {
            halfSize++;
        }

        for (int i = 0; i < halfSize; i++) {
            ValueContainer valueContainer = valueContainers.get(i);
            valueContainer.cropName();
            valueContainer.setNameAlignment(Align.left);
            valueContainer.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            addElement(valueContainer);
            final int i1 = i + halfSize;
            if (i1 < valueContainers.size()) {
                valueContainer = valueContainers.get(i1);
                valueContainer.cropName();
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"));
                addElement(valueContainer);
            }

            row();
        }
    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
        setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));
    }
}
