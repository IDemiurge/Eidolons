package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.data.filesys.PathFinder;

/**
 * Created by PC on 02.11.2016.
 */
public class HitBall extends Actor {
    private World world;
    Body body;
    BodyDef bDef;
    FixtureDef fDef;
    CircleShape shape;
    Sprite sprite;
    float radius = 1;
    public HitBall(World world){
        this.world = world;
        bDef = new BodyDef();
        setBounds(10,15,radius*2,radius*2);
        bDef.position.set(getX(),getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bDef);
        shape = new CircleShape();
        shape.setRadius(radius);
        fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = 2f;
        fDef.restitution = 1;
        body.createFixture(fDef);
        body.setUserData("ball");
        PathFinder.init();
        String q = PathFinder.getImagePath();
        Texture textureAtlas = new Texture(q + "mini\\unit\\Nature\\Wisp.jpg");
        textureAtlas.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(textureAtlas);
        sprite.setBounds(0,0,radius*2,radius*2);
        sprite.setOriginCenter();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        sprite.draw(batch);
//        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        setPosition(body.getPosition().x-radius,body.getPosition().y-radius);
        sprite.setPosition(body.getPosition().x - sprite.getWidth()/2,body.getPosition().y - sprite.getHeight()/2);
        sprite.setRotation(MathUtils.radiansToDegrees*body.getAngle());
        super.act(delta);
    }
}
