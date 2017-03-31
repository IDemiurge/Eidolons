package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public class ValueTooltip extends ToolTip {

    public ValueTooltip() {
        super();
    }

    @Override
    public void updateAct(float delta) {
        clear();
        List<ValueContainer> values = (List<ValueContainer>) getUserObject();

        values.forEach(el -> {
            addElement(el);
            row();
        });
    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
        setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));
    }
}
