package libgdx.screens.handlers;

import com.badlogic.gdx.Screen;
import eidolons.content.consts.VisualEnums;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.testenv.TestEnvMGM;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import libgdx.screens.load.MenuLoadScreen;
import main.system.launch.Launch;
import libgdx.Adapter;
import libgdx.gui.dungeon.panels.generic.story.IggBriefScreenOld;
import libgdx.GdxMaster;
import libgdx.assets.Assets;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveScreen;
import libgdx.launch.GenericLauncher;
import eidolons.system.libgdx.datasource.ScreenData;
import libgdx.screens.generic.ScreenWithLoader;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.screens.menu.AnimatedMenuScreen;
import eidolons.system.options.OptionsMaster;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import java.util.function.Supplier;

import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.GuiEventType.SWITCH_SCREEN;
import static main.system.auxiliary.log.LogMaster.important;

public class ScreenLoader {
    private static boolean initRunning;
    private final GenericLauncher genericLauncher;
    public static boolean firstInitDone;

    public ScreenLoader(GenericLauncher genericLauncher) {
        this.genericLauncher = genericLauncher;
        GuiEventManager.bind(SWITCH_SCREEN, this::loadScreen);
        GuiEventManager.bind(SCREEN_LOADED, this::onScreenLoadDone);
    }

    public void screenInit() {
        ScreenData data = new ScreenData(VisualEnums.SCREEN_TYPE.MAIN_MENU, "Loading...");
        if (isFirstLoadingScreenShown()) {
            Assets.preloadMenu();
            genericLauncher.setScreen(new MenuLoadScreen());
            genericLauncher.render();
        } else {
            loadScreen(new EventCallbackParam(data));
        }
    }

    //TODO Gdx Review
    public void engineInit() {
        if (genericLauncher.getOptionsPath() != null) {
            OptionsMaster.setOptionsPath(genericLauncher.getOptionsPath());
        }
        DC_Engine.systemInit();
        DC_Engine.dataInit();
    }

    public void switchScreen(Supplier<ScreenWithLoader> factory, ScreenData meta) {
        if (!ScreenMaster.screenSet(meta.getType()))
            return;
        GdxMaster.setLoadingCursor();
        important("switchScreen " + meta.getType());
        final Screen oldScreen = genericLauncher.getScreen();

        //        oldScreen.getPostProcessing().end();
        final ScreenWithLoader newScreen = factory.get();

        newScreen.setupPostProcessing();
        newScreen.initLoadingStage(meta);
        newScreen.setViewPort(ScreenMaster.getMainViewport());
        newScreen.setData(meta);

        Assets.loadAtlasesForScreen(newScreen, meta.getType());
        genericLauncher.setScreen(newScreen);
        {
            if (oldScreen != null)
                oldScreen.dispose();
        }

        triggerLoaded(meta);
    }

    public void triggerLoaded(ScreenData data) {
        important("triggerLoaded " + data.getName());
        // GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK);

        switch (data.getType()) {
            //TODO refactor THIS
            case DUNGEON:
                if (isSecondInit()){
                    LogMaster.log(1, "*************** Second init attempted!");
                    return;
                }
                setInitRunning(true);
                Core.onThisOrNonGdxThread(() -> {
                    GuiEventManager.trigger(GuiEventType.UPDATE_LOAD_STATUS, "Loading dungeon...");
                    initScenario(data, data.getName() );
                    GuiEventManager.trigger(GuiEventType.UPDATE_LOAD_STATUS, "Dungeon loaded - " + EidolonsGame.lvlPath);
                    firstInitDone = true;
                    setInitRunning(false);
                });
                break;
            case MAIN_MENU:
                setInitRunning(false);
                GuiEventManager.trigger(SCREEN_LOADED,
                        new ScreenData(VisualEnums.SCREEN_TYPE.MAIN_MENU));
                break;
            default:
                GuiEventManager.trigger(SCREEN_LOADED,
                        new ScreenData(data.getType()));
        }
    }

    private boolean isSecondInit() {
        if (!Flags.isMacro()) {
            if (firstInitDone)
                return true;
            if (isInitRunning())
                return true;
        }
        if (DC_Game.game != null ||  (Core.getMainHero() != null)) {
            return true;
        }
        return false;
    }

    public void loadScreen(EventCallbackParam param) {
        ScreenData newMeta = (ScreenData) param.get();
        important(newMeta.getType() + " loadScreen()");
        // GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK);
        if (newMeta != null) {
            switch (newMeta.getType()) {
                case WEAVE:
                    switchScreen(WeaveScreen::getInstance, newMeta);
                    break;
                case DUNGEON:
                    //TODO PITCH FIX - GET INSTANCE!
                    GuiEventManager.trigger(GuiEventType.UPDATE_LOAD_STATUS, "Loading game screen...");
                    switchScreen(DungeonScreen::new, newMeta);
                    Core.setScope(Core.APPLICATION_SCOPE.BATTLE);
                    break;
                case BRIEFING:
                case CINEMATIC:
                    switchScreen(IggBriefScreenOld::new, newMeta);
                    break;
                //TODO macro Review
                // case MAP:
                //     Eidolons.setScope(Eidolons.SCOPE.MAP);
                //     switchScreen(MapScreen::getInstance, newMeta);
                //     if (newMeta.getName() != null)
                //         AdventureInitializer.setScenario(newMeta.getName());
                //     break;
                case PRE_BATTLE:
                    break;
                case MAIN_MENU:
                    Core.setScope(Core.APPLICATION_SCOPE.MENU);
                    switchScreen(AnimatedMenuScreen::new, newMeta);
                    WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.GDX_READY, true);
                    WaitMaster.markAsComplete(WaitMaster.WAIT_OPERATIONS.GDX_READY);
                    break;
            }
        }
    }

    public void onScreenLoadDone(EventCallbackParam param) {
        if (genericLauncher.getScreen() == null) {
            //TODO
        } else {
            ((ScreenWithLoader) genericLauncher.getScreen()).loadDone(param);
        }
    }


    public void initScenario(ScreenData data, String name) {
        LogMaster.log(1, "initScenario for dungeon:" + name);
        Launch.START(Launch.LaunchPhase._8_dc_init);

        Core.setGdxBeansProvider( ()-> new Adapter().createGdxBeans());

        DC_Engine.gameStartInit();
        //how to prevent this from being called twice?

        Launch.START(Launch.LaunchPhase._9_meta_init);
        if (!Core.initScenario(createMetaForScenario(data)) ) {
            Launch.END(Launch.LaunchPhase._8_dc_init);
            Launch.END(Launch.LaunchPhase._9_meta_init);
            setInitRunning(false);
            return; // INIT FAILED or EXITED
        }
        Core.mainGame.getMetaMaster().getGame().initAndStart();
    }

    public MetaGameMaster createMetaForScenario(ScreenData data) {
          return new TestEnvMGM(data.getName());
    }

    public static void setFirstInitDone(boolean firstInitDone) {
        ScreenLoader.firstInitDone = firstInitDone;
    }

    public static boolean isInitRunning() {
        return initRunning;
    }

    public static void setInitRunning(boolean initRunning) {
        ScreenLoader.initRunning = initRunning;
    }

    protected boolean isFirstLoadingScreenShown() {
        return !CoreEngine.TEST_LAUNCH || (!Flags.isIDE());
    }

}