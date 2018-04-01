package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
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
        try {
            animation = SpriteAnimationFactory.getSpriteAnimation(
             getImagePaths().get(0).split("img")[1]
            );
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

//        animation.setFrameDuration(
//         getDuration().getLowMax() / animation.getFrameNumber()/loops);

        super.start();
    }

    @Override
    public void draw(Batch batch, float delta) {
        time += delta;
        try {
            TextureRegion texture = animation.getKeyFrame(time, true);
//        getSprite().setRegion( texture);
//        setSprite(new Sprite(texture));
            for (Particle p : getParticles()) {
                if (p == null) {
                    continue;
                }
                if (percentageOfLaggingParticles > 0) {
                    if (RandomWizard.chance(percentageOfLaggingParticles)) {
                        texture = animation.getPrevious(texture);
                    }
                }
                p.setRegion(texture);
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        super.draw(batch, delta);
    }


    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}
