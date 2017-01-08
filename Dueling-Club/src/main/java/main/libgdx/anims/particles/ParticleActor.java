package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
    static ArrayList<ParticleEffect> array;

    public ParticleActor(/*String presetName,World world, float x, float y*/) {
        array = new ArrayList<>();
//        bodyDef = new BodyDef();
//        bodyDef.position.set(new Vector2(x, y));
//        bodyDef.type = BodyDef.BodyType.KinematicBody;
//        body = world.createBody(bodyDef);
//        shape = new PolygonShape();
//        shape.setAsBox(50, 50);
//        fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        body.createFixture(fixtureDef);
        setBounds(0, 0, 25, 25);
//        for (int i = 0; i < 2; i++) {
//            ParticleEffect pf = new ParticleEffect();
//            PathFinder.init();
//
////        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\particle.pt"),Gdx.files.internal(""));
////            pf.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Smoke_Test.pt"),Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT"));
//            ;
//            pf.load(Gdx.files.internal(PathFinder.getImagePath() +
//             "mini\\sprites\\particles\\" +
//              "Smoke_Test1.pt"),
//             Gdx.files.internal(PathFinder.getImagePath() + "\\mini\\sprites\\particles\\" +
//              ""
//             ));
//            pf.getEmitters().first().setPosition(200 + i * 400, 200);
//            array.add(pf);
//        }

        particleEffect = new ParticleEffect();
        //        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\particle.pt"),Gdx.files.internal(""));
//            pf.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Smoke_Test.pt"),Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT"));
        particleEffect.load(Gdx.files.internal(PathFinder.getImagePath() +
                        "mini\\sprites\\particles\\" +
                        "Smoke_Test1.pt"),
                Gdx.files.internal(PathFinder.getImagePath() + "\\mini\\sprites\\particles\\" +
                        ""
                ));
        particleEffect.getEmitters().first().setPosition(200, 200);
        particleEffect.getEmitters().first().start();
//        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\particle.pt"),Gdx.files.internal(""));
//        particleEffect.load(Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT\\Smoke_Test.pt"),Gdx.files.internal("D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT"));
//        particleEffect.getEmitters().first().setPosition(getX()+200,getY()+200);
//        particleEffect.start();
//        for (int i = 0; i < array.size(); i++) {
//            array.get(i).start();
//            System.out.println("started actor");
//        }

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
//        System.out.println("draw");
//        for (int i =0; i< array.size(); i++){
//
//            array.get(i).update(1/60);
//            System.out.println("updated");
//            array.get(i).draw(batch,1/60);
//            System.out.println("drawd particles");
//            if (array.get(i).isComplete()){
//                array.get(i).reset();
//                System.out.println("RESETED!!!!!!");
//            }
//        }
        super.draw(batch, parentAlpha);

        particleEffect.update(parentAlpha);
        particleEffect.draw(batch, parentAlpha);
        if (particleEffect.isComplete()) {
            particleEffect.reset();
        }
    }
}
