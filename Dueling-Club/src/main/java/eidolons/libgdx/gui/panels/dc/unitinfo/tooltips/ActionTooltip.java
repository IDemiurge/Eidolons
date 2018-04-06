package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.screens.DungeonScreen;
import main.entity.Entity;

import java.util.List;

/**
 * Created by JustMe on 9/20/2017.
 */
public class ActionTooltip extends Tooltip {
    private final DC_ActiveObj action;
    private boolean radial;

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
    protected boolean checkGuiStageBlocking() {
        if (radial)
            if (!DungeonScreen.getInstance().getGuiStage().getRadial().isReady())
                return true;
        return super.checkGuiStageBlocking();
    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
    }

    public boolean isRadial() {
        return radial;
    }

    public void setRadial(boolean radial) {
        this.radial = radial;
    }
}
