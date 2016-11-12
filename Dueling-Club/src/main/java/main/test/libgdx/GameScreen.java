package main.test.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import main.data.filesys.PathFinder;
import main.libgdx.*;

import static com.badlogic.gdx.Gdx.input;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen implements Screen {

    private DC_GDX_Background background;
    private DC_GDX_TopPanel topPanel;
    private DC_GDX_GridPanel gridPanel;
    private DC_GDX_TargetUnitInfoPanel unitInfoPanel;
    private DC_GDX_ActiveUnitInfoPanel activeUnitInfoPanel;
    private DC_GDX_ActionGroup actionGroup;

    private SpriteBatch batch;
    private OrthographicCamera cam;

    public GameScreen PostConstruct() {
        cam = new OrthographicCamera();
        MyInputController controller = new MyInputController(cam);
        cam.setToOrtho(false, 1600, 900);
/*        GL30 gl = Gdx.graphics.getGL30();
        gl.glEnable(GL30.GL_BLEND);
        gl.glEnable(GL30.GL_TEXTURE_2D);
        gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);*/
        batch = new SpriteBatch();
        PathFinder.init();
        background = new DC_GDX_Background(PathFinder.getImagePath()).init();
        topPanel = new DC_GDX_TopPanel(PathFinder.getImagePath()).init();
        gridPanel = new DC_GDX_GridPanel(PathFinder.getImagePath(), 100, 100).init();

        unitInfoPanel = new DC_GDX_TargetUnitInfoPanel(PathFinder.getImagePath()).init();
        unitInfoPanel.setX(Gdx.graphics.getWidth() - unitInfoPanel.getWidth());
        unitInfoPanel.setY(Gdx.graphics.getHeight() - unitInfoPanel.getHeight());

        activeUnitInfoPanel = new DC_GDX_ActiveUnitInfoPanel(PathFinder.getImagePath()).init();
        activeUnitInfoPanel.setX(0);
        activeUnitInfoPanel.setY(Gdx.graphics.getHeight() - activeUnitInfoPanel.getHeight());

        actionGroup = new DC_GDX_ActionGroup(PathFinder.getImagePath()).init();
        actionGroup.setY(10);
        actionGroup.setX(Gdx.graphics.getWidth() / 2 - actionGroup.getWidth() / 2);

        //gridPanel.setY(actionGroup.getY()+actionGroup.getHeight());
        //gridPanel.setX(activeUnitInfoPanel.getMinWeight());
        Gdx.input.setInputProcessor(controller);
        return this;
    }

    @Override
    public void render(float delta) {
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

        batch.begin();
        background.draw(batch, 1);
        batch.end();

        SpriteBatch batch2 = new SpriteBatch();
        batch2.setProjectionMatrix(cam.combined);
        batch2.begin();
        gridPanel.draw(batch2, 1);
        batch2.end();

        batch.begin();
        //background.draw(batch, 1);
        //gridPanel.draw(batch, 1);
        topPanel.draw(batch, 1);
        unitInfoPanel.draw(batch, 1);
        activeUnitInfoPanel.draw(batch, 1);
        actionGroup.draw(batch, 1);
        batch.end();



        actionGroup.hit(1, 1, true);
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
