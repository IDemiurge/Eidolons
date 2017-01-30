package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;

/**
 * Created by JustMe on 1/29/2017.
 */
public interface Animation {

    void reset();

    void finished();

    boolean draw(Batch batch);

    void start();

    ANIM_PART getPart();

    float getTime();

    float getDelay();

    boolean isRunning();

    void setDelay(float delay);
}
