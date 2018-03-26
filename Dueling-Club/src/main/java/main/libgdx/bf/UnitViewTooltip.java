package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.libgdx.GdxMaster;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class UnitViewTooltip extends ValueTooltip {

    BaseView view;

    public UnitViewTooltip(BaseView view) {
        super(view);
        this.view = view;
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        updateRequired = true;
        if (view.isHoverResponsive() || view instanceof OverlayView) //TODO quick fix to ignore bf obj
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);
        super.onMouseEnter(event, x, y, pointer, fromActor);

    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        super.onMouseMoved(event, x, y);
        if (view.isHoverResponsive() || view instanceof OverlayView) //TODO quick fix to ignore bf obj
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);

    }

    @Override
    protected void exited() {
        super.exited();
        if (view.isHoverResponsive() || view instanceof OverlayView) // quick fix to ignore passive UnitViews
            //TODO check if toActor is not just a child of the UnitView, like arrow or emblem!
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, view);
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.onMouseExit(event, x, y, pointer, toActor);
    }

    @Override
    protected boolean checkActorExitRemoves(Actor toActor) {
        if (view instanceof GridUnitView) {
            if (GdxMaster.getAncestors(toActor).contains(((GridUnitView) view).getInitiativeQueueUnitView()))
                return false;
        }
        return super.checkActorExitRemoves(toActor);
    }
}
