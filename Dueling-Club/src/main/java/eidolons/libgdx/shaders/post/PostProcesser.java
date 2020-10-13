package eidolons.libgdx.shaders.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.shaders.blur.Blur;

/**
 * Created by JustMe on 12/3/2018.
 */
public class PostProcesser {

    private static final float WORLD_TO_SCREEN = 0.01f;
    private final OrthographicCamera camera;
    SpriteBatch batch;
    Processor active;
    Blur blur;
    private final FrameBuffer fboA;
    //really multiple?! well in truth that would be cool... give it a shot
    private final FrameBuffer fboB;

    public PostProcesser(SpriteBatch batch) {
        this.batch = batch;

        int VIRTUAL_WIDTH = GdxMaster.getWidth();
        int VIRTUAL_HEIGHT = GdxMaster.getHeight();
        float SCENE_WIDTH = VIRTUAL_WIDTH / 100;
        float SCENE_HEIGHT = VIRTUAL_HEIGHT / 100f;
        camera = new OrthographicCamera();
        FitViewport viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
        camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
        fboA = FrameBuffer.createFrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
        fboB = FrameBuffer.createFrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
        //        camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
        blur = new Blur();
        active = blur;
    }

    public void activate() {
        //draw it all to fbo, edit fbo, draw back to batch
        fboA.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //should be done in the renderer itself...

    }

    public void apply(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

//        batch.flush();
        fboA.end();
        // Horizontal blur from FBO A to FBO B
        fboB.begin();
        batch.begin();
        active.prepareForFBO(batch, delta);
//        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawTexture(fboA.getColorBufferTexture(), 0.0f, 0.0f);
        batch.flush();
        fboB.end();

        // Vertical blur from FBO B to the screen

        active.prepareForBatch(batch);
        drawTexture(fboB.getColorBufferTexture(), 0.0f, 0.0f);
        batch.flush();
        batch.end();

    }

    private void drawTexture(Texture texture, float x, float y) {
        int width = texture.getWidth();
        int height = texture.getHeight();

        batch.draw(texture,
         x, y,
         0.0f, 0.0f,
         width, height,
         WORLD_TO_SCREEN, WORLD_TO_SCREEN,
         0.0f,
         0, 0,
         width, height,
         false, false);
    }
}
