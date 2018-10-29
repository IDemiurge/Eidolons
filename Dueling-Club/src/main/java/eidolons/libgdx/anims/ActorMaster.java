package eidolons.libgdx.anims;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import eidolons.libgdx.anims.actions.AutoFloatAction;
import eidolons.libgdx.anims.actions.FadeInAction;
import eidolons.libgdx.anims.actions.FadeOutAction;
import eidolons.libgdx.anims.actions.RotateByActionLimited;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.gui.generic.GearCluster;
import main.system.auxiliary.ClassMaster;

import java.util.*;

/**
 * Created by JustMe on 1/26/2017.
 */
public class ActorMaster {
    public static final Map<Class, ActionPool> poolMap = new HashMap<>();
    private static final float DEFAULT_FADE_OUT_DURATION = 2;
    private static final float DEFAULT_FADE_IN_DURATION = 0.5f;

    public static void addAfter(Actor actor, Action action) {
        if (getActionsOfClass(actor, AfterAction.class).size() > 0) {
            return;
        }
        AfterAction aa = (AfterAction) getAction(AfterAction.class);
        aa.setAction(action);
        actor.addAction(aa);
        action.setTarget(actor);
        aa.setTarget(actor);
    }

    public static Action getAction(Class<? extends Action> aClass) {
        ActionPool pool = poolMap.get(aClass);
        if (pool == null) {
            pool = new ActionPool(aClass);
            poolMap.put(aClass, pool);
        }
        Action a = pool.obtain();
        a.setPool(pool);
        return a;
    }


    public static void addRemoveAfter(Actor actor) {
        addAfter(actor, new RemoveActorAction());
    }

    public static void addSetVisibleAfter(Actor actor, boolean b) {
        addAfter(actor, new Action() {
            @Override
            public boolean act(float delta) {
                actor.setVisible(b);
                return true;
            }
        });
    }
    public static void addHideAfter(Actor actor) {
        addSetVisibleAfter(actor, false);
    }

    public static MoveByAction getMoveByAction(Vector2 origin, Vector2 destination,
                                               EmitterActor actor, int pixelsPerSecond) {

        MoveByAction action = (MoveByAction) getAction(MoveByAction.class);// new MoveByAction();
        float x = destination.x - origin.x;
        float y = destination.y - origin.y;
        action.setAmount(x, y);
        Float duration = (float) (Math.sqrt(x * x + y * y) / pixelsPerSecond);
        action.setDuration(duration);
        actor.addAction(action);
        action.setTarget(actor);
        return action;
    }


    public static AlphaAction addFadeInOrOut(Actor actor, float duration) {
        float alpha = actor.getColor().a;
        AlphaAction action = (AlphaAction) getAction(AlphaAction.class);// new AlphaAction();
        action.setAlpha(1 - alpha);
        action.setDuration(duration);
        action.setTarget(actor);
        actor.addAction(action);
        return action;
    }

    public static AlphaAction addFadeOutAction(Actor actor, float dur) {
        return addAlphaAction(actor, dur, true);
    }

    public static void addFadeOutAction( Actor actor, float dur, boolean remove) {
        addAlphaAction(actor, dur, true);
        if (remove)
            addRemoveAfter(actor);
    }
    public static AlphaAction addFadeInAction(Actor actor) {
        return addAlphaAction(actor, 0, false);
    }

    public static AlphaAction addFadeInAction(Actor actor, float dur) {
        return addAlphaAction(actor, dur, false);
    }

    public static void addFadeInAndOutAction(Actor actor, float dur, boolean remove) {
        actor.setColor(actor.getColor().r, actor.getColor().g, actor.getColor().b, 0);
        AlphaAction in = (AlphaAction) getAction(FadeInAction.class);
        in.setAlpha(1);
        in.setDuration(dur / 2);
        in.setTarget(actor);
        in.setInterpolation(Interpolation.fade);

        AlphaAction out = (AlphaAction) getAction(FadeOutAction.class);
        out.setAlpha(0);
        out.setDuration(dur / 2);
        out.setTarget(actor);
        out.setInterpolation(Interpolation.fade);
        SequenceAction sequence;
        if (remove) {
//            Actions.sequence(in, out, remove);
            RemoveActorAction r = new RemoveActorAction();
            r.setTarget(actor);
            sequence = Actions.sequence(in, out, r);
        } else {
            sequence = Actions.sequence(in, out);
        }
        actor.addAction(sequence);
        sequence.setTarget(actor);
    }
    public static AlphaAction addAlphaAction(Actor actor, float dur, boolean out) {
        if (dur<=0){
            dur =out? DEFAULT_FADE_OUT_DURATION : DEFAULT_FADE_IN_DURATION;
        }
        return addAlphaAction(actor, dur, out ? 0 : 1);
    }

        public static AlphaAction addAlphaAction(Actor actor, float dur, float alpha) {
            if (alpha==actor.getColor().a) {
                return null;
            }
        AlphaAction action = (AlphaAction) getAction(
         alpha<actor.getColor().a ? FadeOutAction.class : FadeInAction.class );

        action.setAlpha(alpha);
        action.setDuration(dur);
        action.setTarget(actor);
        actor.addAction(action);
        action.setInterpolation(Interpolation.fade);
        return action;
    }

    public static AlphaAction addFadeOutAction(Actor actor) {
        return addFadeOutAction(actor, 0);

    }

    public static void addRotateByAction(Actor actor, float amount) {
        addRotateByAction(actor, actor.getRotation(), actor.getRotation() + amount);
    }

    public static void addRotateByAction(Actor actor, float from, float to) {
        if (!getActionsOfClass(actor, RotateByActionLimited.class).isEmpty()) {
            getActionsOfClass(actor, RotateByActionLimited.class).forEach(action -> {
                if (action instanceof Action)
                    actor.removeAction((Action) action);
            });
            actor.setRotation(from);
        }
        RotateByActionLimited action = (RotateByActionLimited) getAction(RotateByActionLimited.class);

        action.setAmount(to - from);
        if ((action.getAmount()) >= 270)
            action.setAmount(   to - from-360);
        if ((action.getAmount()) <= -270)
            action.setAmount(   to - from+360);

        float speed = 180 * AnimMaster.getInstance().getAnimationSpeedFactor(); //* options
        float duration = Math.abs(from - to) / speed;
        action.setDuration(duration);
        actor.addAction(action);
        action.setTarget(actor);
    }

    public static void addMoveByAction(Actor actor, float x, float y, float v) {
        addMoveToAction(actor, actor.getX()+x, actor.getY()+y, v);
    }
        public static void addMoveToAction(Actor actor, float x, float y, float v) {
        MoveToAction action = (MoveToAction) getAction(MoveToAction.class);// new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(v);
        actor.addAction(action);
        action.setTarget(actor);
    }

    public static MoveToAction getMoveToAction(float x, float y, float v) {
        MoveToAction action = (MoveToAction) getAction(MoveToAction.class);// new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(v);
        return action;
    }

    public static void addScaleAction(Actor actor, float scale, float v) {
        addScaleAction(actor, scale, scale, v);
    }

    public static void addScaleActionCentered(Actor actor, float scaleX,
                                              float scaleY, float v) {
        addScaleAction(actor, scaleX, scaleY, v, true);

    }

    public static void addScaleAction(Actor actor, float scaleX, float scaleY, float v) {
        addScaleAction(actor, scaleX, scaleY, v, false);
    }

    public static void addScaleAction(Actor actor, float scaleX, float scaleY,
                                      float v, boolean centered) {
        ScaleToAction action = (ScaleToAction) getAction(ScaleToAction.class);// new ScaleToAction();
        action.setScale(scaleX, scaleY);
        action.setDuration(v);
       addAction(actor, action);
        if (centered) {
            float x = actor.getX() - (scaleX - actor.getScaleX()) * actor.getWidth() / 2;
            float y = actor.getY() - (scaleY - actor.getScaleY()) * actor.getHeight() / 2;
            addMoveToAction(actor, x, y, v);
        }
    }

    public static AutoFloatAction addFloatAction(GearCluster actor, Float float_, float from, float to, float dur) {
        AutoFloatAction action =new AutoFloatAction ();//(AutoFloatAction) getAction(AutoFloatAction.class);
        action.setFloat_(float_);
        action.setDuration(dur);
        action.setStart(from);
        action.setEnd(to);
        addAction(actor, action);

        return action;
    }

    public static void addAction(Actor actor, Action action) {
        actor.addAction(action);
        action.setTarget(actor);
    }

    public static AlphaAction addFadeInOrOutIfNoActions(Actor actor, float duration) {

        if (actor.getActions().size > 0)
            if (getActionsOfClass(actor, AlphaAction.class).size() > 0) {
                return null;
            }
        return addFadeInOrOut(actor, duration);
    }

    public static List<Object> getActionsOfClass(Actor actor, Class<? extends Action> c) {
        return ClassMaster.getInstances(
         new ArrayList<>(Arrays.asList(actor.getActions().toArray())),
         c);
    }

    public static void addScaleActionIfNoActions(Actor actor, float scaleX,
                                                 float scaleY, float v) {
        if (actor.getActions().size > 0) {
            if (ClassMaster.getInstances(
             new ArrayList<>(Arrays.asList(actor.getActions().toArray())),
             ScaleToAction.class).size() > 0) {
                return;
            }
        }

        addScaleAction(actor, scaleX, scaleY, v);
    }

    public static void addChained(Actor actor, Action... actions) {
        actor.getActions().clear(); //dirty "fix"... refactor add!
        SequenceAction action = new SequenceAction();
        for (Action sub : actions) {
            action.addAction(sub);
        }
        actor.addAction(action);
    }

    public static void addDelayedAction(Actor actor, float delay, Action action) {
        DelayAction delayAction= new DelayAction(delay);
        delayAction.setAction(action);
        addAction(actor, delayAction);
    }

    public static Action getScaleAction(float scale, float v) {
        ScaleToAction action = (ScaleToAction) getAction(ScaleToAction.class);
        action.setScale(scale);
        action.setDuration(v);
        return action;
    }
}
