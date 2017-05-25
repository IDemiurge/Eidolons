package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.libgdx.stage.LoadingStage;
import main.system.GuiEventManager;

import static main.system.GuiEventType.SCREEN_LOADED;

public abstract class ScreenWithLoader extends ScreenAdapter {
    protected final LoadingStage loadingStage;
    protected boolean hideLoader = false;
    protected ScreenData data;
    protected ScreenViewport viewPort;

    public ScreenWithLoader() {
        this.loadingStage = new LoadingStage();
        GuiEventManager.bind(SCREEN_LOADED, (param) -> {
            data.setParam(param);
            this.hideLoader();
            afterLoad();
        });
    }

    protected abstract void preLoad();
    protected abstract void afterLoad();

    public void hideLoader() {
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
    }

    public void setData(ScreenData data) {
        this.data = data;
        preLoad();
    }

    public void setViewPort(ScreenViewport viewPort) {
        this.viewPort = viewPort;
        loadingStage.setViewport(viewPort);
    }
}
