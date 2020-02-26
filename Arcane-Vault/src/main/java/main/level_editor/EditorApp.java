package main.level_editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.screens.LoadingScreen;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.OptionsMaster;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.level_editor.gui.screen.LE_Screen;
import main.level_editor.gui.screen.LE_WaitingScreen;
import main.level_editor.sim.LE_MetaMaster;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class EditorApp extends GenericLauncher {
    private static final boolean TEST_MODE = true;
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
                switchScreen(LE_Screen.getScreen((Floor) newMeta.getParameter()), newMeta);
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
        if (TEST_MODE) {
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
