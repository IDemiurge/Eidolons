package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.gui.generic.GroupX;

import java.util.Map;

/**
 * Created by JustMe on 5/6/2018.
 */
public class SpriteActor extends GroupX {

    protected float timeToWait;
    protected float timeToPlay;
    protected boolean paused;
    protected SpriteAnimation anim;
    protected Map<SPRITE_ACTOR_ANIMATION, SpriteAnimation> animsMap;
    private float period;

    public SpriteActor(   ) {

    }
    public SpriteActor(SpriteAnimation curAnim) {
        this.anim = curAnim;
        newAnim(curAnim);
    }

    public void play(SPRITE_ACTOR_ANIMATION animation) {
        anim = animsMap.get(animation);
        if (anim == null) {
            anim = createAnim(animation);
            animsMap.put(animation, anim);
        }
        newAnim(anim);
    }

    private SpriteAnimation createAnim(SPRITE_ACTOR_ANIMATION animation) {
//        SpriteAnimationFactory.getSpriteAnimation() getDuration
        return null;
    }

    private void newAnim(SpriteAnimation anim) {
        anim.reset();
        this.anim.setX(getX());
        this.anim.setY(getY());
        paused = false;
        timeToPlay = (anim.getAnimationDuration());
        this.anim.setPlayMode(PlayMode.NORMAL);
        this.anim.setCustomAct(true);
//        anim.getLifecycleDuration()
    }

    public SpriteAnimation getAnim() {
        return anim;
    }

    @Override
    public void act(float delta) {
        anim.setX(getX());
        anim.setY(getY());
        anim.act(delta);
        anim.setScale(getScaleX());
        anim.setRotation(getRotation());
//        if (paused) {
//            timeToWait -= delta;
//            if (timeToWait <= 0) {
//                paused = false;
//                timeToWait = 0;
//                playStateAnim();
//
//            }
//        } else {
//            timeToWait -= delta;
//            if (anim.isAnimationFinished()) {
//                reset();
//            }
//        }


    }



    private void reset() {
        paused = true;
        timeToWait = getPeriod();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!paused)
            anim.draw(batch);
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(float period) {
        this.period = period;
    }

    public enum SPRITE_ACTOR_ANIMATION {
        FLASH, FADE_IN_OUT, SCUD_OVER,
    }
}
