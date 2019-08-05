package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.screens.CustomSpriteBatch;
import main.system.threading.WaitMaster;

public class FullscreenAnimation extends SuperActor {

    SpriteAnimation sprite;
    private float alpha;
    boolean loop;
    String path;

    public FullscreenAnimation(boolean loop, String path) {
        this.loop = loop;
        this.path = path;
        initSprite(path);
    }

    public FullscreenAnimation(boolean loop) {
        this.loop = loop;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (sprite != null) {
            sprite.act(delta);
            if (!loop && sprite.isAnimationFinished())
                finished();
        }
    }

    private void finished() {
        WaitMaster.receiveInput(getWaitOperation(), true);
    }

    private WaitMaster.WAIT_OPERATIONS getWaitOperation() {
        return WaitMaster.WAIT_OPERATIONS.ANIMATION_FINISHED;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        if (batch instanceof CustomSpriteBatch) {
//            ((CustomSpriteBatch) batch).setBlending(SuperActor.BLENDING.SCREEN);
//        }
        if (sprite != null) {
            sprite.setAlpha(getColor().a*alpha);
            sprite.draw(batch);
        }

//        if (batch instanceof CustomSpriteBatch) {
//            ((CustomSpriteBatch) batch).resetBlending();
//        }
    }

    public void initSprite(String path) {
        sprite = SpriteAnimationFactory.getSpriteAnimation(path, false);
        if (sprite == null) {
            return;
        }
        sprite.setAlpha(alpha);
        sprite.setOffsetX(GdxMaster.getWidth() / 2); //center...
        sprite.setOffsetY(GdxMaster.getHeight() / 2);
        sprite.setCustomAct(true);
        sprite.setFrameDuration(0.07f);
        sprite.setPlayMode(loop? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
    }

    @Override
    public Color getColor() {
        return getSprite().getColor();
    }

    @Override
    public void setColor(Color color) {
        getSprite().setColor(color);
    }

    public void setPlayMode(Animation.PlayMode playMode) {
        getSprite().setPlayMode(playMode);
    }

    public void setFrameDuration(float frameDuration) {
        getSprite().setFrameDuration(frameDuration);
    }

    public SpriteAnimation getSprite() {
        return sprite;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float v) {
        alpha = v;
    }
}
