package main.libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.texture.TextureManager;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation extends Animation<TextureRegion> {
    final static float defaultFrameDuration = 0.025f;
    public float x;
    public float y;
    private int loops;
    private boolean looping;
    private float stateTime;
    private float offsetY;
    private float offsetX;
    private float frameNumber;
    private float alpha = 1f;
    private int cycles;
    private float lifecycle;
    private float angle;

    public SpriteAnimation(String path) {
        this(defaultFrameDuration, false, 1, path);
    }

    /*
    play_mode.

     */
    public SpriteAnimation(float frameDuration, boolean looping, int loops, String path) {
        super(frameDuration, TextureManager.getSpriteSheetFrames(path));
        frameNumber = TextureManager.getFrameNumber(path);
        stateTime = 0;
        this.looping = looping;
        this.loops = loops;
    }

    public void reset() {
        stateTime = 0;
        cycles = 0;
        lifecycle = 0;
    }
    public boolean draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        updateSpeed();
        boolean looping = this.looping || loops > cycles || loops == 0;
        TextureRegion currentFrame = (TextureRegion) getKeyFrame(stateTime, looping);


        if (currentFrame == null) {
            dispose();
            return false;
        }

        if (getLifecycleDuration() != 0) {
            cycles = (int) (stateTime / getLifecycleDuration());
            lifecycle = stateTime % getLifecycleDuration() / getLifecycleDuration();
        }

        Sprite sprite = new Sprite(currentFrame);
        sprite.setAlpha(alpha);
//        angle = RandomWizard.getRandomInt(360) / 360f;
//        sprite.setRotation(angle);
        sprite.setPosition(x + offsetX - currentFrame.getRegionWidth() / 2, y
                + offsetY
                - currentFrame.getRegionHeight() / 2);
        sprite.draw(batch);

        batch.draw(currentFrame, x + offsetX - currentFrame.getRegionWidth() / 2, y
                + offsetY
                - currentFrame.getRegionHeight() / 2);

        return true;
    }

    private void updateSpeed() {
//        setFrameDuration(); TODO (de)acceleration !
    }

    public void dispose() {
//        spriteBatch.dispose();
//        sheet.dispose();
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
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

    public enum SPRITE_BEHAVIOR {
        FREEZE_WHEN_LOOPS_DONE,
    }
}
