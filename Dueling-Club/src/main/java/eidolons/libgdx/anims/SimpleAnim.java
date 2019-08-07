package eidolons.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.Animation;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.screens.DungeonScreen;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnim implements Animation {
    String spritePath;
    Runnable onDone;
    Vector2 origin;
    Vector2 dest;
    Vector2 pos;
    Float duration;
    List<SpriteX> sprites;
    private boolean done;
    private int fps=14;
    private SuperActor.BLENDING blending= SuperActor.BLENDING.SCREEN;

    public SimpleAnim(String spritePath, Runnable onDone, Vector2 origin, Vector2 dest, Float duration) {
        this.spritePath = spritePath;
        this.onDone = onDone;
        this.origin = origin;
        this.dest = dest;
        this.duration = duration;
    }

    public SimpleAnim(String spritePath, Runnable onDone) {
        this(spritePath, onDone, GridMaster.getCenteredPos(Eidolons.getMainHero().getCoordinates()), null, null);
        this.spritePath = spritePath;
        this.onDone = onDone;
    }

    @Override
    public void start(Ref ref) {
        pos = origin;
        if (sprites == null) {
            sprites = new ArrayList<>();
            for (String substring : ContainerUtils.openContainer(spritePath)) {
                SpriteX sprite = new SpriteX(substring);
                //set stuff?
                sprite.getSprite().setLooping(false);
                sprite.getSprite().setCustomAct(false);
                sprite.getSprite().setLoops(1);
                sprites.add(sprite);
            }
        }
    }

    public void setOrigin(Vector2 origin) {
        this.origin = origin;
    }

    @Override
    public void reset() {
        done = false;
    }

    @Override
    public void finished() {
        onDone.run();
    }

    @Override
    public boolean tryDraw(Batch batch) {
        //TODO dest
        if (duration == null) {
            done = true;
            for (SpriteX sprite : sprites) {
                sprite.setBlending(blending);
                sprite.setFps(fps);
                sprite.setX(pos.x);
//                DungeonScreen.getInstance().getCamera().position.x);
                sprite.setY(pos.y);

                if (sprite.draw(batch)) {
                    done = false;
                }

            }
            if (done)
            {
                finished();
                return false;
            }
        }
        return true;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setBlending(SuperActor.BLENDING blending) {
        this.blending = blending;
    }
    @Override
    public boolean draw(Batch batch) {
        return tryDraw(batch);
    }


    @Override
    public void start() {
        start(null);
    }

    @Override
    public AnimConstructor.ANIM_PART getPart() {
        return null;
    }

    @Override
    public float getTime() {
        return 0;
    }

    @Override
    public float getDelay() {
        return 0;
    }

    @Override
    public void setDelay(float delay) {

    }


    @Override
    public boolean isRunning() {
        return !done;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void onDone(EventCallback callback, EventCallbackParam param) {

    }

    @Override
    public Ref getRef() {
        return Eidolons.getMainHero().getRef();
    }

    @Override
    public void setForcedDestination(Coordinates forcedDestination) {

    }

    @Override
    public void setParentAnim(CompositeAnim compositeAnim) {

    }


    @Override
    public void setDone(boolean b) {

    }
}
