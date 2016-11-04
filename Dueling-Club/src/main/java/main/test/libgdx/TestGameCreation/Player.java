package main.test.libgdx.TestGameCreation;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by PC on 01.11.2016.
 */
public class Player extends GameObj {
    public Player(World world) {
        super(world);
        setPosition(getX(),getY());
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2,1);
        createBody(shape, BodyDef.BodyType.KinematicBody);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        body.setTransform(getX(),getY(),0);
        super.draw(batch, parentAlpha);
    }
}
