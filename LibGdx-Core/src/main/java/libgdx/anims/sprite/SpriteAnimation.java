package libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import libgdx.GdxMaster;
import libgdx.bf.datasource.SpriteData;
import libgdx.screens.CustomSpriteBatch;
import libgdx.texture.TextureManager;
import main.content.enums.GenericEnums.BLENDING;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.launch.Flags;

import java.util.Arrays;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation extends Animation<TextureRegion> implements Blended{
    final static float defaultFrameDuration = 0.025f;
    private TextureAtlas atlas;
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
    private Color color = new Color(1, 1, 1, 1);
    private Float scale;
    private boolean customAct;
    private float speed;
    private boolean aDefault;
    private BLENDING blending;
    private float originX;
    private float originY;
    private Runnable onCycle;
    private int lastCycle;
    private SpriteData data;
    private float pauseBetweenCycles;
    private float pauseBetweenCyclesRandomness=0.5f;
    private float pauseTimer;

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
        this.atlas = atlas;
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
        if (frameDuration > 0) {
            setFps(1f / frameDuration);
        }
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
        if (frameDuration > 0) {
            setFps(1f / frameDuration);
        }
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
        if (pauseTimer>0) {
            pauseTimer-=delta;
            return ;
        }
        stateTime += delta;
    }

    public boolean draw(Batch batch) {

        if (Flags.isFootageMode())
            return false;

        if (!isCustomAct()) {
            act(Gdx.graphics.getDeltaTime());
        }
        if (pauseTimer>0) {
            return false;
        }
        if (frameNumber == 0)
            return false;

        boolean resetBlending = false;
        if (blending != null)
        //            if (batch instanceof CustomSpriteBatch)
        {
            //                if ((((CustomSpriteBatch) batch).getBlending() != blending))
            {
                ((CustomSpriteBatch) batch).setBlending(blending);
                resetBlending = blending==BLENDING.INVERT_SCREEN;
            }
        }
        boolean result = drawThis(batch);

        if (resetBlending)
        //            if (batch instanceof CustomSpriteBatch)
        {
            ((CustomSpriteBatch) batch).resetBlending();
        }
        return result;
    }

    public boolean drawThis(Batch batch) {
        float lifecycleDuration = getLifecycleDuration();
        if (lifecycleDuration != 0) {
            checkReverse();
            lastCycle = cycles;
            cycles = (int) (stateTime / lifecycleDuration);
            if (checkCycle()) {
                if (onCycle != null) {
                    onCycle.run();
                }
                if (pauseBetweenCycles>0){
                    pauseTimer =
                    RandomWizard.randomize(pauseBetweenCycles, pauseBetweenCyclesRandomness);
                    return false;
                }
            }
            lifecycle = stateTime % lifecycleDuration / lifecycleDuration; //% of completion!

        }
        updateSpeed();
        boolean looping = this.looping || loops > cycles || loops == 0;
        if (!looping) {
            if (cycles >= loops) {
                return false;
            }
        }
        TextureRegion currentFrame = getKeyFrame(stateTime, looping);
        if (currentFrame == null) {
            dispose();
            return false;
        }

        float alpha = this.alpha;
        drawTextureRegion(batch, currentFrame, alpha, offsetX, offsetY);

        // try {
        //     for (int i = 1; i < getTrailingFramesNumber(); ) {
        //
        //         TextureRegion frame = getOffsetFrame(stateTime, -i);
        //         if (frame == null) {
        //             LogMaster.log(1, stateTime + " null");
        //             return true;
        //         }
        //         i++;
        //         alpha = 3f / (i * i);
        //         if (alpha > 0.1f)
        //             drawTextureRegion(batch, currentFrame, alpha, offsetX, offsetY);
        //         else
        //             return true;
        //     }
        // } catch (Exception e) {
        //     ExceptionMaster.printStackTrace(e);
        // }
        return true;
    }

    protected boolean checkCycle() {
        return cycles > lastCycle;
    }

    @Override
    public BLENDING getBlending() {
        return blending;
    }

    public void setBlending(BLENDING blending) {
        this.blending = blending;
    }

    private int getTrailingFramesNumber() {
        if (!isDrawTrailing()) {
            return 0;
        }
        return getFrameNumber() - 1;
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
            sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        } else {
            sprite.setRegion(currentFrame);
            //update orig?
        }
        sprite.flip(flipX, flipY);

        sprite.setAlpha(alpha);
        sprite.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        if (getScale() != null) {
            sprite.setScale(getScale());
            sprite.setPosition((int) (x + offsetX - getScale() * currentFrame.getRegionWidth() / 2), y
                    + (int) (offsetY
                    - getScale() * currentFrame.getRegionHeight() / 2));
        } else {
            sprite.setPosition((int) (x + offsetX - currentFrame.getRegionWidth() / 2), y
                    + (int) (offsetY
                    - currentFrame.getRegionHeight() / 2));
        }
        sprite.setRotation(rotation);
        sprite.setOrigin(originX, originY);

        //        if (color != null)
        sprite.setColor(color);
        if (!batch.isDrawing()) {
            batch.begin();
        }
        //        if (batch instanceof CustomSpriteBatch) {
        //            ((CustomSpriteBatch) batch).setBlending(blending);
        //        }
        sprite.draw(batch);

    }

    public void setData(SpriteData spriteData) {
        this.data = spriteData;
        initData();

    }

    private void initData() {
        for (String s : data.getValues().keySet()) {
            SpriteData.SPRITE_VALUE spriteValue = data.getEnumConst(s);
            float f = data.getFloatValue(spriteValue);
            Boolean bool = data.getBooleanValue(spriteValue);
            switch (spriteValue) {
                case color:
                case backAndForth:
                case playMode:
                    break;
                case blending:
                    setBlending(new EnumMaster<BLENDING>().retrieveEnumConst(BLENDING.class, data.getValue(spriteValue)));
                    break;
                case fps:
                    setFps(f);
                    break;
                case loops:
                    setLoops((int) f);
                    break;
                case x:
                    setOffsetX(f);
                    break;
                case y:
                    setOffsetY(f);
                    break;
                case scale:
                    setScale(f);
                    break;
                case rotation:
                    setRotation(f);
                    break;
                case flipX:
                    setFlipX(bool);
                    break;
                case flipY:
                    setFlipY(bool);
                    break;
                case alpha:
                    setAlpha(f);
                    break;
            }

        }
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
        if (regions.size < 0)
            return null;
        // if (getKeyFrames().length > 0) { //TODO
            return getKeyFrame(stateTime, looping);
        // }
        // return null;
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

    public float getOriginalFps() {
        return originalFps;
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
        if (looping) {
            if (loops > 0) {
                return loops <= cycles;
            }
            return false;
        }
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
        setFps(Math.round(originalFps * speed));
        this.speed = speed;
    }

    public float getSpeed() {

        return speed;
    }

    public float getHeight() {
        if (ListMaster.isNotEmpty(getRegions())) {
            return getRegions().get(getCurrentFrameNumber()).packedHeight;
        }
        if (getKeyFrames().length > 0) {
            return getKeyFrames()[(getCurrentFrameNumber())].getRegionHeight();
        }
        return 0;
    }

    public float getWidth() {
        if (ListMaster.isNotEmpty(getRegions())) {
            return getRegions().get(getCurrentFrameNumber()).packedWidth;
        }
        if (getKeyFrames().length > 0) {
            return getKeyFrames()[(getCurrentFrameNumber())].getRegionWidth();
        }
        return 0;
    }

    @Override
    public TextureRegion[] getKeyFrames() {
        if (regions == null) {
            return new TextureRegion[0];
        }
        if (regions.size == 0) {
            return new TextureRegion[0];
        }
        return super.getKeyFrames();
    }

    public void centerOnScreen() {
        setOffsetX((GdxMaster.getWidth() - getWidth()) / 2 + getWidth() / 2);
        setOffsetY((GdxMaster.getHeight() - getHeight()) / 2 + getHeight() / 2);
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

    public void setFps(float i) {
        if (originalFps == 0) {
            originalFps = i;
        }
        setFrameDuration(1f / i);
    }

    public void setDefault(boolean aDefault) {
        this.aDefault = aDefault;
    }

    public boolean isDefault() {
        return aDefault;
    }

    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;

    }

    public boolean isFlipY() {
        return flipY;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setOnCycle(Runnable onCycle) {
        this.onCycle = onCycle;
    }

    public Runnable getOnCycle() {
        return onCycle;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void setPauseBetweenCyclesRandomness(float pauseBetweenCyclesRandomness) {
        this.pauseBetweenCyclesRandomness = pauseBetweenCyclesRandomness;
    }

    public void setPauseBetweenCycles(float pauseBetweenCycles) {
        this.pauseBetweenCycles = pauseBetweenCycles;
    }
}
