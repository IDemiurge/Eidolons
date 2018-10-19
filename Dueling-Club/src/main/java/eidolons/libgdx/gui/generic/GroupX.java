package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import main.system.auxiliary.ClassMaster;

/**
 * Created by JustMe on 3/2/2018.
 */
public class GroupX extends Group {

    boolean autoSize;

    public GroupX(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public GroupX() {
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if (isAutoSize())
        {
        if (getWidth() < actor.getWidth() + actor.getX())
            setWidth(actor.getWidth() + actor.getX());
        if (getHeight() < actor.getHeight() + actor.getY())
            setHeight(actor.getHeight() + actor.getY());
        }
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    protected void initResolutionScaling() {
        float coef = (float) Math.pow(GdxMaster.getFontSizeMod(), 0.3f);
        if (coef < 1)
            return;
        setScale(coef, coef);
        setWidth(getWidth() * getScaleX());
        setHeight(getHeight() * getScaleY());
    }

    public void addAt(float x, float y, Actor actor) {
        super.addActor(actor);
        actor.setPosition(x, y);
    }

    public Group getFirstParentOfClass(Class clazz) {
        Group actor = getParent();
        while (true) {
            actor = actor.getParent();
            if (actor == null) {
                break;
            }
            if (ClassMaster.isInstanceOf(actor, clazz)) {
                return actor;
            }

        }
        return null;
    }

    public Array<Action> getAllChildrenActions() {
        return getAllChildrenActions(null);
    }

    public Array<Action> getAllChildrenActions(Class actionClass) {
        Array<Action> list = new Array<>();
        Group group = this;
        addActions(list, group, actionClass);

        return list;
    }

    public Array<Action> getActionsOfClass(Class actionClass) {
        return getActionsOfClass(actionClass, true);
    }

    public Array<Action> getActionsOfClass(Class actionClass,
                                           boolean recursive) {
        Array<Action> list = new Array<>();
        addActions(list, this, actionClass, recursive);
        return list;
    }

    private void addActions(Array<Action> list, Actor actor, Class actionClass
    ) {
        addActions(list, actor, actionClass, true);

    }

    private void addActions(Array<Action> list, Actor actor, Class actionClass,
                            boolean recursive) {
        if (actor == null || actor.getActions() == null)
            return;
        for (Action sub : actor.getActions()) {
            if (actionClass != null)
                if (!ClassMaster.isInstanceOf(sub, actionClass)) {
                    continue;
                }
            list.add(sub);
        }
        if (recursive)
            if (actor instanceof Group) {

                for (Actor sub : ((Group) actor).getChildren()) {
                    addActions(list, sub, actionClass);
                }
            }
    }

    public void fadeOut() {
        for (Action sub : getActionsOfClass(AlphaAction.class)) {
            removeAction(sub);
        }
        for (Action sub : getActionsOfClass(AfterAction.class)) {
            removeAction(sub);
        }
        setVisible(true);
        ActorMaster.addFadeOutAction(this, getFadeOutDuration());
        ActorMaster.addHideAfter(this);
    }

    protected float getFadeOutDuration() {
        return 0;
    }

    protected float getFadeInDuration() {
        return 0;
    }

    public void fadeIn() {
        setVisible(true);
        getColor().a = 0;
        for (Action sub : getActionsOfClass(AlphaAction.class)) {
            removeAction(sub);
        }
        for (Action sub : getActionsOfClass(AfterAction.class)) {
            removeAction(sub);
        }
        ActorMaster.addFadeInAction(this, getFadeInDuration());
    }

    public void offset(float dX, float dY) {
        setPosition(getX() + dX, getY() + dY);
    }

    public void addActorAtPos(Actor actor, float x, float
     y) {
        addActor(actor);
        actor.setPosition(x, y);
    }

    public void toggleFade() {
        if (isVisible()) {
            fadeOut();
        } else {
            fadeIn();
        }
    }
}
