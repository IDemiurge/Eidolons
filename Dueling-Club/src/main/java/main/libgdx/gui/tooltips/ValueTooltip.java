package main.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class ValueTooltip extends ToolTip {

    public ValueTooltip(  String text) {
        this(null, text);
    }
    public ValueTooltip(TextureRegion texture, String text) {
        super();
        setUserObject(new ListMaster<ValueContainer>().getList(new ValueContainer(texture, text)));
    }
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
