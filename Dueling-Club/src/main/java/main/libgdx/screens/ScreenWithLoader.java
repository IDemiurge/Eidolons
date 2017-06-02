package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.libgdx.stage.ChainedStage;
import main.libgdx.stage.LoadingStage;
import main.system.EventCallbackParam;

public abstract class ScreenWithLoader extends ScreenAdapter {
    protected final LoadingStage loadingStage;
    protected boolean hideLoader = false;
    protected ScreenData data;
    protected ScreenViewport viewPort;
    protected ChainedStage introStage;

    public ScreenWithLoader() {
        this.loadingStage = new LoadingStage();
    }

    protected void preLoad() {
        if (data.getDialogScenarios().size() > 0) {
            introStage = new ChainedStage(data.getDialogScenarios());
            introStage.setViewport(viewPort);
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
    }

    protected abstract void afterLoad();

    protected void hideLoader() {
        this.hideLoader = true;
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
}
