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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import main.data.filesys.PathFinder;
import main.system.images.ImageManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Java2DTest implements ApplicationListener {

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Java2DTest";
        cfg.width = 1480;
        cfg.height = 720;

        new LwjglApplication(new Java2DTest(), cfg);
    }

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Java2DTexture j2dTex;
    private TextureRegion sprite;


    private List<java.awt.Shape> shapes = new ArrayList<java.awt.Shape>();
    private int pointer = 0;

    @Override
    public void create() {
        if (Gdx.app.getType()!=ApplicationType.Desktop)
            throw new GdxRuntimeException("this demo only works on desktop with Java2D");

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(w, h);
        camera.setToOrtho(false);
        batch = new SpriteBatch();

        // 1. -- create our Java2D buffer; must be big enough to hold our shape!
        j2dTex = new Java2DTexture(1024, 1024);

        // 2. -- setup our texture region for drawing part of our buffer
        sprite = new TextureRegion(j2dTex);


        PathFinder.init();
        ImageManager.init();
        Image image = ImageManager.getImage(ImageManager.DEFAULT_BACKGROUND);
        show(image);
    }


        void show(Image image) {
        Graphics2D g2d = j2dTex.begin();

        //do whatever want here, e.g. solid fill, strokes, gradient fill

        g2d.drawImage(image
          , 0,0, null );

        //upload data to GL
        j2dTex.end();

        //don't forget to set our texture region up..
        //we use + 1 since Java2D draws outlines on the OUTSIDE
        Rectangle bounds = //shape.getBounds();
         new Rectangle(0, 0,  image.getWidth(null ), image.getHeight(null ));
        sprite.setRegion(bounds.x, bounds.y, bounds.width+1, bounds.height+1);
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
        try {
            batch.draw(TextureManager.toTexture(
             ImageManager.getBufferedImage8bit(      ImageManager.getBufferedImage(
             ImageManager.getImage(ImageManager.DEFAULT_BACKGROUND)))), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
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



    public static class Java2DTexture extends Texture {

        protected BufferedImage bufferImg;
        protected IntBuffer buffer;
        private final Color BACKGROUND = new Color(0, 0, 0, 0);

        public Java2DTexture(int width, int height, Format format) {
            super(width, height, format);
            bufferImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            buffer = BufferUtils.newIntBuffer(width * height);
        }

        public Java2DTexture(int width, int height) {
            this(width, height, Format.RGBA8888);
        }

        public Java2DTexture() {
            this(1024, 1024);
        }

        public BufferedImage getBufferedImage() {
            return bufferImg;
        }

        public Graphics2D begin() {
            //you could probably cache this instead of requesting it every time
            Graphics2D g2d = (Graphics2D) bufferImg.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setBackground(BACKGROUND);
            g2d.clearRect(0, 0, bufferImg.getWidth(), bufferImg.getHeight());
            g2d.setColor(java.awt.Color.white);
            return g2d;
        }

        public void end() {
            // now we pass the BufferedImage pixel data to the LibGDX texture...
            int width = bufferImg.getWidth();
            int height = bufferImg.getHeight();
            //you could probably cache this rather than requesting it every upload
            int[] pixels = ((DataBufferInt)bufferImg.getRaster().getDataBuffer())
             .getData();
            this.bind();
            buffer.rewind();
            buffer.put(pixels);
            buffer.flip();
            Gdx.gl.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height,
             GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        }

    }
}
