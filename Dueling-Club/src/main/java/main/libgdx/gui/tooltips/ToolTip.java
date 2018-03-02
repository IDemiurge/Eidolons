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
//refactor - why not implement?
    public InputListener getController() {
        return new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                onMouseMoved(event, x, y);
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onTouchDown(event, x, y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onTouchUp(event, x, y);
                super.touchUp(event, x, y, pointer, button);
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

    protected void onTouchUp(InputEvent event, float x, float y) {
    }

    protected void onTouchDown(InputEvent event, float x, float y) {
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
        showing = true;
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (event != null) {
            Actor actor = event.getRelatedActor();
            if (!checkActorExitRemoves(toActor))
                return ;
            if (toActor == this) {
                addListener(new InputListener() {
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        if (toActor != actor)
                            onMouseExit(event, x, y, pointer, toActor);
                        super.exit(event, x, y, pointer, toActor);
                    }
                });
                return;
            }
        }
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
        showing = false;

        if (getEntity() != null)
            if (manager != null) {
                manager.entityHoverOff(getEntity());
            }
    }

    protected boolean checkActorExitRemoves(Actor toActor) {

        return true;
    }

    public Entity getEntity() {
        return null;
    }

    public ToolTipManager getManager() {
        return manager;
    }

    public void setManager(ToolTipManager manager) {
        this.manager = manager;
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
