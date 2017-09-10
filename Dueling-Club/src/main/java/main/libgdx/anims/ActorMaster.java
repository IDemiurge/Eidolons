package main.libgdx.anims;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import main.game.bf.Coordinates;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.bf.GridMaster;
import main.system.auxiliary.ClassMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/26/2017.
 */
public class ActorMaster {
    public static void addAfter(Actor actor, Action action) {
        AfterAction aa = new AfterAction();
        action.setTarget(actor);
        aa.setAction(action);
        actor.addAction(aa);
        aa.setTarget(actor);
    }

    public static void addRemoveAfter(Actor actor) {
        addAfter(actor, new RemoveActorAction());


    }

    public static MoveByAction getMoveByAction(Vector2 origin, Vector2 destination,
                                               EmitterActor actor, int pixelsPerSecond) {

        MoveByAction action = new MoveByAction();
        float x = destination.x - origin.x;
        float y = destination.y - origin.y;
        action.setAmount(x, y);
        Float duration = (float) (Math.sqrt(x * x + y * y) / pixelsPerSecond);
        action.setDuration(duration);
        actor.addAction(action);
        action.setTarget(actor);
        return action;
    }

    public static MoveToAction getMoveToAction(
     Coordinates destination, EmitterActor actor, int pixelsPerSecond) {

        MoveToAction action = new MoveToAction();
        Vector2 v = GridMaster.
         getCenteredPos(destination);
        action.setPosition(v.x, v.y);
        Float duration = (float) (Math.sqrt(v.x * v.x + v.y * v.y) / pixelsPerSecond);
        action.setDuration(
         duration);
        LogMaster.log(1, "MoveTo " +
         v +
         " duration: " + duration);
        actor.addAction(action);
        action.setTarget(actor);
        return action;
    }

    public static AlphaAction addFadeInOrOut(Actor actor, float duration) {
        float alpha = actor.getColor().a;
        AlphaAction action = new AlphaAction();
        action.setAlpha(1 - alpha);
        action.setDuration(duration);
        action.setTarget(actor);
        return action;
    }

    public static AlphaAction getFadeAction(Actor actor) {
        AlphaAction action = new AlphaAction();
        action.setAlpha(0);
        action.setDuration(3);
        action.setTarget(actor);
        return action;
    }

    public static AlphaAction addFadeAction(Actor actor) {
        AlphaAction action = getFadeAction(actor);
        actor.addAction(action);
        return action;
    }

    public static void addRotateByAction(Actor actor, int from, int to) {
        if (!getActionsOfClass(actor, RotateByAction.class).isEmpty()){
            getActionsOfClass(actor, RotateByAction.class).forEach(action->{
            if (action instanceof  Action)
                actor.removeAction(( Action)action);
            });
            actor.setRotation(from);
        }
        RotateByAction action = new RotateByAction();
        action.setAmount(to - from);
        if (Math.abs(action.getAmount()) >= 270)
            action.setAmount((action.getAmount() + 360) % 360);

        float speed = 360; //* options
        float duration = Math.abs(from - to) / speed;
        action.setDuration(duration);
        actor.addAction(action);
        action.setTarget(actor);
    }

    public static void addMoveToAction(Actor actor, float x, float y, float v) {
        MoveToAction action = new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(v);
        actor.addAction(action);
        action.setTarget(actor);
    }

    public static void addScaleAction(Actor actor, float scaleX, float scaleY, float v) {
        ScaleToAction action = new ScaleToAction();
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

    private static List<Object> getActionsOfClass(Actor actor, Class<? extends Action> c) {
        return ClassMaster.getInstances(
         new LinkedList<>(Arrays.asList(actor.getActions().toArray())),
         c);
    }

    public static void addScaleActionIfNoActions(Actor actor, float scaleX,
                                                 float scaleY, float v) {
        if (actor.getActions().size > 0) {
            if (ClassMaster.getInstances(
             new LinkedList<>(Arrays.asList(actor.getActions().toArray())),
             ScaleToAction.class).size() > 0) {
                return;
            }
        }

        addScaleAction(actor, scaleX, scaleY, v);
    }

}
