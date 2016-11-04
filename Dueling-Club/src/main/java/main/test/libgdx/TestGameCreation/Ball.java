package main.test.libgdx.TestGameCreation;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by PC on 01.11.2016.
 */
public class Ball extends GameObj {
    CircleShape shape;
    public Ball(World world) {
        super(world);

        shape = new CircleShape();
        shape.setRadius(2);
        setBounds(10,15,shape.getRadius()*2,shape.getRadius()*2);
        createBody(shape, BodyDef.BodyType.DynamicBody);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(body.getPosition().x-shape.getRadius(),body.getPosition().y-shape.getRadius()); // задаём позицию актёру!!!!!!
    }
}
