package main.test.libgdx.prototype;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by PC on 08.11.2016.
 */
public class PlayerActor extends Actor {
    private static World world;
    private static Body body;
    private static float size = 50f;
    public PlayerActor(World world) {
    this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size,size);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 1;
        fixtureDef.restitution = 0;
        body.createFixture(fixtureDef);
//        setBounds(body.getPosition().x+1,body.getPosition().y+1,20,20);
        setBounds(132,113,size*2,size*2);
        addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("player touch detected");
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
//        System.out.println("Player draw " + System.currentTimeMillis());

        super.draw(batch, parentAlpha);
        body.setTransform(getX()+size,getY()+size,0);

    }
}
