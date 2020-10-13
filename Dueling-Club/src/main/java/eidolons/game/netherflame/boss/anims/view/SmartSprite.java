package eidolons.game.netherflame.boss.anims.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import eidolons.libgdx.anims.sprite.SpriteAnimation;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SmartSprite extends SpriteAnimation {
    /*
    define a custom frame range, but also allow going outside of it on demand
    I'd love to use some alpha cross-fading there too...

this is ping-pong playmode sprite between b and e;
custom getFrame() then?
or just substitute another sprite?
     */

    int begin, end;
    TextureAtlas.AtlasRegion[] frames;
    private boolean reverse;
    private Integer targetFrame;

    public SmartSprite(float frameDuration, TextureAtlas atlas, int begin, int end) {
        this(null, frameDuration, atlas, begin, end);
    }
    public SmartSprite(PlayMode playMode, float frameDuration, TextureAtlas atlas, int begin, int end) {
        super(frameDuration, playMode!=null, atlas);
        frames = Arrays.stream(atlas.getRegions().items).collect(Collectors.toList()).toArray(
                new TextureAtlas.AtlasRegion[atlas.getRegions().size]);
        this.begin = begin;
        this.end = end;
        if (end==0) {
            this.end = atlas.getRegions().size;
        }
        resetRange();
        if (playMode==null) {
            setPlayMode(PlayMode.LOOP_PINGPONG);
        }
    }

    public void allowFinish() {
        end = frames.length;
        setOnCycle(this::resetRange);
    }

    @Override
    public void act(float delta) {
        if (reverse) {
            super.act(-delta);
        }
        super.act(delta);
    }

    @Override
    protected boolean checkCycle() {
        if (targetFrame != null) {
            if (getCurrentFrameNumber() == targetFrame) {
                return true;
            }
        }
        return super.checkCycle();
    }

    //that's kind of for HIT() ? Or other stuff...
    public void playBackTo(int frame, float maxDelay) {
        targetFrame = frame;
        setFps((getCurrentFrameNumber() - frame) / maxDelay);
        TextureAtlas.AtlasRegion[] range = Arrays.copyOfRange(frames, begin, end);
        setKeyFrames(range);
        setOnCycle(() -> {
            //we need it to happen when it hits that frame!
            resetRange();
            setOnCycle(null);
            reverse = false;
            targetFrame = null;
            setFps(getOriginalFps());
        });
        reverse = true;
    }

    private void resetRange() {
        if (end == 0) {
            if (begin == 0)
                setKeyFrames(frames);
            else
                end = frames.length;
        }
        if (end != 0) {
            TextureAtlas.AtlasRegion[] regions = new TextureAtlas.AtlasRegion[end - begin];
            for (int i = begin; i <end ; i++) {
                regions[i]=frames[i];
            }
            setKeyFrames(regions);
        }
    }
}
