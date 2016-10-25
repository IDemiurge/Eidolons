package main.test.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import main.data.filesys.PathFinder;
import main.libgdx.DC_GDX_Background;
import main.libgdx.DC_GDX_TopPanel;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen implements Screen {

    private DC_GDX_Background background;
    private DC_GDX_TopPanel topPanel;

    private SpriteBatch batch;

    public GameScreen PostConstruct() {
        GL30 gl = Gdx.graphics.getGL30();
        gl.glEnable(GL30.GL_BLEND);
        gl.glEnable(GL30.GL_TEXTURE_2D);
        gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        batch = new SpriteBatch();
        PathFinder.init();
        background = new DC_GDX_Background(PathFinder.getImagePath());
        topPanel = new DC_GDX_TopPanel(PathFinder.getImagePath()).init();
        return this;
    }

    @Override
    public void render(float delta) {

        GL30 gl = Gdx.graphics.getGL30();
        gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //batch.setProjectionMatrix(engine.camera.combined);

        //engine.camera.tick();

        //batch.setTransformMatrix(MapView.identityMatrix);

        batch.begin();
        background.draw(batch, 1);
        topPanel.draw(batch, 1);
//        batch.draw(background);
        batch.end();

        //batch.setTransformMatrix(MapView.isoTransform);
    }

    @Override
    public void resize(int width, int height) {
        float camWidth = width;
        float camHeight = height;

/*        to disable pixelperfect
        float camWidth = MapView.TILE_WIDTH * 10.0f;
        float camHeight = camWidth * ((float)height / (float)width);
*/

/*        engine.camera = new Camera(camWidth, camHeight);
        engine.camera.update();*/
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

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
