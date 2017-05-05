package main.test.frontend;

import main.client.DC_Engine;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.game.core.game.DC_Game;
import main.game.logic.dungeon.DungeonMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.hotkey.GlobalKeys;
import main.system.launch.CoreEngine;
import main.test.PresetLauncher;
import main.test.debug.GameLauncher;
import main.test.debug.GameLauncher.CODE;
import main.test.libgdx.DENIS_Launcher;

import java.util.Arrays;
import java.util.LinkedList;
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
    public static String ENEMY_PARTY = "Minotaur;Troglodyte;Harpy";
    public static String PLAYER_PARTY = ""
            + "Bandit Archer;"
            ;
    public static String objData = "";
    public static String objData2
            = "7-5=Minotaur,8-5=Troglodyte,";
    public static boolean SUPER_FAST_MODE;
    public static String exceptions = "";
    public static boolean forceRunGT = false;
    private static boolean MINIMAP_TEST_ON = false;
    private static boolean VISION_HACK = false;
    private static boolean fullscreen = false;
    private static DC_Game game;
    private static boolean running;
    private static int unitGroupLevel;
    private static GameLauncher gameLauncher;
    private static DENIS_Launcher guiLauncher;

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


        List<String> arglist = new LinkedList<>(Arrays.asList(args));
        if (args != null) {
            if (args == SKIP_CHOICE_ARGS || (arglist.contains(PRESET_OPTION_ARG) ||
                    (arglist.contains(PRESET_ARG)))) {
                skipChoice = true;

            }
        }

        gameLauncher = new GameLauncher(game, FAST_MODE, SUPER_FAST_MODE );
        if (skipChoice) {
            if (args[0] == PRESET_OPTION_ARG) {
                PresetLauncher.PRESET_OPTION = StringMaster.getInteger(args[1]);
                FAST_MODE = PresetLauncher.chooseLaunchOption();
            } else if (arglist.contains(PRESET_ARG)) {
                if (arglist.size() > 1) {
                    gameLauncher.PLAYER_PARTY = arglist.get(1);
                }
                if (arglist.size() > 2) {
                    gameLauncher.ENEMY_PARTY = arglist.get(2);
                }
                if (arglist.size() > 3) {
                    DEFAULT_DUNGEON = arglist.get(3);
                }

            }
            gameLauncher.ENEMY_CODE = CODE.PRESET;
            gameLauncher.PARTY_CODE = CODE.PRESET;
        }
        gameLauncher.setDungeon(DEFAULT_DUNGEON);
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
                    gameLauncher.ENEMY_CODE = CODE.NONE;
                    gameLauncher.PARTY_CODE = CODE.PRESET;
                    gameLauncher.LEADER_MOVES_FIRST = true;
                    gameLauncher.VISION_HACK = SUPER_FAST_MODE;
                    if (SUPER_FAST_MODE) {
                        DungeonMaster.CHOOSE_LEVEL = false;
                    }
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
                DENIS_Launcher.main(new String[]{});
            }
        }

        DC_Engine.init();
        Chronos.mark("GAME LAUNCHED");

        game = gameLauncher.initDC_Game();
        game.start(true);
        initKeyManager();


    }



    private static void initKeyManager() {
        if (GLOBAL_HOTKEYS_ON) {
            new GlobalKeys().initDC_GlobalKeys();
        }
    }

    public static GameLauncher getGameLauncher() {
        if (gameLauncher == null) {
            gameLauncher = new GameLauncher(game, FAST_MODE, SUPER_FAST_MODE );
        }

        return gameLauncher;
    }

    public static void setGameLauncher(GameLauncher gameLauncher) {
        FAST_DC.gameLauncher = gameLauncher;
    }

}
