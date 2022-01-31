package libgdx.gui.dungeon.panels.dc.unitinfo.tooltips;

import eidolons.entity.active.ActiveObj;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.tooltips.Tooltip;
import libgdx.screens.dungeon.DungeonScreen;
import main.entity.Entity;

import java.util.List;

/**
 * Created by JustMe on 9/20/2017.
 */
public class ActionTooltip extends Tooltip {
    private final ActiveObj action;
    private boolean radial;

    public ActionTooltip(ActiveObj el) {
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
    }

    public boolean isRadial() {
        return radial;
    }

    public void setRadial(boolean radial) {
        this.radial = radial;
    }
}
