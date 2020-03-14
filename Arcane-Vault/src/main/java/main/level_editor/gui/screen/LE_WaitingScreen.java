package main.level_editor.gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenWithLoader;
import main.system.EventCallbackParam;

public class LE_WaitingScreen extends ScreenWithLoader {
    private static LE_WaitingScreen instance;
    FadeImageContainer background;
    Stage stage;

    public static LE_WaitingScreen getInstance() {
        if (instance == null) {
            instance = new LE_WaitingScreen();
        }
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    protected void preLoad() {
        //random bg
//        initLabels();
    }

    @Override
    public void initLoadingStage(ScreenData meta) {
        super.initLoadingStage(meta);
        stage = new Stage();
//        background = new FadeImageContainer();
//        stage.addActor(background);
//        background.setImage(DungeonEnums.MAP_BACKGROUND.BASTION.getBackgroundFilePath());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    protected boolean isWaitForInput() {
        return false;
    }

    @Override
    protected void afterLoad() {
    }

    @Override
    public void loadingAssetsDone(EventCallbackParam param) {
    }

    //default big buttons in the middle?
}
