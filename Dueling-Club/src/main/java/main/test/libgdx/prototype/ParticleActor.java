package main.test.libgdx.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.data.filesys.PathFinder;

import java.util.ArrayList;

/**
 * Created by PC on 04.12.2016.
 */
public class ParticleActor extends Actor {
    ParticleEffect particleEffect;
    Body body;
    BodyDef bodyDef;
    PolygonShape shape;
    FixtureDef fixtureDef;
    ArrayList<ParticleEffect> array;

    public ParticleActor(World world, float x, float y) {
        array = new ArrayList<>();
        bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bodyDef);
        shape = new PolygonShape();
        shape.setAsBox(50, 50);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        setBounds(body.getPosition().x, body.getPosition().y, shape.getRadius() * 2, shape.getRadius() * 2);
        for (int i = 0; i < 2; i++) {
            ParticleEffect pf = new ParticleEffect();
            PathFinder.init();

//        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\particle.pt"),Gdx.files.internal(""));
//            pf.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Smoke_Test.pt"),Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT"));
            pf.load(Gdx.files.internal(PathFinder.getImagePath() + "\\myFolder\\Smoke_Test.pt"), Gdx.files.internal(PathFinder.getImagePath() + "\\myFolder"));
            pf.getEmitters().first().setPosition(0 + i * 400, 0);
            array.add(pf);
        }

//        particleEffect = new ParticleEffect();
////        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\particle.pt"),Gdx.files.internal(""));
//        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Smoke_Test.pt"),Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT"));
//        particleEffect.getEmitters().first().setPosition(getX()+200,getY()+200);
//        particleEffect.start();
        for (int i = 0; i < array.size(); i++) {
            array.get(i).start();
        }


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        particleEffect.getEmitters().first().setPosition(getX()+10,getY()+10);
//        particleEffect.update(parentAlpha);
//        particleEffect.draw(batch);
//        if (particleEffect.isComplete()){
//            particleEffect.reset();
//            System.out.println("reseted");
//
//        }
        super.draw(batch, parentAlpha);
    }
}
