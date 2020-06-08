package eidolons.libgdx.utils.mixer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import eidolons.libgdx.texture.Images;
import main.data.filesys.PathFinder;


public class MaskTest2 implements ApplicationListener {

    private static final float HEIGHT = 1080;
    private static final float WIDTH = 1920;
    OrthographicCamera cam;
    SpriteBatch batch;
    Texture bg, sprite, alphaMask;

    @Override
    public void create () {
        cam = new OrthographicCamera();
        batch = new SpriteBatch();

        bg = new Texture(PathFinder.getImagePath()+ Images.DEFEAT);
        sprite = new Texture(PathFinder.getImagePath()+ Images.BG_EIDOLONS);
        alphaMask = new Texture(PathFinder.getImagePath()+ Images.BLOTCH_INVERT);
        alphaMask.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    @Override
    public void resize (int width, int height) {
        cam.setToOrtho(false, width, height);
        batch.setProjectionMatrix(cam.combined);
    }

    private void drawBackground(SpriteBatch batch) {
        //regular blending mode
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        batch.draw(bg, 0, 0, WIDTH, HEIGHT);
        //... draw background entities/tiles here ...


        //flush the batch to the GPU
        batch.flush();
    }

    private void drawAlphaMask(SpriteBatch batch, float x, float y, float width, float height) {
        //disable RGB color, only enable ALPHA to the frame buffer
        Gdx.gl.glColorMask(false, false, false, true);

        //change the blending function for our alpha map
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

        //draw alpha mask sprite(s)
        batch.draw(alphaMask, x, y, width, height);

        //flush the batch to the GPU
        batch.flush();
    }

    private void drawForeground(SpriteBatch batch, int clipX, int clipY, int clipWidth, int clipHeight) {
        //now that the buffer has our alpha, we simply draw the sprite with the mask applied
        Gdx.gl.glColorMask(true, true, true, true);
        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

        //The scissor test is optional, but it depends 
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(clipX, clipY, clipWidth, clipHeight);

        //draw our sprite to be masked
        batch.draw(sprite, 0, 0, 250, 250);

        //remember to flush before changing GL states again
        batch.flush();

        //disable scissor before continuing
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }

    public static void main(String[] args) {
        new LwjglApplication(
        new MaskTest2() ,getConf());
    }
    public static LwjglApplicationConfiguration getConf() {
        //        Eidolons. getApplication().getGraphics().setFullscreenMode();
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.width = (int) WIDTH;
        conf.height = (int) HEIGHT;
        return conf;
    }
    @Override
    public void render () {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //start the batch
        batch.begin();

        //draw background
        drawBackground(batch);


        //the sprite we want the circle mask applied to
        int spriteWidth = 537;
        int x = (int) ((WIDTH-spriteWidth)/2);
        int spriteHeight = 629;
        int y = (int) ((HEIGHT-spriteHeight)/2);
x=0;
y=0;
        //draw the alpha mask
        drawAlphaMask(batch, x, y, spriteWidth, spriteHeight);

        //draw our foreground elements
        drawForeground(batch, x, y, spriteWidth, spriteHeight);

        batch.end();
    }

    @Override
    public void pause () {

    }

    @Override
    public void resume () {

    }

    @Override
    public void dispose () {
        batch.dispose();
        alphaMask.dispose();
        sprite.dispose();
    }


}
