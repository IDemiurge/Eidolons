package main.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import main.libgdx.stage.LoadingStage;

public abstract class ScreenWithLoader extends ScreenAdapter {
    protected final LoadingStage loadingStage;
    protected boolean hideLoader = false;

    public ScreenWithLoader() {
        this.loadingStage = new LoadingStage();
    }

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
}
