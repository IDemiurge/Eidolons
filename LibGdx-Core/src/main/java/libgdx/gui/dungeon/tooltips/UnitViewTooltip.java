package libgdx.gui.dungeon.tooltips;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.content.consts.VisualEnums;
import libgdx.GdxMaster;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.OverlayView;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.screens.handlers.ScreenMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class UnitViewTooltip extends ValueTooltip {

    public UnitViewTooltip(Actor view) {
        super(view);
    }

    @Override
    protected Drawable getDefaultBackground() {
        // return NinePatchFactory.getZarkFrameDrawable();
        // return NinePatchFactory.getZarkLargeDrawable();
        return super.getDefaultBackground();
    }

    public BaseView getView() {
        return (BaseView) actor;
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
        if (!(actor instanceof BaseView)) {
        return ;
        }
        if (getView().isHoverResponsive() || getView()instanceof OverlayView) //TODO quick fix to ignore bf obj
        {
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, getView());
        }
        if (getView().getPortrait().getScreenOverlay() <= 0.01f) {
            ScreenMaster.getGrid().getGridManager().getAnimHandler().animate(getView(),
                    VisualEnums.VIEW_ANIM.screen);
        }
        super.onMouseEnter(event, x, y, pointer, fromActor);

    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        super.onMouseMoved(event, x, y);
        //        if (view.isHoverResponsive() || getView()instanceof OverlayView) //TODO quick fix to ignore bf obj
        //            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);

    }

    @Override
    protected void exited() {
        super.exited();
        if (getView().isHoverResponsive() || getView()instanceof OverlayView) // quick fix to ignore passive UnitViews
            //(done) TODO check if toActor is not just a child of the UnitView, like arrow or emblem!
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, getView());
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.onMouseExit(event, x, y, pointer, toActor);
    }

    @Override
    protected boolean checkActorExitRemoves(Actor toActor) {
        if (getView()instanceof UnitGridView) {
            if (GdxMaster.getAncestors(toActor).contains(((UnitGridView) getView()).getInitiativeQueueUnitView()))
                return false;
        }
        if (toActor != null) {
            Group anotherViewThere = GdxMaster.getFirstParentOfClass(toActor, UnitGridView.class);
            if (anotherViewThere instanceof UnitGridView) {
                if (getView().getUserObject().getCoordinates().equals(((UnitGridView) anotherViewThere).getUserObject().getCoordinates())) {
                    if (manager != null)
                        manager.entityHoverOff(getEntity());
                    return false;
                }
            }

        }
        return super.checkActorExitRemoves(toActor);
    }
}
