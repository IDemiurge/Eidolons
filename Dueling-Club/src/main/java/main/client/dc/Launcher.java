package main.client.dc;

import main.client.cc.CharacterCreator;
import main.client.cc.HC_Master;
import main.client.cc.gui.MainPanel;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.MainManager.MAIN_MENU_ITEMS;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.meta.arcade.ArenaArcadeMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.game.core.game.Game;
import main.game.module.adventure.MacroManager;
import main.libgdx.launch.DemoLauncher;
import main.libgdx.launch.ScenarioLauncher;
import main.libgdx.launch.Showcase;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.swing.generic.components.G_Panel;
import main.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.windows.G_Frame;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;
import main.system.hotkey.HC_KeyManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Launcher {

    public static final String VERSION = CoreEngine.VERSION;

    public static final String MAIN_TITLE = "Eidolons: Battlecraft";
    public static final String ICON_PATH = "UI\\components\\main\\logo.png";
    // "UI\\Empty3.png";
    public static final String FAST_TEST_PARTY = "Aeridan's Party";
    public static final boolean ILYA_MODE = true;
    public static final boolean BRUTE_AI_MODE = false;
    public static final boolean ITS_ME = true;
    private static final Dimension VIEW_PANEL_SIZE = GuiManager.DEF_DIMENSION;
    private static final String BACKGROUND_IMAGE_1920x1200 = "UI\\components\\main\\bg_1920x1200.png";
    private static final String BACKGROUND_IMAGE_1920x1080 = "UI\\components\\main\\bg_1920x1080.png";
    private static final String BACKGROUND_IMAGE_1680x1050 = "big\\DC\\bg.jpg";
    private static final String[] LAUNCH_OPTIONS = {"Last", "Default", "Test", "Fast Party",
     "New Arcade", "Continue Arcade",};
    public static boolean DEV_MODE = false;
    static MAIN_MENU_ITEMS[] autoPressSequence;
    static String loadPath;
    private static boolean CHOICE_TEST_MODE = false;
    private static boolean HC_TEST_MODE = false;
    private static boolean T3_TEST_MODE = false;
    private static boolean DEBUG_MODE = false;
    private static G_Frame frame;
    private static boolean fullscreen = false;
    private static VIEWS view;
    private static MainMenu mainMenu;
    private static G_Panel viewPanel;
    private static JLabel background;
    private static boolean macroMode = true;

    private static MainManager mainManager;
    private static Map<VIEWS, Component> views = new HashMap<>();
    private static KeyListener dcKeyListener;
    private static HC_KeyManager hcKeyListener;
    private static KeyListener menuKeyListener;
    private static boolean dataInitialized;
    private static boolean running = false;
    private static boolean fastMacroTest = false;
    private static boolean macroView;
    private static boolean superFastMode;
    private static String preset;
    private static GAME_TYPE gameType;
    private static GAME_MODES gameMode;
    private static String LAUNCH_OPTION;
    private static boolean isAutoHC;

    private static void initKeyListeners() {
        // dcKeyListener= new dcKeyListener();
        hcKeyListener = new HC_KeyManager();
    }

    private static final String readLast() {
        return FileManager.readFile(getLastPresetPath());
    }

    public static String getLastPresetPath() {
        return PathFinder.getPrefsPath() + "HC Last Party.txt";
    }

    public static void main(String[] args) {
        GuiManager.init();
        if (args != null) {
            LAUNCH_OPTION = LAUNCH_OPTIONS[0];
        } else {
            int init = DialogMaster.optionChoice("Launch Options", LAUNCH_OPTIONS);
            if (init == -1) {
                return;
            }
            LAUNCH_OPTION = LAUNCH_OPTIONS[init];
        }
        switch (LAUNCH_OPTION) {

            case "New Arcade":
                gameType = GAME_TYPE.ARCADE;
                gameMode = GAME_MODES.ARENA_ARCADE;
                break;
            case "Continue Arcade":
                gameType = GAME_TYPE.ARCADE;
                gameMode = GAME_MODES.ARENA_ARCADE;
                autoPressSequence = new MAIN_MENU_ITEMS[]{MAIN_MENU_ITEMS.CONTINUE_ARCADE,
                 MAIN_MENU_ITEMS.CONTINUE_LAST};
                break;
            case "Last":
                preset = readLast();
                if (preset.isEmpty()) {
                    preset = null;
                }
                break;
            case "Test":
                DEV_MODE = true;
                DEBUG_MODE = true;
                HC_TEST_MODE = true;
                break;
        }

        ItemGenerator.setGenerationOn(!superFastMode);

        if (args != null) {
            fastMacroTest = args.length > 0;
        } else {
            fastMacroTest = false;
        }
        running = true;
        XML_Reader.setMacro(true);
        DC_Engine.systemInit();
        initFullData();
        MetaManager.init();
        initKeyListeners();


//        simulationInit();

        initTopGUI();
        initMainMenu();
        welcome();

        CoreEngine.setLogicTest(false);
        if (isAutoHC)
            initAutoLaunches();

        // CharacterCreator.getPanel().getTabs().select(0);
        // CharacterCreator.getHeroPanel().getMvp().toggleTreeView();
        // HC_Master.goToSkillTree(param)
        // full screen mode launch - guiManager init first then!
        // main menu component init and add
    }

    private static void initAutoLaunches() {
        if (fastMacroTest) {
            PartyHelper.loadParty(FAST_TEST_PARTY);
            MacroManager.newGame();
//            setView(MacroManager.getMacroViewComponent(), VIEWS.MAP);
        } else if (preset != null || HC_TEST_MODE || CHOICE_TEST_MODE) {
            getMainManager().setCurrentItem(MAIN_MENU_ITEMS.PRESET_HERO);
            getMainManager().launchSelection(DC_TYPE.CHARS, StringMaster.PRESET,
             InfoMaster.CHOOSE_HERO);
            if (preset != null) {
                initPreset();
            } else {
                initRandomLaunch();
            }
        } else if (T3_TEST_MODE) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    HC_Master.toggleT3View();
                }
            });
        } else {

        }
        runAutoPressSequence();
        autoSetDebug();
    }

    private static void autoSetDebug() {
        if (ArenaArcadeMaster.isTestMode()) {
            LAUNCH_OPTION = "Test";
        }
        switch (LAUNCH_OPTION) {
            case "New Arcade":
                break;
            case "Continue Arcade":
            case "Last":
            case "Test":
                DEV_MODE = true;
                DEBUG_MODE = true;
                if (Eidolons.game != null) {
                    Eidolons.game.setDebugMode(DEBUG_MODE);
                }
                break;
            // HC_TEST_MODE = true;
        }

    }

    private static void runAutoPressSequence() {
        if (autoPressSequence != null) {
            for (MAIN_MENU_ITEMS item : autoPressSequence) {
                mainManager.itemClicked(item);
            }
        }
    }

    private static void initRandomLaunch() {
        Loop.startLoop(100);
        while (Loop.loopContinues()) {
            try {
                getMainManager().getSequence().selected(RandomWizard.getRandomInt(100));
                // getMainManager().doneSelection();
                break;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    private static void initPreset() {
        for (String typeName : StringMaster.open(preset)) {
            ObjType presetHero = DataManager.getType(typeName, DC_TYPE.CHARS);
            if (getView() != VIEWS.CHOICE) {
                getMainManager().getSequenceMaster().chooseNewMember(PartyHelper.getParty());
            }
            addHero(presetHero);
        }
    }

    private static void addHero(ObjType presetHero) {
        getMainManager().getSequence().getView().getData().add(presetHero);
        int index = getMainManager().getSequence().getView().getData().indexOf(presetHero);
        if (index == -1) {
            LogMaster.log(1, " ");
        }
        getMainManager().getSequence().selected(index);
    }

    private static void welcome() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.DEATH);
    }

    static void exit() {
        System.exit(0);
    }

    private static void simulationInit() {
        Simulation.init();
    }

    private static void setCustomIcon() {
        ImageIcon img = ImageManager.getIcon(ICON_PATH);
        frame.setIconImage(img.getImage());
    }

    public static void setView(Component viewComp, VIEWS newView) {

        view = newView;
        GuiManager.setKeyListener(getKeyListener(newView));
        if (viewComp == null) {
            return;
        }
        if (viewPanel == null) {
            initTopGUI();
        }
        viewPanel.removeAll();

        viewPanel.add(viewComp, "pos 0 0");
        if (view.isBackgroundNeeded()) {
            addBackground();
        }
        viewPanel.revalidate();
        viewPanel.repaint();
        // if (!views.containsKey(view)) //another bullet in my poor leg
        views.put(view, viewComp);

        macroView = newView == VIEWS.MAP || newView == VIEWS.TOWN || newView == VIEWS.DIALOGUE;
        if (Eidolons.getGame() != null)
            if (!macroView && newView != VIEWS.MINI_MAP && newView != VIEWS.DC) {
                Eidolons.getGame().setSimulation(true);
                Eidolons.getGame().setGameMode(GAME_MODES.SIMULATION);
            }
    }

    private static void addBackground() {
        // TODO Auto-generated method stub

    }

    private static void initTopGUI() {
        frame = new G_Frame(MAIN_TITLE, true);
        // setCustomCursor()
        setCustomIcon();
        if (!GuiManager.isWide()) {
            frame.setLayout(new GridLayout());
            viewPanel = new G_Panel();
            viewPanel.setLayout(new GridLayout());
            viewPanel.setSize(
             // TODO
             GuiManager.getScreenSize());
            frame.add(viewPanel, "pos 0 0");
        } else {
            DEV_MODE = false;
            initBackground();
            frame.add(background, "pos 0 0");
            viewPanel = new G_Panel(
             // VISUALS.ROOT_FRAME
            );
            viewPanel.setLayout(new GridLayout()); // otherwise there is this
            // size calc issue? viewPanel.setSize(VIEW_PANEL_SIZE);
            double X = (GuiManager.getScreenWidth() - VIEW_PANEL_SIZE.getWidth()) / 2;
            double Y = (GuiManager.getScreenHeight() - VIEW_PANEL_SIZE.getHeight()) / 2;
            frame.setLayout(new MigLayout());
            G_Panel panel = new G_Panel();
            panel.setPanelSize(GuiManager.getScreenSize());
            panel.setSize(GuiManager.getScreenSize());
            panel.add(viewPanel, "pos " + X + " " + Y);
            panel.setOpaque(true);
            panel.setBackground(ColorManager.BACKGROUND);
            frame.add(panel, "pos 0 0");

            frame.setComponentZOrder(panel, 0);
            frame.setComponentZOrder(background, 1);
        }
        if (fullscreen) {
            GuiManager.setFullscreen(true);
            GuiManager.setWindowToFullscreen(frame);
        } else {
            frame.setSize(GuiManager.getScreenSize());
            // frame.setBackground(Color.black);
        }
        frame.setUndecorated(true);
        frame.setVisible(true);

    }

    private static void setCustomCursor() {
        Image image = ImageManager.getDefaultCursor();
        Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(image,
         new Point(frame.getX(), frame.getY()), "img");
        frame.setCursor(c);
    }

    private static void initBackground() {
        // TODO small or big, then resize or just add, as long as it is bigger
        // than resolution
        String imgName = null;
        switch (GuiManager.getDisplayMode()) {
            case _1920x1200_:
                imgName = BACKGROUND_IMAGE_1920x1200;
                break;
            case _1920x1080_:
                imgName = BACKGROUND_IMAGE_1920x1080;
                break;
            case _1680x1050_:
                imgName = BACKGROUND_IMAGE_1680x1050;
                break;
        }

        ImageIcon picture = ImageManager.getIcon(imgName);

        background = new JLabel(picture);
    }

    public static KeyListener getKeyListener(VIEWS newView) {
        switch (newView) {
            case DC:
                return dcKeyListener;
            case HC:
                return hcKeyListener;
            case MENU:
                return menuKeyListener;
            default:
                break;
        }
        return null;
    }

    public static void resetView(VIEWS newView) {
        if (getView() == VIEWS.DC && (newView == VIEWS.MENU || newView == VIEWS.HC)) {
            exitDC(newView == VIEWS.MENU);
        }
        if (getView() == VIEWS.HC && (newView == VIEWS.MENU)) {
            exitDC(true);
        }
        if (getMainManager().isMacroMode() && (newView == VIEWS.MENU)) {
            getMainManager().setMacroMode(false);
            MacroManager.exitGame();
        }
        Component viewComp = views.get(newView);
        if (viewComp == null) {
            setView(newView);
        } else {
            setView(viewComp, newView);
        }

        if (newView == VIEWS.HC) {
            try {
                PartyHelper.getParty().resetMembers();
                for (MainPanel p : CharacterCreator.getTabPanel().getPanels().values()) {
                    p.refresh();
                    p.getMiddlePanel().resetBuffer();
                }
                CharacterCreator.refreshGUI();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    private static void exitDC(boolean mainMenu) {
        if (Eidolons.game == null) {
            return;
        }
        try {
            Eidolons.game.exit(mainMenu);
        } catch (InterruptedException e) {
            main.system.ExceptionMaster.printStackTrace(e);
            Eidolons.game.getGameLoopThread().interrupt();
        }
        //

    }

    private static void initMainMenu() {
        mainMenu = new MainMenu();
        setMainManager(new MainManager(mainMenu));

        mainMenu.setManager(getMainManager());
        setView(mainMenu, VIEWS.MENU);
        if (!CoreEngine.isArcaneVault() && autoPressSequence == null) {
            getMainManager().refresh();
        }

    }

    public static void initMenu(final MAIN_MENU_ITEMS toSelect) {
        if (mainMenu == null) {
            initMainMenu();
        }
        new Thread(new Runnable() {
            public void run() {
                mainMenu.itemClicked(toSelect);
            }
        }, " thread").start();

    }

    public static void launchHC(boolean arcadeMode, Unit... heroes) {
        Game.game.setSimulation(true);
        if (!isDataInitialized()) {
            initFullData();
            // simulationInit();
        }
        CharacterCreator.setPartyMode(true);
        for (Unit hero : heroes) {
            CharacterCreator.addHero(hero, true);
        }
        setView(CharacterCreator.getTabPanel(), VIEWS.HC);
        CharacterCreator.refreshGUI();
    }

    private static void initFullData() {
        CoreEngine.setMenuScope(false);
        if (isDataInitialized()) {
            return;
        }
        DC_Engine.dataInit();
        setDataInitialized(true);
    }

    public static void launchDC(String partyName) {
        launchDC(partyName, true);
    }

    public static void launchDC() {
        launchDC(PartyHelper.getParty().getName());

    }

    public static String getLoadPath() {
        return loadPath;
    }

    public static void setLoadPath(String path) {
        loadPath = path;
    }

    public static void launchDC(String partyName, boolean forceBattleInit) {
        // initSave(Launcher.getLoadPath());
        /*
         * initObjectString buffs and dynamic params non-bf objects - items,
		 */
        DC_Game game = Eidolons.game;
        try {
            initFullData();

            boolean first = false;
            if (game == null) {
                first = true;
                DC_Engine.gameInit();
            }

            DemoLauncher.main(new String[]{});
            ScreenData data = new ScreenData(ScreenType.BATTLE, "Loading...");
            GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
            Eidolons.initScenario(new ScenarioMetaMaster(ScenarioLauncher.CRAWL));
//        Eidolons.mainGame.getMetaMaster().getGame().init( );

            try {
                Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

//            GdxLauncher.main(new String[]{});
            game = Simulation.getGame();
            if (game == null) {
                game = new DC_Game(false);
            }
            game.init();
            game.setSimulation(true);
            CharacterCreator.getHeroManager().prebattleCleanSave();
            game.setSimulation(false);
            game.setDebugMode(isDEBUG_MODE_DEFAULT());
            // game.getState().init();

            if (CharacterCreator.isArcadeMode()) {
                PartyHelper.initArcade();
            }

            PartyHelper.getParty().setGame(game);
            if (forceBattleInit || !game.isBattleInit()) {
                game.battleInit();
            }
            game.start(first);

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            game.setSimulation(true);
        }

//        setView(VIEWS.DC);
        try {
            createGame();

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public static void createGame() {
//        WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
//        BattleSceneLauncher battleSceneLauncher = new BattleSceneLauncher();
//        LwjglCanvas canvas = new LwjglCanvas(
//         battleSceneLauncher,
//         battleSceneLauncher.getConf());
//        SwingUtilities.invokeLater(
////        Gdx.app.postRunnable(
//         () -> frame.add(canvas.getCanvas()));
    }

    public static boolean isInMenu() {
        return getView() == VIEWS.MENU;
    }

    public static MainManager getMainManager() {
        return mainManager;
    }

    public static void setMainManager(MainManager mainManager) {
        Launcher.mainManager = mainManager;
    }

    public static boolean isDataInitialized() {
        return dataInitialized;
    }

    public static void setDataInitialized(boolean dataInitialized) {
        Launcher.dataInitialized = dataInitialized;
    }

    public static boolean isRunning() {
        return running;
    }

    public static HC_KeyManager getHcKeyListener() {
        if (hcKeyListener == null) {
            hcKeyListener = new HC_KeyManager();
        }
        return hcKeyListener;
    }

    public static VIEWS getView() {
        return view;
    }

    public static void setView(VIEWS newView) {
        switch (newView) {
            case CHOICE:
                break;
            case DC:
                frame.setVisible(false);
                break;
            case DIALOGUE:
                break;
            case HC:
                frame.setVisible(true);
                break;
            case MAP:
                break;
            case MENU:
                frame.setVisible(true);

                break;
            case TOWN:
                break;
        }

    }

    public static boolean isMacroMode() {
        return macroMode;
    }

    public static void setMacroMode(boolean macroMode) {
        Launcher.macroMode = macroMode;
    }

    public static G_Frame getFrame() {
        return frame;
    }

    public static boolean isDEBUG_MODE_DEFAULT() {
        if (Showcase.isRunning()) return false;
        return DEBUG_MODE || DEV_MODE;
    }

    public static void setDEBUG_MODE(boolean dEBUG_MODE) {
        DEBUG_MODE = dEBUG_MODE;
    }

    public static boolean isSuperFastMode() {
        return superFastMode;
    }

    /*
     *
     */
    public enum VIEWS {
        MENU(true),
        HC(false),
        DC(false),
        CHOICE(true),
        MAP(false),
        TOWN(false),
        DIALOGUE(false),
        MINI_MAP(false),
        T3(false),;
        // only if wide?
        private boolean bg;

        VIEWS(boolean bg) {
            this.bg = bg;
        }

        public boolean isBackgroundNeeded() {
            return bg;
        } // precombat, pre-hc,...
    }

}
