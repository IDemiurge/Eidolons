package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.libgdx.stage.ChainedStage;
import main.libgdx.stage.LoadingStage;
import main.system.GuiEventManager;

import static main.system.GuiEventType.SCREEN_LOADED;

public abstract class ScreenWithLoader extends ScreenAdapter {
    protected final LoadingStage loadingStage;
    protected boolean hideLoader = false;
    protected ScreenData data;
    protected ScreenViewport viewPort;
    protected ChainedStage introStage;

    public ScreenWithLoader() {
        this.loadingStage = new LoadingStage();
        GuiEventManager.bind(SCREEN_LOADED, (param) -> {
            data.setParam(param);
            this.hideLoader();
            afterLoad();
            updateInputController();
        });
    }

    protected void preLoad() {
        if (data.getDialogScenarios().size() > 0) {
            introStage = new ChainedStage(data.getDialogScenarios());
            introStage.setViewport(viewPort);
        }
    }

    protected abstract void afterLoad();

    protected void hideLoader() {
        this.hideLoader = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if (!hideLoader) {
            loadingStage.act(delta);
            loadingStage.draw();
        }

        if (introStage != null && !introStage.isDone()) {
            introStage.act(delta);
            introStage.draw();
        }
    }

    protected boolean canShowScreen() {
        return hideLoader && (introStage != null && introStage.isDone());
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
