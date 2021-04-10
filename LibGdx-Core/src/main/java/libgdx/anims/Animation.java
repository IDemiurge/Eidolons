package libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.main.AnimMaster;
import libgdx.particles.spell.SpellVfx;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 1/29/2017.
 */
public interface Animation {


    void start(Ref ref);

    void start();

    void reset();

    boolean tryDraw(Batch batch);

    boolean draw(Batch batch);

    void finished();

    ANIM_PART getPart();

    float getTime();

    float getDelay();

    void setDelay(float delay);

    boolean isRunning();

    void onDone(EventCallback callback, EventCallbackParam param);

    Ref getRef();

    void setForcedDestination(Coordinates forcedDestination);

    void setParentAnim(CompositeAnim compositeAnim);

    default void startAsSingleAnim( ) {
        startAsSingleAnim(getRef());
    }
    default void startAsSingleAnim(Ref ref) {
        start(ref);
        AnimMaster.getInstance().add(new CompositeAnim(this));
    }
    default List<Pair<GuiEventType, EventCallbackParam>> getEventsOnFinish() {
        return null;
    }

    default List<Pair<GuiEventType, EventCallbackParam>> getEventsOnStart() {
        return null;
    }

    default Entity getActive() {
        return null;
    }

    default float getPixelsPerSecond() {
        return 0f;
    }

    default void setEmitterList(List<SpellVfx> list) {

    }

    boolean isDone();

    void setDone(boolean b);
}
