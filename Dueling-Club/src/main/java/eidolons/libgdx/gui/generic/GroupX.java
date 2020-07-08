package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.sprite.Blended;
import eidolons.libgdx.shaders.ShaderDrawer;
import main.system.auxiliary.ClassMaster;

/**
 * Created by JustMe on 3/2/2018.
 */
public class GroupX extends Group {

    boolean autoSize;

    public GroupX(boolean autoSize) {
        this();
        this.autoSize = autoSize;
    }

    public GroupX() {
        setTransform(false);
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        setTransform(degrees != 0);
        for (Group ancestor : GdxMaster.getAncestors(this)) {
            ancestor.setTransform(degrees != 0);
        }
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
    }

    @Override
    protected void rotationChanged() {
        super.rotationChanged();
        setTransform(getRotation() != 0);
        for (Group ancestor : GdxMaster.getAncestors(this)) {
            ancestor.setTransform(getRotation() != 0);
        }
    }

    public void addActor(Actor actor, int align) {
        addActor(actor);
        initPos(actor, align);

    }

    public void initPos(Actor actor, int align) {
        if ((align & Align.right) != 0) {
            GdxMaster.right(actor);
        } else if ((align & Align.left) != 0) {
            //0
        } else {
            actor.setX(GdxMaster.centerWidth(actor));
        }
        if ((align & Align.top) != 0) {
            GdxMaster.top(actor);
        } else if ((align & Align.bottom) != 0) {
            //0
        } else {
            actor.setY(GdxMaster.centerHeight(actor));
        }
    }

    public void pack() {
        float maxX = 0;
        float maxY = 0;
        float minX = 0;
        float minY = 0;
        for (Actor child : getChildren()) {
            if (maxX <= child.getX() + child.getWidth())
                maxX = child.getX() + child.getWidth();
            if (maxY <= child.getY() + child.getHeight())
                maxY = child.getY() + child.getHeight();

            if (minX >= child.getX())
                minX = child.getX();
            if (minY >= child.getY())
                minY = child.getY();
        }

        setSize(maxX - minX, maxY - minY);
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if (isAutoSize()) {
            if (getWidth() < actor.getWidth() + actor.getX())
                setWidth(actor.getWidth() + actor.getX());
            if (getHeight() < actor.getHeight() + actor.getY())
                setHeight(actor.getHeight() + actor.getY());
        }
    }

    protected void addBg(Actor actor) {
        addActor(actor);
        setSize(actor.getWidth(), actor.getHeight());
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    protected void initResolutionScaling() {
        float coef = (float) Math.pow(GdxMaster.getFontSizeMod(), 0.3f);
        if (coef < 1.2f)
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
        ActionMaster.addFadeOutAction(this, getFadeOutDuration());
        if (isHideWhenFade())
            ActionMaster.addHideAfter(this);
    }

    protected boolean isHideWhenFade() {
        return true;
    }

    protected float getFadeOutDuration() {
        return 2;
    }

    protected float getFadeInDuration() {
        return 2;
    }

    public void fadeIn() {
        setVisible(true);
        //        getColor().a = 0;
        for (Action sub : getActionsOfClass(AlphaAction.class)) {
            removeAction(sub);
        }
        for (Action sub : getActionsOfClass(AfterAction.class)) {
            removeAction(sub);
        }
        ActionMaster.addFadeInAction(this, getFadeInDuration());
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

    public void drawScreen(Batch batch, boolean screen) {
        boolean draw = false;
        for (Actor child : getChildren()) {
            //set visible?
            if (child instanceof GroupX && !(isBlendContainer())) {
                float x = child.getX();
                float y = child.getY();
                child.setPosition(x +getX(), child.getY()+getY());
                ((GroupX) child).drawScreen(batch, screen);
                child.setPosition(x , y );
            } else {
                draw = true;
                if (child instanceof Blended) {
                    child.setVisible(((Blended) child).getBlending() != null == screen);
                } else {
                    child.setVisible(!screen);
                }
            }
        }
        if (draw)
            draw(batch, ShaderDrawer.SUPER_DRAW);
    }

    protected boolean isBlendContainer() {
        return false;
    }

    public void setStage_(Stage s) {
        setStage(s);
    }

    public void fadeTo(float alpha, float dur) {
        ActionMaster.addAlphaAction(this, dur, alpha);
    }
}
