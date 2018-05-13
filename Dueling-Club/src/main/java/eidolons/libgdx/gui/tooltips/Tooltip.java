package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.stage.StageX;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

public abstract class Tooltip<T extends Actor> extends TablePanel<T> {

    protected boolean showing;
    protected ToolTipManager manager;
    protected Actor actor;

    public Tooltip(Actor actor) {
        this.actor = actor;
    }

    public Tooltip() {

    }

    @Override
    public boolean isTouchable() {
        return false;
    }

    //refactor - why not implement?
    public InputListener getController() {
        return new  ClickListener() {

            public boolean handle(Event e) {
                if (isBattlefield()) {
                    if (HqPanel.getActiveInstance() != null) {
                        return true;
                    }
                }

                return super.handle(e);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                onMouseMoved(event, x, y);
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getTapCount() > 1) {
                    onDoubleTouchDown(event, x, y);
                } else
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

    protected boolean isBattlefield() {
        return false;
    }

    protected void onTouchUp(InputEvent event, float x, float y) {
    }

    protected void onTouchDown(InputEvent event, float x, float y) {
    }

    protected void onDoubleTouchDown(InputEvent event, float x, float y) {
    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (checkGuiStageBlocking()) {
            onMouseExit(event, x, y, -1, null);
            return;
        }

        if (showing) {
            return;
        }
        entered();
    }

    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (checkGuiStageBlocking()) {
            return;
        }
        entered();
    }

    protected void entered() {
        showing = true;
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (!showing)
            return;
        if (event != null) {
            if (!checkActorExitRemoves(toActor))
                return;
        }
        if (checkGuiStageBlocking())
            return;

        exited();

        if (getEntity() != null)
            if (manager != null) {
                manager.entityHoverOff(getEntity());
            }
    }

    protected void exited() {
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
        showing = false;
    }

    protected boolean checkActorExitRemoves(Actor toActor) {
        if (actor == null)
            return true;
        if (toActor == actor)
            return false;
        return !GdxMaster.getAncestors(toActor).contains(actor);
    }


    protected boolean checkGuiStageBlocking() {
        if (this.actor != null && getManager() != null)
            if (this.actor.getStage() != getManager().getStage()) {
                Actor actor = ((StageX) getManager().getStage()).getMouseOverActor();
                if (actor != null) {
                    if (checkUiActorBlocks(actor))
                        return true;
                }
            }
        return false;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    protected boolean checkUiActorBlocks(Actor actor) {
        List<Group> list = GdxMaster.getAncestors(actor);
        if (actor instanceof Group) {
            list.add((Group) actor);
        }
        for (Group sub : list) {
            if (sub instanceof InitiativePanel)
                return false;
        }
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

    public void addTo(Actor container) {
        actor = container;
        container.addListener(getController());
    }
}
