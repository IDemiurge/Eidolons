package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
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
        spriteAnimation.setFrameDuration(0.5f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public float getFadeDuration() {
        return spriteAnimation.getFrameDuration();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        spriteAnimation.draw(batch);
        setImage((spriteAnimation.getCurrentFrame()));
        if (blending != null) {
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).setBlending(blending);
            }
        }
        super.draw(batch, parentAlpha);
        debug();

    }

    public void setBlending(BLENDING blending) {
        this.blending = blending;
    }

    public BLENDING getBlending() {
        return blending;
    }
}
