package main.libgdx.texture;

/**
 * Created by JustMe on 1/5/2017.
 */

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import main.libgdx.GdxMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Java2DTest implements ApplicationListener {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture2D j2dTex;
    private TextureRegion sprite;
    private List<java.awt.Shape> shapes = new ArrayList<>();
    private int pointer = 0;

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Java2DTest";
        cfg.width = 1480;
        cfg.height = 720;

        new LwjglApplication(new Java2DTest(), cfg);
    }

    @Override
    public void create() {
        if (Gdx.app.getType() != ApplicationType.Desktop) {
            throw new GdxRuntimeException("this demo only works on desktop with Java2D");
        }

        float w = GdxMaster.getWidth();
        float h = GdxMaster.getHeight();

        camera = new OrthographicCamera(w, h);
        camera.setToOrtho(false);
        batch = new SpriteBatch();

        // 1. -- create our Java2D buffer; must be big enough to hold our shape!
        j2dTex = new Texture2D(1024, 1024);

        // 2. -- setup our texture region for drawing part of our buffer
        sprite = new TextureRegion(j2dTex);


        ImageManager.init();
        Image image = ImageManager.getImage(ImageManager.DEFAULT_BACKGROUND);
        show(image);
    }


    void show(Image image) {
        Graphics2D g2d = j2dTex.begin();

        //do whatever want here, e.g. solid fill, strokes, gradient fill

        g2d.drawImage(image
                , 0, 0, null);

        //upload data to GL
        j2dTex.end();

        //don't forget to set our texture region up..
        //we use + 1 since Java2D draws outlines on the OUTSIDE
        Rectangle bounds = //shape.getBounds();
                new Rectangle(0, 0, image.getWidth(null), image.getHeight(null));
        sprite.setRegion(bounds.x, bounds.y, bounds.width + 1, bounds.height + 1);
    }

    @Override
    public void dispose() {
        batch.dispose();
        j2dTex.dispose();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
//        batch.draw(sprite, 10, 10);
//        try {
//            batch.draw(TextureManager.toTexture(
//             ImageManager.getBufferedImage8bit(      ImageManager.getBufferedImage(
//             ImageManager.getEmitterPath(ImageManager.DEFAULT_BACKGROUND)))), 0, 0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }


}
