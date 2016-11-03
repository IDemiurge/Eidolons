package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.system.ai.logic.actions.Action;

/**
 * Created by PC on 02.11.2016.
 */
public class Fon extends Actor {
    Body body;
    BodyDef bDef;
    FixtureDef fDef;
    ChainShape shape;
    World world;
    public Fon(World world) {
        this.world = world;
        bDef = new BodyDef();
        setBounds(0,0,15,25);
        bDef.position.set(getX(),getY());
        bDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bDef);
        shape = new ChainShape();
        shape.createChain(new Vector2[]{new Vector2(0,25),new Vector2(0,0),new Vector2(15,0),new Vector2(15,25)});
        fDef = new FixtureDef();
        fDef.shape = shape;
        body.createFixture(fDef);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
