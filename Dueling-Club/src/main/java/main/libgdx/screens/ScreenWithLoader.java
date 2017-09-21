package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.libgdx.stage.ChainedStage;
import main.libgdx.stage.LoadingStage;
import main.system.EventCallbackParam;

public abstract class ScreenWithLoader extends ScreenAdapter {
    protected   LoadingStage loadingStage;
    protected boolean hideLoader = false;
    protected ScreenData data;
    protected ScreenViewport viewPort;
    protected ChainedStage introStage;
    Batch batch;

    public ScreenWithLoader() {
        //TODO loader here, but need data!
    }

    public Batch getBatch() {
        if (batch==null ){
            batch = new SpriteBatch();
        }
        return batch;
    }

    protected void preLoad() {
        if (data.getDialogScenarios().size() > 0) {
            introStage = new ChainedStage(viewPort, getBatch(), data.getDialogScenarios());
            introStage.setOnDoneCallback(() -> {
                if (hideLoader) {
                    updateInputController();
                }
            });
        }
    }

    public void loadDone(EventCallbackParam param) {
        data.setParam(param);
        this.hideLoader();
        afterLoad();
        updateInputController();
        initCursor();
    }

    private void initCursor() {
        Gdx.graphics.setSystemCursor(SystemCursor.Ibeam);
    }

    private void initLoadingCursor() {

//         cursor = Gdx.graphics.newCursor(myPixmap, 0, 0);
//        Gdx.graphics.setCursor(cursor);
        Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);
//        cursor.dispose();
    }
    protected abstract void afterLoad();

    protected void hideLoader() {
        this.hideLoader = true;
        loadingStage.done();
        if (introStage!=null )
        if (introStage.isDone()) {
            updateInputController();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if (introStage != null && !introStage.isDone()) {
            introStage.act(delta);
            introStage.draw();
        } else if (!hideLoader) {
            loadingStage.act(delta);
            loadingStage.draw();
        }
    }

    protected boolean canShowScreen() {
        boolean isIntroFinished = true;
        if (introStage != null && !introStage.isDone()) {
            isIntroFinished = false;
        }

        return hideLoader && isIntroFinished;
    }

    public void setData(ScreenData data) {
        this.data = data;
        preLoad();
        updateInputController();
    }

    public void setViewPort(ScreenViewport viewPort) {
        this.viewPort = viewPort;
        loadingStage.setViewport(viewPort);
    }

    protected InputMultiplexer getInputController() {
        return introStage != null ?
                new InputMultiplexer(loadingStage, introStage) :
                new InputMultiplexer(loadingStage);
    }

    protected void updateInputController() {
        Gdx.input.setInputProcessor(getInputController());
    }

    public void initLoadingStage(ScreenData meta) {
        this.loadingStage = new LoadingStage(meta );
        initLoadingCursor();
    }

}
