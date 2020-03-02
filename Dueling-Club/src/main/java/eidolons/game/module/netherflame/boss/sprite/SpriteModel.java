package eidolons.game.module.netherflame.boss.sprite;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.game.module.netherflame.boss.anim.BossAnimator;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.gui.generic.GroupX;

import java.util.HashMap;
import java.util.Map;

/**
 * controls the main SpriteAnimation for the boss
 * <p>
 * shaders
 * overlay sprites
 * color/alpha
 * speed
 * substitute sprites
 */
public class SpriteModel extends GroupX {

    SpriteAnimation displayedSprite;
    SpriteAnimation defaultSprite;
    SpriteAnimation previousSprite;
    Map<BossAnimator.BossSpriteVariant, SpriteAnimation> variants = new HashMap<>();
    Vector2 pos;
    private BossAnimator.BossSpriteVariant variant;
    String name;
    private float fadePercentage;
    private float speed = 1;


    public SpriteModel(SpriteAnimation displayedSprite, String name) {
        this.displayedSprite = displayedSprite;
        this.defaultSprite = displayedSprite;
        previousSprite = displayedSprite;
        variants.put(BossAnimator.BossSpriteVariant.DEFAULT, defaultSprite);
        defaultSprite.setLooping(true);
        this.name = name;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        displayedSprite.setBlending(null  );
        defaultSprite.setLooping(true);
        if (pos != null) {
            displayedSprite.setOffsetY(pos.y);
            displayedSprite.setOffsetX(pos.x);
            previousSprite.setOffsetY(pos.y);
            previousSprite.setOffsetX(pos.x);
        }
        displayedSprite.draw(batch);
        if (previousSprite != displayedSprite) {
            if (previousSprite != null) {
                previousSprite.draw(batch);
            }
        }

        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (displayedSprite.isAnimationFinished()) {
            setVariant(BossAnimator.BossSpriteVariant.DEFAULT);
        }
        if (variant != null) {
            SpriteAnimation sprite = variants.get(variant);
            if (sprite == null) {
                sprite = SpriteAnimationFactory.createSpriteVariant(name, variant);
                //TODO boss fix
                sprite.setLooping(variant == BossAnimator.BossSpriteVariant.DEFAULT);

                variants.put(variant, sprite);
            }
            setSprite(sprite);
            displayedSprite = sprite;
        } else {
            setSprite(null); //hide
        }

        if (previousSprite!=displayedSprite)
        if (fadePercentage > 0) {
            fadePercentage = Math.max(0, fadePercentage - delta / getFadeDuration());
            previousSprite.setAlpha(fadePercentage);
            displayedSprite.setAlpha(1 - fadePercentage);
        }
        if (speed == 0)
            speed = 1;
        displayedSprite.setSpeed(speed);
        super.act(delta);
    }

    private float getFadeDuration() {
        return 2f;
    }

    private void setSprite(SpriteAnimation sprite) {
        if (displayedSprite == sprite)
            return;
        if (sprite == null) {
            //hide
        } else {
            previousSprite = displayedSprite;
            displayedSprite = sprite;
        }
//        if (isFade())
        fadePercentage = 1f;
    }

    public void hide() {
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setVariant(BossAnimator.BossSpriteVariant variant) {
        this.variant = variant;
    }

    public BossAnimator.BossSpriteVariant getVariant() {
        return variant;
    }
}
