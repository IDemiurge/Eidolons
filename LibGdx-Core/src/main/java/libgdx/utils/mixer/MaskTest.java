package libgdx.utils.mixer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class MaskTest implements ApplicationListener {

    public static boolean spriteMaskTest=false;
    OrthographicCamera cam;
    SpriteBatch batch;
    Texture bg, sprite, alphaMask;

    @Override
    public void create () {
        // if (spriteMaskTest) {
        //     SpriteAnimation mask = SpriteAnimationFactory.getSpriteAnimation(Sprites.INK_BLOTCH);
        //     gridStage.addActor(masked=new SpriteMask(mask, true, null ));
        //     masked.addActor(gridPanel);
        // }
        cam = new OrthographicCamera();
        batch = new SpriteBatch();

        sprite = new Texture("data/grass.png");
        alphaMask = new Texture("data/mask.png");
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

        //... draw background entities/tiles here ...

        //flush the batch to the GPU
        batch.flush();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        //start the batch
        batch.begin();

        //draw background
        drawBackground(batch);


        //the sprite we want the circle mask applied to
        int x = 25;
        int y = 50;
        int spriteWidth = 200;
        int spriteHeight = 200;


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
