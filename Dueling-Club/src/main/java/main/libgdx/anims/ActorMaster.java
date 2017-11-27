package main.libgdx.anims;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import main.libgdx.anims.actions.RotateByActionLimited;
import main.libgdx.anims.particles.EmitterActor;
import main.system.auxiliary.ClassMaster;

import java.util.*;

/**
 * Created by JustMe on 1/26/2017.
 */
public class ActorMaster {
    public static final Map<Class, ActionPool> poolMap = new HashMap<>();

    public static void addAfter(Actor actor, Action action) {
        if (getActionsOfClass(actor, AfterAction.class).size()>0){
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

    public static void addHideAfter(Actor actor) {
        addAfter(actor, new Action() {
            @Override
            public boolean act(float delta) {
                actor.setVisible(false);
                return true;
            }
        });
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
        return addFadeAction(actor, dur, true);
    }

    public static AlphaAction addFadeInAction(Actor actor, float dur) {
        return addFadeAction(actor, dur, false);
    }

    public static AlphaAction addFadeAction(Actor actor, float dur, boolean out) {
        AlphaAction action = (AlphaAction) getAction(AlphaAction.class);// new AlphaAction();
        action.setAlpha(out ? 0 : 1);
        action.setDuration(dur);
        action.setTarget(actor);
        actor.addAction(action);
        return action;
    }

    public static AlphaAction addFadeOutAction(Actor actor) {
        return addFadeOutAction(actor, 3);

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
        RotateByActionLimited action = new RotateByActionLimited();// (RotateByActionLimited) getAction(RotateByActionLimited.class);// new RotateByAction();
        action.setAmount(to - from);
        if (Math.abs(action.getAmount()) >= 270)
            action.setAmount((action.getAmount() + 360) % 360);

        main.system.auxiliary.log.LogMaster.log(1,from+ "from; to: " +to + "; amount = " + action.getAmount());
        float speed = 360*AnimMaster.getInstance().getAnimationSpeedFactor(); //* options
        float duration = Math.abs(from - to) / speed;
        action.setDuration(duration);
        actor.addAction(action);
        action.setTarget(actor);
    }

    public static void addMoveToAction(Actor actor, float x, float y, float v) {
        MoveToAction action = (MoveToAction) getAction(MoveToAction.class);// new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(v);
        actor.addAction(action);
        action.setTarget(actor);
    }
    public static MoveToAction getMoveToAction( float x, float y, float v) {
        MoveToAction action = (MoveToAction) getAction(MoveToAction.class);// new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(v);
        return action;
    }

    public static void addScaleAction(Actor actor, float scaleX, float scaleY, float v) {
        ScaleToAction action = (ScaleToAction) getAction(ScaleToAction.class);// new ScaleToAction();
        action.setScale(scaleX, scaleY);
        action.setDuration(v);
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
}
