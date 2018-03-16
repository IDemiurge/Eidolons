package main.libgdx.screens.map.path;

import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import main.libgdx.screens.map.layers.AlphaMap.ALPHA_MAP;
import main.libgdx.screens.map.obj.PartyActor;

/**
 * Created by JustMe on 3/15/2018.
 */
public class SteerableParty extends SteeringAgent {

    PartyActor actor;
    private Sequence<SteeringBehavior> sequence;

    public SteerableParty(PartyActor actor) {
        this.actor = actor;
        position = new Vector2(actor.getX(), actor.getY());
        independentFacing=true;
    }

    public void act(float delta) {
        update(delta);
        actor.setX(position.x);
        actor.setY(position.y);
    }

    public void moveTo(float x, float y, float speed) {

        setMaxLinearSpeed(speed);
        Vector2 destination = new Vector2(x, y);

        if (sequence!=null ){
            sequence.end();
        }
         sequence = PixmapPathBuilder.
         buildPathSequence(this, position, destination, ALPHA_MAP.ROADS);

        sequence.run();

//        int n=10;
//        position = new Vector2(actor.getX(), actor.getY());
//        for (int i = 1; i <= n; i++) {
//            Vector2 p =new Vector2(  position).lerp( destination, 1f /n *(i) );
//            p.add(RandomWizard.getRandomIntBetween(-4,4),
//             RandomWizard.getRandomIntBetween(-4,4));
//            waypoints.add( p);
//        }
//        Path<Vector2, LinePathParam> path = new LinePath(waypoints);
//        setSteeringBehavior(getFollowPath(path));

    }


}
