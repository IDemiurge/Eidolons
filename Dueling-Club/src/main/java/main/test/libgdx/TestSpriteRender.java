package main.test.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import main.data.filesys.PathFinder;

import static com.badlogic.gdx.Gdx.input;

/**
 * Created with IntelliJ IDEA.
 * Date: 01.11.2016
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class TestSpriteRender implements ApplicationListener {

    static final int WIDTH = 480;
    static final int HEIGHT = 320;
    Sprite sprite;
    Rectangle glViewport;
    OrthographicCamera cam;
    SpriteBatch spriteBatch;
    private Texture cellTexture;

    public static void main(String[] args) {
/*
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(x,y,w,h);
        ScissorStack.calculateScissors(camera, spriteBatch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        spriteBatch.draw(...);
        spriteBatch.flush();
        ScissorStack.popScissors();
*/

        LwjglApplicationConfiguration lwjglApplicationConfiguration = new LwjglApplicationConfiguration();
        lwjglApplicationConfiguration.title = "demo";
        lwjglApplicationConfiguration.useGL30 = true;

/*        lwjglApplicationConfiguration.width = 1920;
        lwjglApplicationConfiguration.height = 1080;*/

        lwjglApplicationConfiguration.width = 1600;
        lwjglApplicationConfiguration.height = 900;
        lwjglApplicationConfiguration.fullscreen = false;


        TestSpriteRender r = new TestSpriteRender();
        new LwjglApplication(r, lwjglApplicationConfiguration);
    }

    @Override
    public void create() {
        cellTexture = new Texture(PathFinder.getImagePath() + "\\UI\\cells\\Empty Cell v3.png");

        sprite = new Sprite(cellTexture);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1600, 900);

        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        if (input.isKeyPressed(Input.Keys.LEFT)) {
            cam.translate(-20, 0);
        }
        if (input.isKeyPressed(Input.Keys.RIGHT)) {
            cam.translate(20, 0);
        }
        if (input.isKeyPressed(Input.Keys.UP)) {
            cam.translate(0, 20);
        }
        if (input.isKeyPressed(Input.Keys.DOWN)) {
            cam.translate(0, -20);
        }
        cam.update();
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        sprite.draw(spriteBatch, 1);
        spriteBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
