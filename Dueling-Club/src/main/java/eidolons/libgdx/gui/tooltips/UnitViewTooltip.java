package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.grid.GridViewAnimator;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.bf.grid.cell.OverlayView;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
import eidolons.libgdx.screens.ScreenMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class UnitViewTooltip extends ValueTooltip {

    BaseView view;

    public UnitViewTooltip(BaseView view) {
        super(view);
        this.view = view;
    }

    @Override
    protected Drawable getDefaultBackground() {
        // return NinePatchFactory.getZarkFrameDrawable();
        // return NinePatchFactory.getZarkLargeDrawable();
        return super.getDefaultBackground();
    }

    public BaseView getView() {
        return view;
    }

    public Vector2 getDefaultOffset() {
        return new Vector2(128, 128);
    }

    @Override
    protected boolean isBattlefield() {
        return true;
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        setUpdateRequired(true);
        if (view.isHoverResponsive() || view instanceof OverlayView) //TODO quick fix to ignore bf obj
        {
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);
        }
        if (view.getPortrait().getScreenOverlay() <= 0.01f) {
            ScreenMaster.getDungeonGrid().getGridViewAnimator().animate(view,
                    GridViewAnimator.VIEW_ANIM.screen);
        }
        super.onMouseEnter(event, x, y, pointer, fromActor);

    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        super.onMouseMoved(event, x, y);
        //        if (view.isHoverResponsive() || view instanceof OverlayView) //TODO quick fix to ignore bf obj
        //            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);

    }

    @Override
    protected void exited() {
        super.exited();
        if (view.isHoverResponsive() || view instanceof OverlayView) // quick fix to ignore passive UnitViews
            //(done) TODO check if toActor is not just a child of the UnitView, like arrow or emblem!
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, view);
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.onMouseExit(event, x, y, pointer, toActor);
    }

    @Override
    protected boolean checkActorExitRemoves(Actor toActor) {
        if (view instanceof UnitGridView) {
            if (GdxMaster.getAncestors(toActor).contains(((UnitGridView) view).getInitiativeQueueUnitView()))
                return false;
        }
        if (toActor != null) {
            Group anotherViewThere = GdxMaster.getFirstParentOfClass(toActor, UnitGridView.class);
            if (anotherViewThere instanceof UnitGridView) {
                if (view.getUserObject().getCoordinates().equals(((UnitGridView) anotherViewThere).getUserObject().getCoordinates())) {
                    if (manager != null)
                        manager.entityHoverOff(getEntity());
                    return false;
                }
            }

        }
        return super.checkActorExitRemoves(toActor);
    }
}
