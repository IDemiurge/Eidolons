package main.libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.texture.TextureManager;

import java.util.Arrays;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation extends Animation<TextureRegion> {
    final static float defaultFrameDuration = 0.025f;
    public float x;
    public float y;
    boolean backAndForth;
    private int loops;
    private boolean looping;
    private float stateTime;
    private float offsetY;
    private float offsetX;
    private float frameNumber;
    private float alpha = 1f;
    private int cycles;
    private float lifecycle;
    private float rotation;
    private Sprite sprite;
    private boolean attached = true;
    private PlayMode originalPlayMode;


    public SpriteAnimation(String path) {
        this(defaultFrameDuration, false, 1, path, null, false);
    }

    public SpriteAnimation(String path
            , boolean singleSprite) {
        this(defaultFrameDuration, false, 1, path, null, singleSprite);
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
    }

    public SpriteAnimation(Texture texture) {
        this(defaultFrameDuration, false, 1, null, texture, false);
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

    public boolean draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        updateSpeed();
        boolean looping = this.looping || loops > cycles || loops == 0;
        TextureRegion currentFrame = getKeyFrame(stateTime, looping);


        if (currentFrame == null) {
            dispose();
            return false;
        }

        if (getLifecycleDuration() != 0) {
            checkReverse();
            cycles = (int) (stateTime / getLifecycleDuration());
            lifecycle = stateTime % getLifecycleDuration() / getLifecycleDuration();
        }

        if (sprite == null) {
            sprite = new Sprite(currentFrame);
        } else {
            sprite.setRegion(currentFrame);
        }
        sprite.setAlpha(alpha);

        sprite.setRotation(rotation);
        sprite.setPosition(x + offsetX - currentFrame.getRegionWidth() / 2, y
                + offsetY
                - currentFrame.getRegionHeight() / 2);
        sprite.draw(batch);


        return true;
    }


    private void updateSpeed() {
//        setFrameDuration(); TODO (de)acceleration !
    }

    public void dispose() {
//        spriteBatch.dispose();
//        sheet.dispose();
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

    public TextureRegion getOffset(TextureRegion texture, int offset) {
        int index =
                Arrays.asList(getKeyFrames()).indexOf(texture);
        index += offset;
        while (index < 0) {
            index = getKeyFrames().length - offset;
            offset--;
        }
        while (index >= getKeyFrames().length) {
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

    public float getFrameNumber() {
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

    public enum SPRITE_BEHAVIOR {
        FREEZE_WHEN_LOOPS_DONE,
    }

}
