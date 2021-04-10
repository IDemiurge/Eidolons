package main.level_editor.gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import libgdx.bf.generic.FadeImageContainer;
import eidolons.system.libgdx.datasource.ScreenData;
import libgdx.screens.ScreenWithLoader;
import main.system.EventCallbackParam;

public class LE_WaitingScreen extends ScreenWithLoader {
    private static LE_WaitingScreen instance;
    FadeImageContainer background;
    Stage stage;

    private LE_WaitingScreen() {
        stage = new Stage();
    }

    public static LE_WaitingScreen getInstance() {
        main.system.auxiliary.log.LogMaster.log(1,"getInstance " );
        if (instance == null) {
            main.system.auxiliary.log.LogMaster.log(1,"create Instance " );
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

    protected boolean isWaitForInputSupported() {
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
