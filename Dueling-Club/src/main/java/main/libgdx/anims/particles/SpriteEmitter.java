package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.system.auxiliary.RandomWizard;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by JustMe on 1/29/2017.
 */
public class SpriteEmitter extends Emitter {
    int loops = 1;
    int percentageOfLaggingParticles;
    SpriteAnimation animation;
    private float time;

    public SpriteEmitter(BufferedReader reader) throws IOException {
        super(reader);

    }

    @Override
    public boolean isPremultipliedAlpha() {
        return true;
    }

    @Override
    public void start() {
//        if (animation==null )
        time = 0;
        animation = new SpriteAnimation(getImagePath());

//        animation.setFrameDuration(
//         getDuration().getLowMax() / animation.getFrameNumber()/loops);

        super.start();
    }

    @Override
    public void draw(Batch batch, float delta) {
        time += delta;
        TextureRegion texture = animation.getKeyFrame(time, true);
//        getSprite().setRegion( texture);
//        setSprite(new Sprite(texture));


        for (Particle p : getParticles()) {
            if (p == null) continue;
            if (percentageOfLaggingParticles > 0) {
                if (RandomWizard.chance(percentageOfLaggingParticles)) {
                    texture = animation.getPrevious(texture);
                }
            }
            p.setRegion(texture);

        }
        super.draw(batch, delta);
    }


    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}
