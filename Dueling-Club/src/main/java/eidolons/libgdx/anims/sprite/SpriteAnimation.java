package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.SpriteActor;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.texture.TextureManager;
import main.system.ExceptionMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.Arrays;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation extends Animation<TextureRegion> {
    final static float defaultFrameDuration = 0.025f;
    private float originalFps;
    public float x;
    public float y;
    boolean backAndForth;
    private Array<AtlasRegion> regions;
    private int loops;
    private boolean looping;
    private float stateTime;
    private float offsetY;
    private float offsetX;
    private int frameNumber;
    private float alpha = 1f;
    private int cycles;
    private float lifecycle;
    private float rotation;
    private Sprite sprite;
    private boolean attached = true;
    private PlayMode originalPlayMode;
    private boolean flipX;
    private boolean flipY;
    private Color color;
    private Float scale;
    private boolean customAct;
    private float speed;
    private boolean aDefault;
    private SuperActor.BLENDING blending;

    public void setBackAndForth(boolean backAndForth) {
        this.backAndForth = backAndForth;
    }

    public SpriteAnimation(String path) {
        this(defaultFrameDuration, false, 1, path, null, false);
    }

    public SpriteAnimation(String path
            , boolean singleSprite) {
        this(defaultFrameDuration, false, 1, path, null, singleSprite);
    }

    public SpriteAnimation(float frameDuration, boolean backAndForth, TextureAtlas atlas) {
        this(frameDuration, backAndForth, SpriteAnimationFactory.getSpriteRegions(backAndForth, atlas));
    }

    public SpriteAnimation(float frameDuration, boolean looping, TextureAtlas atlas, String name) {
        this(frameDuration, looping, atlas.findRegions(name));
    }

    public SpriteAnimation(float frameDuration, boolean looping, int loops, String path,
                           Texture texture
            , boolean singleSprite) {
        super(frameDuration, TextureManager.getSpriteSheetFrames(path, singleSprite, texture));
        if (path != null) {
            frameNumber = TextureManager.getFrameNumber(path);
        }
        stateTime = 0;
        this.looping = looping;
        this.loops = loops;
        originalFps = frameDuration;
    }

    public SpriteAnimation(Texture texture) {
        this(defaultFrameDuration, false, 1, null, texture, false);
    }

    public SpriteAnimation(float frameDuration, boolean looping,
                           Array<AtlasRegion> re
    ) {
        super(frameDuration, re);
        regions = re;
        this.looping = looping;
        this.frameNumber = re.size;
        originalFps = frameDuration;
    }


    public void start() {
    }


    public void reset() {
        stateTime = 0;
        cycles = 0;
        lifecycle = 0;
        rotation = 0;
        alpha = 1f;
        sprite = null;
    }

    private void checkReverse() {
        if (backAndForth) {
            if (cycles != -1) {
                if (getLifecycleDuration() != 0) {
                    if ((int) (stateTime / getLifecycleDuration()) > cycles) {
                        if (getPlayMode() == PlayMode.LOOP_REVERSED) {
                            setPlayMode(originalPlayMode);
                        } else {
                            originalPlayMode = getPlayMode();
                            setPlayMode(PlayMode.LOOP_REVERSED);
                        }
                    }
                }
            }
        }
    }

    public void act(float delta) {
        stateTime += delta;
    }

    public boolean draw(Batch batch) {

        if (CoreEngine.isCinematicMode())
            return false;

        if (!isCustomAct()) {
            act(Gdx.graphics.getDeltaTime());
        }
        if (frameNumber == 0)
            return false;

        boolean resetBlending = false;
        if (blending != null)
            if (batch instanceof CustomSpriteBatch) {
                if ((((CustomSpriteBatch) batch).getBlending() != blending)) {
                    ((CustomSpriteBatch) batch).setBlending(blending);
                    resetBlending = true;
                }
            }
        boolean result = drawThis(batch);

        if (resetBlending)
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).resetBlending();
            }
        return result;
    }

    public boolean drawThis(Batch batch) {
        if (getLifecycleDuration() != 0) {
            checkReverse();
            cycles = (int) (stateTime / getLifecycleDuration());
            lifecycle = stateTime % getLifecycleDuration() / getLifecycleDuration();
        }
        updateSpeed();
        boolean looping = this.looping || loops > cycles || loops == 0;

        TextureRegion currentFrame = getKeyFrame(stateTime, looping);
        if (currentFrame == null) {
            dispose();
            return false;
        }

        float alpha = this.alpha;
        drawTextureRegion(batch, currentFrame, alpha, offsetX, offsetY);

        try {
            for (int i = 1; i < getTrailingFramesNumber(); ) {

                TextureRegion frame = getOffsetFrame(stateTime, -i);
                if (frame == null) {
                    LogMaster.log(1, stateTime + " null");
                    return true;
                }
                i++;
                alpha = 3f / (i * i);
                if (alpha > 0.1f)
                    drawTextureRegion(batch, currentFrame, alpha, offsetX, offsetY);
                else
                    return true;
            }
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return true;
    }

    public SuperActor.BLENDING getBlending() {
        return blending;
    }

    public void setBlending(SuperActor.BLENDING blending) {
        this.blending = blending;
    }

    private int getTrailingFramesNumber() {
        if (!isDrawTrailing()) {
            return 0;
        }
        return (int) (getFrameNumber() - 1);
    }

    private boolean isDrawTrailing() {
        return false;
    }

    private void drawTextureRegion(Batch batch, TextureRegion currentFrame, float alpha
            , float offsetX, float offsetY
    ) {
        if (alpha <= 0) {
            return;
        }

        if (sprite == null) {
            sprite = new Sprite(currentFrame);
        } else {
            sprite.setRegion(currentFrame);
        }
        sprite.flip(flipX, flipY);

        sprite.setAlpha(alpha);
        sprite.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        if (getScale() != null)
            sprite.setScale(getScale());
        sprite.setRotation(rotation);
        sprite.setPosition(x + offsetX - currentFrame.getRegionWidth() / 2, y
                + offsetY
                - currentFrame.getRegionHeight() / 2);

        if (color != null)
            sprite.setColor(color);
        if (!batch.isDrawing()) {
            batch.begin();
        }
        sprite.draw(batch);

    }


    private void updateSpeed() {
        //        setFrameDuration(); TODO (de)acceleration !
    }

    public void dispose() {
        //        spriteBatch.dispose();
        //        sheet.dispose();
    }

    public Array<AtlasRegion> getRegions() {
        return regions;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getLifecycleDuration() {
        return getFrameDuration() * frameNumber;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public TextureRegion getPrevious(TextureRegion texture) {
        return getOffset(texture, -1);
    }

    public TextureRegion getNext(TextureRegion texture) {
        return getOffset(texture, 1);
    }

    public TextureRegion getCurrentFrame() {
        return getKeyFrame(stateTime, looping);
    }

    public int getCurrentFrameNumber() {
        return getKeyFrameIndex(stateTime);
    }

    public TextureRegion getOffsetFrame(
            float time, int offset) {
        int index = getKeyFrameIndex(time);
        index += offset;

        if (index < 0 || index > getFrameNumber()) {
            return null;
        }
        //        Object region = getKeyFrames()[index];
        //        if (region instanceof TextureRegion)
        //            return (TextureRegion) region;
        if (regions == null) {
            return getKeyFrames()[index];
        }
        return regions.get(index);
    }


    public TextureRegion getOffset(TextureRegion texture, int offset) {
        return getOffset(texture, offset, false);
    }

    public TextureRegion getOffset(TextureRegion texture, int offset, boolean exceptLastFrame) {
        int index =
                Arrays.asList(getKeyFrames()).indexOf(texture);
        index += offset;

        while (index < 0) {
            if (exceptLastFrame)
                return null;
            index = getKeyFrames().length - offset;
            offset--;
        }
        while (index >= getKeyFrames().length) {
            if (exceptLastFrame)
                return null;
            index = offset;
            offset--;
        }
        return getKeyFrames()[index];
    }

    public float getStateTime() {
        return stateTime;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getCycles() {
        return cycles;
    }

    public float getLifecycle() {
        return lifecycle;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isAnimationFinished() {
        if (looping)
            return false;
        if (getPlayMode() == PlayMode.LOOP)
            return false;
        if (getPlayMode() == PlayMode.LOOP_PINGPONG)
            return false;
        if (getPlayMode() == PlayMode.LOOP_REVERSED)
            return false;
        if (getPlayMode() == PlayMode.LOOP_RANDOM)
            return false;
        return isAnimationFinished(getStateTime());
    }

    public boolean isCustomAct() {
        return customAct;
    }

    public void setCustomAct(boolean customAct) {
        this.customAct = customAct;
    }

    public void setSpeed(float speed) {
        setFrameDuration(originalFps / speed);
        this.speed = speed;
    }

    public float getSpeed() {

        return speed;
    }

    public float getHeight() {
        if (!ListMaster.isNotEmpty(getRegions())) {
            return 0;
        }
        return getRegions().get(0).packedHeight;
    }

    public float getWidth() {
        if (!ListMaster.isNotEmpty(getRegions())) {
            return 0;
        }
        return getRegions().get(0).packedWidth;
    }

    public void centerOnScreen() {
        setOffsetX(Math.abs(GdxMaster.getWidth() - getWidth()) / 2 + getWidth() / 2);
        setOffsetY(Math.abs(GdxMaster.getHeight() - getHeight()) / 2 + getHeight() / 2);
    }
        public void centerOnParent(Actor actor) {
        Vector2 pos = new Vector2(actor.getX(), actor.getY());
        actor.localToStageCoordinates(pos);
//        pos2= actor.getStage().stageToScreenCoordinates(pos2);
        setX(pos.x);
        setY(pos.y);
        setOffsetX(Math.abs(actor.getWidth() - getWidth()) / 2 + getWidth() / 2);
        setOffsetY(Math.abs(actor.getHeight() - getHeight()) / 2 + getHeight() / 2);

    }

    public void setFps(int i) {
        setFrameDuration(1f / i);
    }

    public void setDefault(boolean aDefault) {
        this.aDefault = aDefault;
    }

    public boolean isDefault() {
        return aDefault;
    }


    public enum SPRITE_BEHAVIOR {
        FREEZE_WHEN_LOOPS_DONE,
    }

}
