package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreate;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class WeaponToolTip extends ValueTooltip {

    @Override
    public void updateAct() {
        clear();

        final List<ValueContainer> valueContainers = getUserObject().get();

        final int size = valueContainers.size();
        int halfSize = size / 2;
        if (size % 2 != 0) {
            halfSize++;
        }

        addRow(valueContainers.subList(0, halfSize - 1));
        addRow(valueContainers.subList(halfSize - 1, valueContainers.size()));

    }

    public void addRow(List<ValueContainer> list) {
        inner.addCol();
        for (ValueContainer valueContainer : list) {
            valueContainer.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            inner.addElement(valueContainer);
        }
    }

    @Override
    public void postUpdateAct() {
        inner.pad(20);

        NinePatchDrawable ninePatchDrawable =
                new NinePatchDrawable(new NinePatch(getOrCreate("UI/components/tooltip_background.9.png")));
        setBackground(ninePatchDrawable);
    }
}
