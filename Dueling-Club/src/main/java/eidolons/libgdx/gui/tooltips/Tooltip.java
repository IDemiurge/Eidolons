package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.atb.AtbPanel;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.libgdx.stage.StageX;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

public abstract class Tooltip<T extends Actor> extends TablePanelX<T> {

    protected boolean showing;
    protected ToolTipManager manager;
    protected Actor actor;
    private boolean mouseHasMoved;

    public Tooltip(Actor actor) {
        this.actor = actor;
    }

    public Tooltip() {

    }

    @Override
    public boolean isTouchable() {
        return false;
    }

    public Actor getActor() {
        return actor;
    }

    //refactor - why not implement?
    public InputListener getController() {
        return new ClickListener() {

            public boolean handle(Event e) {
                if (isBattlefield()) {
                    if (DungeonScreen.getInstance().isBlocked())
                        return true;
                } else {
                    if (ConfirmationPanel.getInstance().isVisible())
                        return true;
                }
                if (actor != null)
                    if (!actor.isVisible() || actor.getColor().a <= 0)
                        return true;
                return super.handle(e);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                onMouseMoved(event, x, y);
                return true;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() > 1) {
                    onDoubleClick(event, x, y);
                } else super.clicked(event, x, y);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getTapCount() > 1) {
                    onDoubleClick(event, x, y);
                } else
                    onTouchDown(event, x, y);
                return super.touchDown(event, x, y, pointer, button);
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

    @Override
    public Actor hit(float x, float y, boolean touchable) {
            if (!isTouchable())
                return null;
        return super.hit(x, y, touchable);
    }

    protected boolean isBattlefield() {
        return false;
    }

    protected void onTouchUp(InputEvent event, float x, float y) {
    }

    protected void onTouchDown(InputEvent event, float x, float y) {
    }

    protected void onDoubleClick(InputEvent event, float x, float y) {
    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (checkGuiStageBlocking()) {
            onMouseExit(event, x, y, -1, null);
            return;
        }

        if (showing) {
            mouseHasMoved =true;
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
        mouseHasMoved =false;
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
        mouseHasMoved =false;
    }

    public boolean isMouseHasMoved() {
        return mouseHasMoved;
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
                    return checkUiActorBlocks(actor);
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
            if (sub instanceof AtbPanel)
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

    public Vector2 getDefaultOffset() {
        return null;
    }

}
