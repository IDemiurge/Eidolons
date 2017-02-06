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
    private float rotation;
    private Sprite sprite;

    public SpriteAnimation(String path) {
        this(defaultFrameDuration, false, 1, path, false);
    }

    public SpriteAnimation(String path
            , boolean singleSprite) {
        this(defaultFrameDuration, false, 1, path, singleSprite);
    }

    public SpriteAnimation(float frameDuration, boolean looping, int loops, String path
            , boolean singleSprite) {
        super(frameDuration, TextureManager.getSpriteSheetFrames(path, singleSprite));
        frameNumber = TextureManager.getFrameNumber(path);
        stateTime = 0;
        this.looping = looping;
        this.loops = loops;
    }

    public void reset() {
        stateTime = 0;
        cycles = 0;
        lifecycle = 0;
        rotation=0;
        alpha = 1f;
        sprite = null ;
    }
    public boolean draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        updateSpeed();
        boolean looping = this.looping || loops > cycles || loops == 0;
        TextureRegion currentFrame =  getKeyFrame(stateTime, looping);


        if (currentFrame == null) {
            dispose();
            return false;
        }

        if (getLifecycleDuration() != 0) {
            cycles = (int) (stateTime / getLifecycleDuration());
            lifecycle = stateTime % getLifecycleDuration() / getLifecycleDuration();
        }

if (sprite==null )
         sprite = new Sprite(currentFrame);
        else
            sprite.setRegion(currentFrame);
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public enum SPRITE_BEHAVIOR {
        FREEZE_WHEN_LOOPS_DONE,
    }
}
