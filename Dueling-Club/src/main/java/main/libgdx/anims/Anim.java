package main.libgdx.anims;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.entity.Ref;
import main.entity.obj.Active;
import main.libgdx.anims.STD_ANIMS.ANIM;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Anim extends Actor {
    private Ref ref;
    //List<Phase> phases;
    int time;
    Point origin;
    Point destination;
    Shape shape;
    java.util.List<Emitter> emitterList;
    int lightEmission;
    Supplier<Texture> textureSupplier;
    private ANIM template;

    public Anim(Active active,
                ANIM template) {
        this.template = template;
        ref = active.getRef();
        Animation a ;//= new Animation()

//        ref.getSourceObj()

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);




    }
}
