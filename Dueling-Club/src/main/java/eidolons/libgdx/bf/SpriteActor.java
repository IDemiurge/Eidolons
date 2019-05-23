package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.gui.generic.GroupX;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 5/6/2018.
 */
public class SpriteActor extends GroupX {

    protected float timeToWait;
    protected float timeToPlay;
    protected boolean paused;
    protected SpriteAnimation anim;
    protected Map<SPRITE_ACTOR_ANIMATION, SpriteAnimation> animsMap = new HashMap<>();
    private float period;

    String animPathRoot;

    public SpriteActor(String animPathRoot) {
        this.animPathRoot = animPathRoot;
    }


    public void play(SPRITE_ACTOR_ANIMATION animation) {
        anim = animsMap.get(animation);
        if (anim == null) {
            anim = createAnim(animation);
            animsMap.put(animation, anim);
        }
        if (anim!=null ){
            newAnim(anim);
        }
    }

    @Override
    public float getHeight() {
        if (CoreEngine.isIggDemo())
        return 80; //TODO igg demo hack
        if (anim != null) {
            return anim.getHeight();
        }
        return super.getHeight();
    }

    @Override
    public float getWidth() {
        if (CoreEngine.isIggDemo())
            return 80; //TODO igg demo hack
        if (anim != null) {
            return anim.getWidth();
        }
        return super.getWidth();
    }

    private SpriteAnimation createAnim(SPRITE_ACTOR_ANIMATION animation) {
       return SpriteAnimationFactory.getSpriteAnimation(animPathRoot +"/" + animation.getAnimAtlasName() + ".txt");
    }

    private void newAnim(SpriteAnimation anim) {
        anim.reset();
        anim.centerOnParent(this);

        paused = false;
        timeToPlay = (anim.getAnimationDuration());
        this.anim.setPlayMode(PlayMode.NORMAL);
        this.anim.setCustomAct(true);
        anim.setFrameDuration(0.02f);
    }

    public SpriteAnimation getAnim() {
        return anim;
    }

    @Override
    public void act(float delta) {
        if (anim==null ){
            return;
        }
        if (anim.isAnimationFinished()) {
            anim = null;
            return;
        }
        anim.setX(getX() - anim.getWidth()/2);
        anim.setY(getY()- anim.getHeight()/2);
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
        if (anim==null ){
            return;
        }
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
        ;

        public String getAnimAtlasName() {
            return "slot 64";
        }
    }
}
