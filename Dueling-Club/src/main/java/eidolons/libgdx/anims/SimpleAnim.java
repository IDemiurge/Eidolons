package eidolons.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.particles.spell.SpellVfx;
import eidolons.libgdx.particles.spell.SpellVfxPool;
import main.content.enums.GenericEnums;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnim implements Animation {

    List<SpriteX> sprites;
    List<SpellVfx> vfx;
    private boolean done;
    private int fps = 14;
    private GenericEnums.BLENDING blending = GenericEnums.BLENDING.SCREEN;
    private boolean parallel;

    String spritePaths;
    String vfxPaths;
    Runnable onDone;

    Vector2 origin;
    Vector2 dest;
    Vector2 pos;
    Float duration;

    GraphicData data;

    public SimpleAnim(String vfx, String spritePaths, Runnable onDone, Vector2 origin, Vector2 dest, Float duration) {
        this.vfxPaths = vfx;
        this.spritePaths = spritePaths;
        this.onDone = onDone;
        this.origin = origin;
        this.dest = dest;
        this.duration = duration;
    }

    public SimpleAnim(String spritePaths, Runnable onDone) {
        this(null, spritePaths, onDone);
    }

    public SimpleAnim( GraphicData data, String vfx, String spritePaths, Runnable onDone) {
        this(vfx, spritePaths, onDone);
        this.data=data;
    }
    public SimpleAnim(String vfx, String spritePaths, Runnable onDone) {
        this(vfx, spritePaths, onDone, GridMaster.getCenteredPos(
                Eidolons.getPlayerCoordinates()), null, null);
        this.spritePaths = spritePaths;
        this.onDone = onDone;
    }

    @Override
    public void start(Ref ref) {
        if (data == null) {
            pos = origin;
        } else
            pos =                    new Vector2(origin.x + data.getFloatValue(GraphicData.GRAPHIC_VALUE.x),
                            origin.y + data.getFloatValue(GraphicData.GRAPHIC_VALUE.y));
        vfx = SpellVfxPool.getEmitters(vfxPaths, 1);
        for (SpellVfx spellVfx : vfx) {
            spellVfx.start();
            spellVfx.allowFinish();

        }
        if (sprites == null) {
            sprites = new ArrayList<>();

            for (String substring : ContainerUtils.openContainer(spritePaths)) {
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
        if (onDone != null) {
            onDone.run();
        }
    }

    @Override
    public boolean tryDraw(Batch batch) {
        //TODO dest
        //        if (duration == null) what's that?
        {
            done = true;

            for (SpellVfx spellVfx : vfx) {
                spellVfx.updatePosition(pos.x, pos.y);
                spellVfx.draw(batch, 1f);
                if (!spellVfx.isComplete()) {
                    done = false;
                }
            }

            for (SpriteX sprite : sprites) {
                sprite.setBlending(blending);
                sprite.setFps(fps);
                sprite.setX(pos.x);
                sprite.setY(pos.y);

                if (sprite.draw(batch)) {
                    done = false;
                }

            }
            if (done) {
                finished();
                return false;
            }
        }
        return true;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setBlending(GenericEnums.BLENDING blending) {
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
    public AnimEnums.ANIM_PART getPart() {
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
        dest = GridMaster.getCenteredPos(forcedDestination);
    }

    public void setDest(Vector2 dest) {
        this.dest = dest;
    }

    @Override
    public void setParentAnim(CompositeAnim compositeAnim) {

    }


    @Override
    public void setDone(boolean b) {

    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }
}
