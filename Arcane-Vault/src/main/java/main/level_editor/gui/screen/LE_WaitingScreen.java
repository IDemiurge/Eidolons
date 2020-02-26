package main.level_editor.gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import eidolons.libgdx.GDX;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenWithLoader;
import main.content.enums.DungeonEnums;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class LE_WaitingScreen extends ScreenWithLoader {
    FileChooser fileChooser;
    FadeImageContainer background;
    Stage stage;

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
        GuiEventManager.bind(GuiEventType.LE_CHOOSE_FILE , p-> chooseFile());
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

    private void chooseFile() {
        if (fileChooser == null) {
            initFileChooser();
        }
    }

    private void initFileChooser() {
//        FileChooser.setFavoritesPrefsName("com.your.package.here.filechooser");
        Gdx.input.setInputProcessor(stage);
        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected (Array<FileHandle> file) {
                if (file.size==0) {
                    return;
                }
                WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.SELECTION, file.get(0).file().getAbsolutePath());
                fileChooser.fadeOut();
            }
        });
        stage.addActor(fileChooser.fadeIn());

    }
    //default big buttons in the middle?
}
