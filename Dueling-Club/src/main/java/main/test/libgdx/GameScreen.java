package main.test.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.data.filesys.PathFinder;
import main.libgdx.*;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created with IntelliJ IDEA.
 * Date: 21.10.2016
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class GameScreen implements Screen {
    private Stage bf;
    private DC_GDX_Background background;
    private DC_GDX_TopPanel topPanel;
    private DC_GDX_GridPanel gridPanel;
    private DC_GDX_TargetUnitInfoPanel unitInfoPanel;
    private DC_GDX_ActiveUnitInfoPanel activeUnitInfoPanel;
    private DC_GDX_ActionGroup actionGroup;

    private SpriteBatch batch;
    private OrthographicCamera cam;

    public GameScreen PostConstruct() {
        bf = new Stage();
        cam = new OrthographicCamera();
        bf.getViewport().setCamera(cam);
        MyInputController controller = new MyInputController(cam);
        cam.setToOrtho(false, 1600, 900);
        GL30 gl = Gdx.graphics.getGL30();
        gl.glEnable(GL30.GL_BLEND);
        gl.glEnable(GL30.GL_TEXTURE_2D);
        gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        batch = new SpriteBatch();
        PathFinder.init();
        background = new DC_GDX_Background(PathFinder.getImagePath()).init();
        topPanel = new DC_GDX_TopPanel(PathFinder.getImagePath()).init();


        TempEventManager.bind("grid-created", new EventCallback() {
            @Override
            public void call(final Object obj) {
                Pair<Integer, Integer> p = ((Pair<Integer, Integer>) obj);
                gridPanel = new DC_GDX_GridPanel(PathFinder.getImagePath(), p.getLeft(), p.getRight()).init();
                bf.addActor(gridPanel);
            }
        });


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
        InputMultiplexer multiplexer = new InputMultiplexer(controller, bf);
        Gdx.input.setInputProcessor(bf);
        return this;
    }


    @Override
    public void render(float delta) {
        TempEventManager.processEvents();

        bf.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cam.update();

        batch.begin();
        background.draw(batch, 1);
        batch.end();

        bf.draw();

        batch.begin();
        //background.draw(batch, 1);
        //gridPanel.draw(batch, 1);
        topPanel.draw(batch, 1);
        unitInfoPanel.draw(batch, 1);
        activeUnitInfoPanel.draw(batch, 1);
        actionGroup.draw(batch, 1);
        batch.end();
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
