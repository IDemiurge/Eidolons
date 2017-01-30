package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.anims.sprite.SpriteAnimation;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by JustMe on 1/29/2017.
 */
public class SpriteEmitter extends  Emitter {
    private float time;

    public SpriteEmitter(BufferedReader reader) throws IOException {
        super(reader);
time=0;
    }

    @Override
    public void start() {
//        if (animation==null )
            animation=new SpriteAnimation(getImagePath());
        super.start();
    }

    SpriteAnimation animation;
    @Override
    public void draw(Batch batch, float delta) {
        time += delta;
        TextureRegion texture = animation.getKeyFrame(time, true);
        getSprite().setRegion( texture);
        super.draw(batch, delta);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}
