package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by PC on 02.11.2016.
 */
public class Ground extends Actor {
    Body body;
    BodyDef bDef;
    FixtureDef fDef;
    PolygonShape shape;
    World world;

    public Ground(World world) {
        this.world = world;
        bDef = new BodyDef();
        setBounds(0,0,20,2);
        bDef.position.set(getX(),getY());
        bDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bDef);
        shape = new PolygonShape();
        shape.setAsBox(20,2);
        fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.restitution = 1f;
        body.createFixture(fDef);
    }
}
