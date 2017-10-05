package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class UnitViewTooltip extends ValueTooltip {

    BaseView view;

    public UnitViewTooltip(BaseView view) {
        this.view = view;
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        updateRequired = true;
        if (view.getTeamColor()!= null || view instanceof OverlayView) //TODO quick fix to ignore bf obj
        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);
        super.onMouseEnter(event, x, y, pointer, fromActor);

    }
    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        super.onMouseMoved(event, x, y);
        if (view.getTeamColor()!= null|| view instanceof OverlayView) //TODO quick fix to ignore bf obj
        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);

    }


    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.onMouseExit(event, x, y, pointer, toActor);
        if (view.getTeamColor()!= null|| view instanceof OverlayView) //TODO quick fix to ignore bf obj
        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, view);
    }
}
