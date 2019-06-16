package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.screens.CustomSpriteBatch;

/**
 * Created by JustMe on 11/8/2018.
 */
public class FadeSprite extends FadeImageContainer {
    SpriteAnimation spriteAnimation;
    private BLENDING blending;

    public FadeSprite(SpriteAnimation spriteAnimation) {
        this.spriteAnimation = spriteAnimation;
        spriteAnimation.setAlpha(0);
        spriteAnimation.setLooping(true);
        spriteAnimation.setFrameDuration(1.25f);
        spriteAnimation.setCustomAct(true);
        setBlending(BLENDING.SCREEN);
    }

    public FadeSprite(String imagePath) {
        this(SpriteAnimationFactory.getSpriteAnimation(imagePath));
    }

    @Override
    public void setContents(Actor contents) {
        if (previousImage == contents)
            return;

        if (previousImage != null) {
            ActorMaster.addFadeOutAction(previousImage, getFadeDuration() * 2, true);
        }
        previousImage = getContent();

        this.content = contents;
        addActor(contents);
        if (previousImage != null) {
            //            previousImage.getColor().a = 1;
            getContent().getColor().a = 0;
            fadePercentage = 1f;
        }
    }

    @Override
    public void setTexture(Drawable drawable) {
        if (drawable instanceof TextureRegion)
            setImage((TextureRegion) drawable);
    }

    @Override
    public void act(float delta) {
        spriteAnimation.act(delta);

        if (spriteAnimation.getCurrentFrameNumber() ==
         1) {
            spriteAnimation.setPlayMode(PlayMode.LOOP);
        } else if (spriteAnimation.getCurrentFrameNumber() ==
         spriteAnimation.getFrameNumber()-1) {
            spriteAnimation.setPlayMode(PlayMode.LOOP_REVERSED);
        }
        super.act(delta);
    }

    @Override
    public float getFadeDuration() {
        return spriteAnimation.getFrameDuration();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setImage((spriteAnimation.getCurrentFrame()));
        if (blending != null) {
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).setBlending(blending);
            }
        }
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
        //        debug();

    }

    @Override
    public void setSize(float width, float height) {
        //        super.setSize(width, height);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
    }

    public BLENDING getBlending() {
        return blending;
    }

    public void setBlending(BLENDING blending) {
        this.blending = blending;
    }
}
