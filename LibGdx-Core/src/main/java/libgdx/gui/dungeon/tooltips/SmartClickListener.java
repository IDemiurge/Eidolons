package libgdx.gui.dungeon.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import libgdx.GdxMaster;
import libgdx.gui.dungeon.panels.headquarters.HqPanel;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.stage.ConfirmationPanel;

/**
 * Created by JustMe on 4/10/2018.
 */
public class SmartClickListener extends ClickListener {
    protected Actor actor;
    protected boolean showing;

    public SmartClickListener(Actor actor) {
        this.actor = actor;
        setTapCountInterval(0.6f);
    }

    @Override
    public boolean handle(Event e) {
        if (isBattlefield()) {
//            if (LordPanel.getInstance() != null)
//                return true;
            if (HqPanel.getActiveInstance() != null)
                return true;
            if (DungeonScreen.getInstance() == null)
                return true;
            if (!DungeonScreen.getInstance().isLoaded())
                return true;
            if (DungeonScreen.getInstance().getSelectionPanel() != null) {
                if (DungeonScreen.getInstance().getSelectionPanel().isVisible()) {
                    return true;
                }
            }
        }
        if (ConfirmationPanel.getInstance().isVisible())
            return true;


        return super.handle(e);
    }

    protected boolean isBattlefield() {
        return false;
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        onMouseMoved(event, x, y);
        return !showing;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (getTapCount() > 1)
            onDoubleClick(event, x, y);

        super.clicked(event, x, y);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
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

    protected void onTouchUp(InputEvent event, float x, float y) {
    }

    protected void onTouchDown(InputEvent event, float x, float y) {
    }

    protected void onDoubleClick(InputEvent event, float x, float y) {
    }

    protected void onMouseMoved(InputEvent event, float x, float y) {

        if (showing) {
            return;
        }
        entered();
    }

    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (!showing)
            entered();
    }

    protected void entered() {
        showing = true;
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (event != null) {
            if (!checkActorExitRemoves(toActor))
                return;
        }
        if (showing)
            exited();

    }

    protected void exited() {
        showing = false;
    }

    protected boolean checkActorExitRemoves(Actor toActor) {
        if (actor == null)
            return true;
        if (toActor == actor)
            return false;
        return !GdxMaster.getAncestors(toActor).contains(actor);
    }
}
