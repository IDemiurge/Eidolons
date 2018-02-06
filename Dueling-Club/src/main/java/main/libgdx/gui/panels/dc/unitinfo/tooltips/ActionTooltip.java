package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.libgdx.gui.NinePatchFactory;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ToolTip;

import java.util.List;

/**
 * Created by JustMe on 9/20/2017.
 */
public class ActionTooltip extends ToolTip {
    private final DC_ActiveObj action;

    public ActionTooltip(DC_ActiveObj el) {
        this.action = el;
    }

    @Override
    public Entity getEntity() {
        return action;
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
        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
    }
}
