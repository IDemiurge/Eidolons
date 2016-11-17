package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.data.filesys.PathFinder;


/**
 * Created by PC on 02.11.2016.
 */
public class Fon extends Actor {
    Body body;
    BodyDef bDef;
    FixtureDef fDef;
    ChainShape shape;
    World world;
    Sprite sprite;
    float bound_X = 20;
    float bound_Y = 15;
    public Fon(World world) {
        this.world = world;
//        bound_X = myUnderstandingField.View_Width;
//        bound_Y = myUnderstandingField.View_Height+myUnderstandingField.View_Height/4;
        bDef = new BodyDef();
        setBounds(0,0,myUnderstandingField.View_Width,myUnderstandingField.View_Height+myUnderstandingField.View_Height/4);
        bDef.position.set(getX(),getY());
        bDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bDef);
        shape = new ChainShape();
        shape.createChain(new Vector2[]{new Vector2(0,bound_Y),new Vector2(0,0),new Vector2(bound_X,0),new Vector2(bound_X,bound_Y)});
        fDef = new FixtureDef();
        fDef.shape = shape;
        body.createFixture(fDef);
        PathFinder.init();
        String q = PathFinder.getImagePath();
        sprite = new Sprite(new Texture(q + "big\\New\\ravenwood.jpg"));
        sprite.setBounds(0,0,myUnderstandingField.View_Width,myUnderstandingField.View_Height);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
//        super.draw(batch, parentAlpha);
    }
}
