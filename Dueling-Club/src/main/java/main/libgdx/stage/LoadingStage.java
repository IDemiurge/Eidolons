package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class LoadingStage extends Stage {
    private Image loadingImage;
    private float counter = 0;

    public LoadingStage() {
        final TextureRegion loadingTexture = getOrCreateR("UI/loading-wheel-trans_256х256.png");
        loadingImage = new Image(loadingTexture);
        loadingImage.setOrigin(Align.center);
        loadingImage.setPosition(
                Gdx.graphics.getWidth() / 2 - loadingImage.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - loadingImage.getHeight() / 2);

        //loadingTexture.getTexture();
        addActor(loadingImage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        counter += delta;

        if (counter >= 0.05) {
            loadingImage.setRotation(loadingImage.getRotation() + 30);
            counter = 0;
        }
    }

    @Override
    public void draw() {
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }
}
