package gdx.general;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.GdxMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.screens.handlers.ScreenMaster;
import main.system.auxiliary.data.FileManager;

public class ABackground {
    Batch batch;
    protected String backgroundPath;
    protected String previousBg;
    protected TextureRegion backTexture;
    protected FadeImageContainer background;
    protected SpriteAnimation backgroundSprite;

    public ABackground(Batch batch) {
        this.batch = batch;
        background = new FadeImageContainer();
    }

    public void setBackgroundPath(String backgroundPath) {
        this.previousBg = this.backgroundPath;
        this.backgroundPath = backgroundPath;
        if (FileManager.isImageFile(backgroundPath)) {
            background.setImage(backgroundPath);
        } else {
            //TODO
        }
        AScreen.setWidth(background.getWidth());
        AScreen.setHeight(background.getHeight());
//        Geom2D
    }

    protected void draw(float delta) {
        updateBackground(delta);
        batch.begin();
        if (backgroundSprite == null && background != null) {
            background.act(delta);
            //scale?
//            GdxMaster.center(background);
            background.draw(batch, 1f);
        } else if (backTexture != null) {
            float colorBits = GdxColorMaster.WHITE.toFloatBits();
            if (batch.getColor().toFloatBits() != colorBits)
                batch.setColor(colorBits); //gotta reset the alpha... if (backTexture == null)
            if (backgroundSprite != null) {
                drawSpriteBg(batch);
            } else if (isCenteredBackground()) {
                int w = backTexture.getRegionWidth();
                int h = backTexture.getRegionHeight();
                int x = (GdxMaster.getWidth() - w) / 2;
                int y = (GdxMaster.getHeight() - h) / 2;
                batch.draw(backTexture, x, y, w, h);
            } else {
                //TODO max
                batch.draw(backTexture, 0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
            }

        }
        batch.end();

    }

    private boolean isCenteredBackground() {
        if (backgroundSprite != null) {
            return true;
        }
        return !ScreenMaster.isFullscreen();
    }

    private void drawSpriteBg(Batch batch) {
        backgroundSprite.setOffsetY(
                Gdx.graphics.getHeight() / 2);
        backgroundSprite.setOffsetX(Gdx.graphics.getWidth() / 2);
        backgroundSprite.setSpeed(0.5f);
        // backgroundSprite.setOffsetY(getCam().position.y);
        // backgroundSprite.setOffsetX(getCam().position.x);
        backgroundSprite.draw(batch);
    }

    private void updateBackground(float delta) {
        if (backgroundSprite != null) {
            backgroundSprite.act(delta);
            backTexture = backgroundSprite.getCurrentFrame();
            if (backgroundSprite.getCurrentFrameNumber() == backgroundSprite.getFrameNumber() - 1) {
                if (backgroundSprite.getPlayMode() == Animation.PlayMode.LOOP_REVERSED)
                    backgroundSprite.setPlayMode(Animation.PlayMode.LOOP);
                else {
                    backgroundSprite.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
                }
            }
            backTexture = backgroundSprite.getCurrentFrame();

        }
    }
}
