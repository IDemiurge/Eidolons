package main.level_editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.screens.ScreenWithLoader;
import main.data.filesys.PathFinder;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.level_editor.gui.screen.LE_WaitingScreen;

import java.util.function.Supplier;

public class EditorApp extends GenericLauncher {
    private final String[] args;

    public EditorApp(String[] args) {
        super();
        this.args = args;
    }

    @Override
    protected void screenSwitcher(ScreenData newMeta) {
        switch (newMeta.getType()) {
            case EDITOR_WELCOME:
                switchScreen(LE_WaitingScreen::new, newMeta);
                break;
            case EDITOR:
                Supplier<ScreenWithLoader> fac = LE_Screen.getScreen((Floor) newMeta.getParameter());

                fac.get().initLoadingStage(newMeta);
                fac.get().setViewPort(viewport);
                ScreenMaster.screenSet(newMeta.getType());
                fac.get().setData(newMeta);
                setScreen(fac.get());
                //                switchScreen(LE_Screen.getScreen((Floor) newMeta.getParameter()), newMeta);
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewport == null)
            return;
        viewport.update(width, height);
    }

    @Override
    public void start() {
        super.start();
//        screenInit();
    }

    @Override
    public void create() {
        GDX.loadVisUI();
        super.create();
    }

    protected void screenInit() {
//        if (args.length> 0 ){
//            // ???
//        } else
        {
            LE_WaitingScreen newScreen = new LE_WaitingScreen();
            ScreenData meta = new ScreenData(SCREEN_TYPE.EDITOR_WELCOME, "Welcome!");
            newScreen.initLoadingStage(meta);
            setScreen(newScreen);
        }
        load();

        render();
    }

    private void load() {
        Eidolons.onNonGdxThread(() -> {
            DC_Engine.dataInit();
//            DC_Engine.dataInit(true);
            LevelEditor.welcome(args.length == 0 ? null : args[0]);
        });
    }

    @Override
    protected boolean isStopOnInactive() {
        return true;
    }

    @Override
    public LwjglApplicationConfiguration getConf() {
        LwjglApplicationConfiguration c = super.getConf();
        if (LevelEditor.TEST_MODE) {
            c.width = 1;
            c.height = 1;
        }
        return c;
    }

    @Override
    public String getOptionsPath() {
        return PathFinder.getXML_PATH() + "options/editor.xml";
    }
}