package main.test.frontend;

import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.data.ability.construct.VariableManager;
import main.game.battlecraft.DC_Engine;
import main.game.core.game.DC_Game;
import main.game.core.launch.PresetLauncher;
import main.game.core.launch.TestLauncher;
import main.game.core.launch.TestLauncher.CODE;
import main.game.core.state.Saver;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.hotkey.GlobalKeys;
import main.system.launch.CoreEngine;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class FAST_DC {
    public static final String[] SKIP_CHOICE_ARGS = new String[]{"", ""};
    public static final String PRESET_ARG = "PRESET";
    public static final String PRESET_OPTION_ARG = "PRESET_OPTION";
    private static final boolean GLOBAL_HOTKEYS_ON = true;
    private static final boolean BEHAVIOR_TEST_ON = false;

    public static String DEFAULT_TEST_DUNGEON = "test\\Clearshot Test.xml";//Clearshot
    public static String DEFAULT_DUNGEON = null;
    public static Boolean FAST_MODE = false;
    public static int ENEMY_CODE = CODE.CHOOSE;
    public static int PARTY_CODE = CODE.CHOOSE; // preset generic-code?
    public static String ENEMY_PARTY = null;//"Minotaur;Troglodyte;Harpy";
    public static String PLAYER_PARTY = null;//""+ "Bandit Archer;";
    public static String objData = "";
    public static String objData2
     = "7-5=Minotaur,8-5=Troglodyte,";
    public static boolean SUPER_FAST_MODE;
    public static String exceptions = "";
    public static boolean forceRunGT = false;
    public static boolean TEST_MODE;
    private static boolean MINIMAP_TEST_ON = false;
    private static boolean VISION_HACK = false;
    private static boolean fullscreen = false;
    private static DC_Game game;
    private static boolean running;
    private static int unitGroupLevel;
    private static TestLauncher launcher;
    private static GdxLauncher guiLauncher;

    public static boolean isRunning() {
        return running;
    }

    //    chars;skills;classes;deities;factions;jewelry; TODO

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-GT")) {
                forceRunGT = true;
            } else if (StringMaster.isInteger(args[0])) {
                PresetLauncher.PRESET_OPTION = StringMaster.getInteger(args[0]);
            } else {
                PresetLauncher.PRESET_LAUNCH = args[0];
            }
        }
        CoreEngine.setExceptionTypes(exceptions);
        running = true;
        DC_Engine.systemInit();
        Launcher.DEV_MODE = true;
        // CoreEngine.setWritingLogFilesOn(true);
        boolean skipChoice = false;


        List<String> arglist = new ArrayList<>(Arrays.asList(args));
        if (args != null) {
            if (args[0].contains(PRESET_OPTION_ARG) ||args == SKIP_CHOICE_ARGS || (arglist.contains(PRESET_OPTION_ARG) ||
             (arglist.contains(PRESET_ARG)))) {
                skipChoice = true;

            }
        }

        launcher =
//         (TEST_MODE)?
         new TestLauncher(game, FAST_MODE, SUPER_FAST_MODE)
//          : new GameLauncher(GAME_SUBCLASS.SCENARIO)
        ;
        if (PLAYER_PARTY != null) {
            launcher.PARTY_CODE = CODE.PRESET;
            launcher.PLAYER_PARTY = PLAYER_PARTY;
        }
        if (ENEMY_PARTY != null) {
            launcher.ENEMY_CODE = CODE.PRESET;
            launcher.ENEMY_PARTY = ENEMY_PARTY;
        }
        if (skipChoice) {
            if (args[0] == PRESET_OPTION_ARG) {
                PresetLauncher.PRESET_OPTION = StringMaster.getInteger(
                 VariableManager.getVar(args[0]));
                FAST_MODE = PresetLauncher.chooseLaunchOption();
                CoreEngine.setExe(true);
            }
            if (arglist.size() > 1) {
                if (arglist.get(1) != null)
                    launcher.PLAYER_PARTY = arglist.get(1);
            }
            if (arglist.size() > 2) {
                if (arglist.get(2) != null)
                    launcher.ENEMY_PARTY = arglist.get(2);
            }
            if (arglist.size() > 3) {
                if (arglist.get(3) != null)
                    DEFAULT_DUNGEON = arglist.get(3);

            }
            launcher.ENEMY_CODE = CODE.PRESET;
            launcher.PARTY_CODE = CODE.PRESET;
        }
        if (DEFAULT_DUNGEON != null)
            launcher.setDungeon(DEFAULT_DUNGEON);
        if (!skipChoice) {
            if (BEHAVIOR_TEST_ON) {
                SUPER_FAST_MODE = true;
            } else {
                FAST_MODE = PresetLauncher.chooseLaunchOption();
                if (BooleanMaster.isFalse(FAST_MODE)) {
                    SUPER_FAST_MODE = true;
                }
            }
        }
        if (!skipChoice) {
            if (FAST_MODE != null) {
                if (FAST_MODE || SUPER_FAST_MODE) {
                    launcher.ENEMY_CODE = CODE.NONE;
                    launcher.PARTY_CODE = CODE.PRESET;
                    launcher.LEADER_MOVES_FIRST = true;
//                    launcher.VISION_HACK = SUPER_FAST_MODE;

                    // preset
                    ItemGenerator.setGenerationOn(!SUPER_FAST_MODE
//                      &&!FAST_MODE
//                     && !CoreEngine.isGraphicTestMode() TODO racing conditions with gdx!
                    );
                    ItemGenerator.setBasicMode(FAST_MODE);

                }

            }
        }
        if (!CoreEngine.isGraphicsOff()) {
            if (!CoreEngine.isSwingOn()) {
                BattleSceneLauncher.main(null);
//                ScreenData data = new ScreenData(ScreenType.BATTLE, "DC", null );
//                GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);
            }
        }

        DC_Engine.fullInit();
        Chronos.mark("GAME LAUNCHED");

        game = launcher.initDC_Game();
        game.start(true);
        initKeyManager();

//        Chronos.mark("xStream save");
//        XStream xStream = new XStream(new DomDriver());
//        String x = xStream.toXML( game);
//        System.out.println(x);
//        Chronos.logTimeElapsedForMark("xStream save");
//
//        Chronos.mark("xStream load");
//       game = (DC_Game) xStream.fromXML(x);
//        Chronos.logTimeElapsedForMark("xStream load");
//
//        Chronos.mark("xStream save2");
//          x = xStream.toXML(DC_Game.game);
//        System.out.println(x);
//        Chronos.logTimeElapsedForMark("xStream save2");
//
//        if (DC_Game.game.equals(game)){
//            Chronos.logTimeElapsedForMark("xStream save2");
//        }

        if (!Saver.TEST_MODE)
            return;
        Chronos.mark("custom save");
        String x = Saver.save("test");
        System.out.println(x);
        Chronos.logTimeElapsedForMark("custom save");

        Chronos.mark("custom load");
//        game = (DC_Game) Loader.loadGame(x);
//        Chronos.logTimeElapsedForMark("custom load");
//
//        Chronos.mark("custom save2");
//        x = custom.toXML(DC_Game.game);
//        System.out.println(x);
//        Chronos.logTimeElapsedForMark("custom save2");
//
//        if (DC_Game.game.equals(game)){
//            Chronos.logTimeElapsedForMark("custom save2");
//        }
    }


    private static void initKeyManager() {
        if (GLOBAL_HOTKEYS_ON) {
            new GlobalKeys().initDC_GlobalKeys();
        }
    }

    public static TestLauncher getLauncher() {
        if (launcher == null) {
            launcher = new TestLauncher(game, FAST_MODE, SUPER_FAST_MODE);
        }

        return launcher;
    }

    public static void setLauncher(TestLauncher launcher) {
        FAST_DC.launcher = launcher;
    }

}
