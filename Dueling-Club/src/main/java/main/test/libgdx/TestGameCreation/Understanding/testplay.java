package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.data.filesys.PathFinder;

/**
 * Created by PC on 01.11.2016.
 */
public class testplay extends Actor {
    Body body;
    BodyDef bDef;
    FixtureDef fDef;
    CircleShape shape;
//    PolygonShape shape
    Sprite sprite;
    float sizeX = 10;
    float sizeY = 10;

    float radius =2;

    World world;
    public testplay(World world) {
    this.world = world;
        bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.KinematicBody;
        bDef.position.set(new Vector2(10,10));
        body = world.createBody(bDef);
//        PolygonShape shape = new PolygonShape();
       shape = new CircleShape();
//        shape.setAsBox(3,2);
        shape.setRadius(radius);
//        setBounds(bDef.position.x,bDef.position.y,shape.getRadius()*2,shape.getRadius()*2);
       setPosition(0,0);
        setSize(radius*2,radius*2);
        fDef = new FixtureDef();
        fDef.density = 2;
        fDef.restitution = 1f;
        fDef.shape = shape;
        body.createFixture(fDef);
        body.setUserData("player");
        PathFinder.init();
        String q = PathFinder.getImagePath();
        Texture textureAtlas = new Texture(q + "mini\\unit\\Nature\\griff.jpg");
        textureAtlas.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite = new Sprite(textureAtlas);
        sprite.setBounds(0,0,radius*2,radius*2);
        sprite.setOriginCenter();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        setPosition(body.getPosition().x-shape.getRadius(),body.getPosition().y-shape.getRadius());

        sprite.setPosition(body.getPosition().x - sprite.getWidth()/2,body.getPosition().y - sprite.getHeight()/2);
        sprite.setRotation(MathUtils.radiansToDegrees*body.getAngle());
        sprite.draw(batch);
//        super.draw(batch,parentAlpha);
    }

    @Override
    public void act(float delta) {
        body.setTransform(getX()+shape.getRadius(),getY()+shape.getRadius(),0);
        super.act(delta);
    }
}
