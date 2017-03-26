package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;
import java.util.function.Supplier;

import static main.libgdx.gui.dialog.ToolTipBackgroundHolder.*;

public class ValueTooltip extends ToolTip {

    public ValueTooltip() {
        super();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        List<ValueContainer> values = ((Supplier<List<ValueContainer>>) getUserObject()).get();

        ValueContainer recordOption = values.get(0);
        if (values.size() == 1) {
            addToolTipOffset(getSingle(), recordOption);
            addElement(recordOption);
        } else {
            addToolTipOffset(ToolTipBackgroundHolder.getTop(), recordOption);
            addElement(recordOption);

            if (values.size() > 2) {
                for (int i = 1; i < values.size() - 1; i++) {
                    recordOption = values.get(i);
                    addToolTipOffset(getMid(), recordOption);
                    addElement(recordOption);
                }
            }

            recordOption = values.get(values.size() - 1);
            addToolTipOffset(getBot(), recordOption);
            addElement(recordOption);
        }
    }

    private void addToolTipOffset(TextureRegion texture, ValueContainer recordOption) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        recordOption.setBackground(drawable);
        recordOption.setHeight(45);
    }
}
