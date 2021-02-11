package main.level_editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import eidolons.content.consts.VisualEnums;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import libgdx.GDX;
import libgdx.launch.GenericLauncher;
import libgdx.screens.ScreenData;
import libgdx.screens.ScreenMaster;
import libgdx.screens.ScreenWithLoader;
import libgdx.screens.load.ScreenLoader;
import main.data.filesys.PathFinder;
import main.level_editor.backend.struct.level.LE_Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.level_editor.gui.screen.LE_WaitingScreen;
import main.system.EventCallbackParam;

import java.util.function.Supplier;

public class EditorApp extends GenericLauncher {
    private final String[] args;

    public EditorApp(String[] args) {
        super();
        this.args = args;
    }

    @Override
    protected ScreenLoader createScreenLoader() {
        return new ScreenLoader(this){
            @Override
            public void loadScreen(EventCallbackParam param) {
                ScreenData newMeta = ((ScreenData) param.get());
                switch (newMeta.getType() ) {
                    case EDITOR_WELCOME:
                        switchScreen(LE_WaitingScreen::getInstance, newMeta);
                        break;
                    case EDITOR:
                        Supplier<ScreenWithLoader> fac = LE_Screen.getScreen((LE_Floor) newMeta.getParameter());

                        ScreenWithLoader screen = fac.get();
                        screen.initLoadingStage(newMeta);
                        screen.setViewPort(viewport);
                        ScreenMaster.screenSet(newMeta.getType());
                        setScreen(screen);
                        screen.setData(newMeta);

                        screen.updateInputController();
                        Eidolons.onNonGdxThread(() -> Eidolons.game.getMetaMaster().getDungeonMaster().reinit());
                        //                GuiEventManager.trigger(GuiEventType.LE_TREE_RESET, LevelEditor.getModel());
                        //                switchScreen(LE_Screen.getScreen((Floor) newMeta.getParameter()), newMeta);
                        break;
                }
            }

            public void screenInit() {
                //        if (args.length> 0 ){
                //            // ???
                //        } else
                {
                    LE_WaitingScreen newScreen =LE_WaitingScreen.getInstance();
                    ScreenData meta = new ScreenData(VisualEnums.SCREEN_TYPE.EDITOR_WELCOME, "Welcome!");
                    newScreen.initLoadingStage(meta);
                    setScreen(newScreen);
                }
                load();

                render();
            }
        };
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


    private void load() {
        LE_WaitingScreen.getInstance();

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
        return c;
    }

    @Override
    public String getOptionsPath() {
        return PathFinder.getXML_PATH() + "options/editor.xml";
    }
}
