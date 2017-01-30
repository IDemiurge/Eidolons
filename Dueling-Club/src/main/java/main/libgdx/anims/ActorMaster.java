package main.libgdx.anims;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.EmitterActor;

/**
 * Created by JustMe on 1/26/2017.
 */
public class ActorMaster {
    public static void addRemoveAfter(Actor actor) {
        AfterAction aa = new AfterAction();
        RemoveActorAction remove = new RemoveActorAction();
        remove.setTarget(actor);

        aa.setAction(remove);
        actor.addAction(aa);
        aa.setTarget(actor);

    }
    public static MoveByAction getMoveByAction(Vector2 origin, Vector2 destination,
                                               EmitterActor actor, int pixelsPerSecond) {

        MoveByAction action = new MoveByAction();
        float x = destination.x-origin.x;
        float y = destination.y-origin.y;
        action.setAmount(x, y);
        Float duration = (float) (Math.sqrt( x *  x + y * y) / pixelsPerSecond);
        action.setDuration(duration);
        actor.addAction(action);
        action.setTarget(actor);
        return action;
    }
    public static  MoveToAction getMoveToAction(
     Coordinates destination, EmitterActor actor, int pixelsPerSecond) {

        MoveToAction action = new MoveToAction();
        Vector2 v = GameScreen.getInstance().getGridPanel().
         getVectorForCoordinateWithOffset(destination);
        action.setPosition(v.x,v.y);
        Float duration = (float) (Math.sqrt(v.x * v.x + v.y * v.y) / pixelsPerSecond);
        action.setDuration(
         duration);
main.system.auxiliary.LogMaster.log(1,"MoveTo " +
 v +
 " duration: " +duration);
        actor.addAction(action);
        action.setTarget(actor);
        return action;
    }

    public static AlphaAction addFadeAction(Actor actor) {
        AlphaAction action = new AlphaAction();
        action.setAlpha(0);
        action.setDuration(3);
        actor.addAction(action);
        action.setTarget(actor   );
        return action;
    }

}
