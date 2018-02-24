package main.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.entity.Entity;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public abstract class ToolTip<T extends Actor> extends TablePanel<T> {

    protected boolean showing;
    private ToolTipManager manager;

    public ToolTip() {

    }

    @Override
    public boolean isTouchable() {
        return false;
    }

    public InputListener getController() {
        return new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                onMouseMoved(event, x, y);
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {

                onMouseEnter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {

                onMouseExit(event, x, y, pointer, toActor);
            }
        };
    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
        showing = true;
    }

    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//        updateRequired = true;
        showing=true;
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
        showing = false;

        if (getEntity() != null)
            if (manager != null) {
            manager.entityHoverOff(getEntity());
        }
    }

    public Entity getEntity() {
        return null;
    }

    public void setManager(ToolTipManager manager) {
        this.manager = manager;
    }

    public ToolTipManager getManager() {
        return manager;
    }
//
//    @Override
//    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//        updateRequired = true;
//        if (view.getTeamColor()!= null ) //TODO quick fix to ignore bf obj
//            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);
//        super.onMouseEnter(event, x, y, pointer, fromActor);
//    }
//    protected void onMouseMoved(InputEvent event, float x, float y) {
//        if (showing) {
//            return;
//        }
//        super.onMouseMoved(event, x, y);
//        if (view.getTeamColor()!= null) //TODO quick fix to ignore bf obj
//            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, view);
//        showing = true;
//    }
//
//
//    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//        super.onMouseExit(event, x, y, pointer, toActor);
//        if (view.getTeamColor()!= null) //TODO quick fix to ignore bf obj
//            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, view);
//    }
}
