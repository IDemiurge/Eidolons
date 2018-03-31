package eidolons.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.entity.Ref;
import main.system.EventCallback;
import main.system.EventCallbackParam;

/**
 * Created by JustMe on 1/29/2017.
 */
public interface Animation {

    void reset();

    void finished();

    boolean tryDraw(Batch batch);

    boolean draw(Batch batch);

    void start(Ref ref);

    void start();

    ANIM_PART getPart();

    float getTime();

    float getDelay();

    void setDelay(float delay);

    boolean isRunning();

    void onDone(EventCallback callback, EventCallbackParam param);
}
