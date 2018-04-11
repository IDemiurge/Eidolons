package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxMaster;

/**
 * Created by JustMe on 4/10/2018.
 */
public class SmartClickListener extends ClickListener {
    Actor actor;
    boolean showing;

    public SmartClickListener(Actor actor) {
        this.actor = actor;
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

    protected void onTouchUp(InputEvent event, float x, float y) {
    }

    protected void onTouchDown(InputEvent event, float x, float y) {
    }

    protected void onDoubleTouchDown(InputEvent event, float x, float y) {
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
