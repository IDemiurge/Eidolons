package main.libgdx.screens;

import com.badlogic.gdx.ScreenAdapter;
import main.libgdx.stage.LoadingStage;

public class ScreenWithLoader extends ScreenAdapter {
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
        if (!hideLoader) {
            loadingStage.act(delta);
            loadingStage.draw();
        }
    }
}
