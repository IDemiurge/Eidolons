package libgdx.gui.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.Blended;
import libgdx.gui.LabelX;
import libgdx.shaders.ShaderDrawer;
import main.system.auxiliary.ClassMaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 3/2/2018.
 */
public class GroupX extends Group {

    static private final Vector2 tmp_ = new Vector2();
    boolean autoSize;

    public static final boolean TOTAL_DEBUG = false;
    public static final LabelX xXx = new LabelX(StyleHolder.getAVQLabelStyle(17)){
        @Override
        protected boolean isTotalDebug() {
            return false;
        }
    };
    public static final Map<Vector2, Map<Object, Integer>> offsets = new HashMap<>();

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (TOTAL_DEBUG) {
            drawDebug(batch, this);
        }
    }

    public static void drawDebug(Batch batch, Actor actor) {
        Vector2 pos = GdxMaster.getAbsolutePosition(actor);
        xXx.setText(actor.getName()==null ? actor.getClass().getSimpleName() : actor.getName() + " " + pos.x + " " + pos.y);
        Map<Object, Integer> offsetMap = offsets.get(pos);
        if (offsetMap == null){
            offsets.put(pos, new HashMap<>());
            xXx.setPosition(pos.x, pos.y);
        }
        else{
            Integer offset = offsetMap.get(actor);
            if (offset == null) {
                offset = offsetMap.size() * 20;
                offsetMap.put(actor, offset);
            }
            xXx.setPosition(pos.x, pos.y + offset);
        }
        //add runnable with pos?
        xXx.draw(batch, 1f);
    }

    public void addActor(String name, Actor actor) {
        super.addActor(actor);
        actor.setName(name);
    }

    public GroupX(boolean autoSize) {
        this();
        this.autoSize = autoSize;
    }

    public GroupX() {
        setTransform(false);
    }


    @Override
    public void setRotation(float degrees) {
        if (getRotation() == degrees) {
            return;
        }
        super.setRotation(degrees);
        setTransform(degrees != 0);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        setTransform(getScaleX() != 1 || getScaleY() != 1);
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
    }

    @Override
    protected void rotationChanged() {
        super.rotationChanged();
        setTransform(getRotation() != 0);
    }

    public void addActor(Actor actor, int align) {
        addActor(actor);
        initPos(actor, align);

    }

    protected Actor hit(float x, float y, boolean touchable, Collection<? extends Actor> children) {
        if (touchable && getTouchable() == Touchable.disabled) return null;
        Vector2 point = tmp_;
        Actor[] childrenArray = children.toArray(new Actor[0]);
        for (int i = children.size() - 1; i >= 0; i--) {
            Actor child = childrenArray[i];
            if (!child.isVisible()) continue;
            child.parentToLocalCoordinates(point.set(x, y));
            Actor hit = child.hit(point.x, point.y, touchable);
            if (hit != null) return hit;
        }
        return null;
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
        ActionMasterGdx.addFadeOutAction(this, getFadeOutDuration());
        if (isHideWhenFade())
            ActionMasterGdx.addHideAfter(this);
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
        ActionMasterGdx.addFadeInAction(this, getFadeInDuration());
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
                child.setPosition(x + getX(), child.getY() + getY());
                ((GroupX) child).drawScreen(batch, screen);
                child.setPosition(x, y);
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
        ActionMasterGdx.addAlphaAction(this, dur, alpha);
    }
}
