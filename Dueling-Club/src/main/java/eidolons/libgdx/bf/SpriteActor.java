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
    protected SpriteAnimation curAnim;
    protected Map<SPRITE_ACTOR_ANIMATION, SpriteAnimation> animsMap;
    private float period;

    public void play(SPRITE_ACTOR_ANIMATION animation) {
        curAnim = animsMap.get(animation);
        if (curAnim == null) {
            curAnim = createAnim(animation);
            animsMap.put(animation, curAnim);
        }
        newAnim(curAnim);
    }

    private SpriteAnimation createAnim(SPRITE_ACTOR_ANIMATION animation) {
//        SpriteAnimationFactory.getSpriteAnimation() getDuration
        return null;
    }

    private void newAnim(SpriteAnimation anim) {
        anim.reset();
        curAnim.setX(getX());
        curAnim.setY(getY());
        paused = false;
        timeToPlay = (anim.getAnimationDuration());
        curAnim.setPlayMode(PlayMode.NORMAL);
//        curAnim.getLifecycleDuration()
    }

    @Override
    public void act(float delta) {
        super.act(delta);
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
//            if (curAnim.isAnimationFinished()) {
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
            curAnim.draw(batch);
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
