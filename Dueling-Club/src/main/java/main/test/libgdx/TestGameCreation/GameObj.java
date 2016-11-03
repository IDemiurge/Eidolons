package main.test.libgdx.TestGameCreation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by PC on 01.11.2016.
 */
public class GameObj extends Actor {
    protected Sprite sprite;
    protected Body body;
    private World world;
    public GameObj(World world){
        this.world = world;
    }

    protected void createBody(Shape shape , BodyDef.BodyType type){
        BodyDef bDef = new BodyDef();
        bDef.position.set(new Vector2(getX(),getY()));
        bDef.type = type;
        body = world.createBody(bDef);
        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.restitution = 1;
        fDef.density = 5;
        body.createFixture(fDef);
    }
}
