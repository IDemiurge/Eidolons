package main.test.frontend;

import main.client.DC_Engine;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.game.NetGame;
import main.client.game.TestMode;
import main.client.game.gui.DC_GameGUI;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.client.net.HostClientConnection;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.logic.arena.UnitGroupMaster;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.dungeon.DungeonMaster;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.hotkey.DC_KeyManager;
import main.system.hotkey.GlobalKeys;
import main.system.launch.CoreEngine;
import main.system.net.WaitingThread;
import main.system.sound.Player;
import main.test.Preset;
import main.test.Preset.PRESET_DATA;
import main.test.PresetLauncher;
import main.test.PresetMaster;
import main.test.debug.GameLauncher;
import main.test.debug.GameLauncher.CODE;
import main.test.libgdx.DENIS_Launcher;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FAST_DC {
    public static final boolean PRESET_DATA_MODE = true;
    public static final boolean NET_FAST_MODE = true;
    public static final String[] SKIP_CHOICE_ARGS = new String[]{"", ""};
    public static final String PRESET_ARG = "PRESET";
    public static final String PRESET_OPTION_ARG = "PRESET_OPTION";
    private static final boolean GLOBAL_HOTKEYS_ON = true;
    private static final String DEFAULT_REGION = "Dusk Dale";
    // "Cemetary";
    private static final boolean BEHAVIOR_TEST_ON = false;

    public static String DEFAULT_TEST_DUNGEON = "test\\Clearshot Test.xml";//Clearshot
    public static String DEFAULT_DUNGEON = null;
    public static Boolean FAST_MODE = false;
    public static Boolean factionLeaderRequired = false;
    // public static boolean PRESET_PARTY = false;
    public static int ENEMY_CODE = CODE.CHOOSE;
    public static int PARTY_CODE = CODE.CHOOSE; // preset generic-code?
    // private static boolean RANDOMIZE_PARTY = false;
    // private static boolean RANDOMIZE_ENEMIES_PARTY = true;
    public static boolean LEADER_MOVES_FIRST = false;
    public static String ENEMY_PARTY = "Minotaur;Troglodyte;Harpy";
    public static String PLAYER_PARTY = ""

            // + "Sir David;"
            // + "Aeridan v3 ;"
            // "Mograine;Haelem;Tobart the Good;Fulgrim";
            // "Elthedar v2;Tili v4;"
            // "Guy Fox;"
            // + "Warlock"
            // + "Chaos Cultist;"
            + "Bandit Archer;"
            // + "Elberen v3;"
            // + "Thief;Twisted Mutant;Spiderling;Skeleton;Evil Eye;" + ""
            ;
    public static String objData = "";
    // private static String objData2 =
    // "4-1=Chaos Warlock,3-1=Witch,5-1=Plague Bringer,";
    public static String objData2
            // = "4-1=Chaos Warlock," +
            // "3-1=Witch,"+
            // "5-1=Plague Bringer," + "5-0=Death Adept," + "4-0=Vampire Mistress,"
            // + "3-0=Gargoyle Sentinel,"

            // "5-1=Wraith Whisperer,";
            // + "4-0=Evil Eye,"
            = "7-5=Minotaur,8-5=Troglodyte,";
    public static boolean SUPER_FAST_MODE;
    public static boolean LOCALHOST = true;
    public static String exceptions = "";
    public static boolean forceRunGT = false;
    private static boolean MINIMAP_TEST_ON = false;
    private static boolean VISION_HACK = false;
    private static boolean fullscreen = false;
    private static DC_Game game;
    private static DC_KeyManager keyManager;
    private static DC_GameGUI GUI;
    private static boolean running;
    private static Boolean host_client;
    private static NetGame netGame;
    private static int unitGroupLevel;
    private static GameLauncher gameLauncher;
    private static DENIS_Launcher guiLauncher;
    private static main.test.libgdx.prototype.Launcher launcher;

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
            }
        }
/*        ClassLoader classLoader = FAST_DC.class.getClassLoader();
        File f = new File(classLoader.getResource("").getFile());
        f = new File(f.getParentFile()+"/dependency/Font");
        for (String s : f.list()) {
            System.out.println(s);
        }*/
//        File f = new File(.getFile());
//        f = f.getParentFile().getParentFile();
//        System.out.println(f.getAbsolutePath());
        //InputStream is = FAST_DC.class.getClassLoader().getResourceAsStream("Font");

        CoreEngine.swingOn = false;
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

            } else if (args.length == 1) {
                if (args[0].equals("client")) {
                    host_client = false;
                    LOCALHOST = DialogMaster.confirm("Localhost?");
                }
                if (args[0].equals("host")) {
                    host_client = true;
                }
            }
        }
        if (host_client != null) {
            netGame = TestMode.launch(host_client);
            // OptionsMaster.setOption(SOUND_OPTION.VOICE_OFF, true);
            if (!LOCALHOST) {
                Player.setSwitcher(false);
            }
            if (!host_client) {
                requestDataFromHost();

                skipChoice = true;
            } else {

                listenForDataRequests();

            }
        }
        gameLauncher = new GameLauncher(game, FAST_MODE, SUPER_FAST_MODE, host_client);
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
        if (!CoreEngine.isSwingOn()) {
            DENIS_Launcher.main(new String[]{});
        }
//        main.test.libgdx.prototype.Launcher.main(new String[]{});
        CoreEngine.setTEST_MODE(true);
        DC_Engine.init();
        Chronos.mark("GAME LAUNCHED");
        if (host_client != null) {
            initNetGame();
        }

        game = gameLauncher.initDC_Game();
        game.setHostClient(host_client);
        game.start(true);
        if (CoreEngine.isSwingOn()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        createGUI();
                    }
                });
            } catch (InvocationTargetException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        initKeyManager();

        if (MINIMAP_TEST_ON) {
            try {
                Launcher.setView(VIEWS.MINI_MAP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if (BEHAVIOR_TEST_ON) {
        // WaitMaster.WAIT(500);
        // game.getManager().infoSelect(game.getPlayer(true).getHeroObj());
        // game.getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.KILL_UNIT);
        // }

        // CoreEngine.setWritingLogFilesOn(true);
    }


    private static void initNetGame() {

        DC_Player playerMe = new DC_Player("Host", Color.blue, true, !host_client, PLAYER_PARTY);
        DC_Player playerEnemy = new DC_Player("Client", Color.cyan, false, !host_client, null);

        if (PresetMaster.getPreset() != null) {
            String party = PresetMaster.getPreset().getValue(PRESET_DATA.ENEMY_PARTY);
            if (party != null) {
                playerEnemy = new DC_Player("Client", Color.cyan, false, !host_client, party);
            }
        }
        if (host_client) {
            game = new DC_Game(netGame, playerMe, playerEnemy);
        } else {
            game = new DC_Game(netGame.getConnection(), netGame.getHostedGame(), playerEnemy,
                    playerMe);
        }
        if (gameLauncher != null) {
            gameLauncher.setGame(game);
        }
        game.setGameMode(GAME_MODES.DUEL);
    }

    private static void listenForDataRequests() {

        // netGame.getConnection().listenInNewThread(HOST_CLIENT_CODES.GAME_DATA_REQUEST);

    }

    private static void initNetData(String data) {
        if (UnitGroupMaster.isFactionMode()) {
            // TODO init host data, prompt for my data (allow last? hero-group
            // pairs)
        }
        Preset preset = PresetMaster.createNetPreset(data);
        PresetMaster.setPreset(preset);
    }

    private static void requestDataFromHost() {
        HostClientConnection connection = netGame.getConnection();
        HOST_CLIENT_CODES code = HOST_CLIENT_CODES.GAME_DATA_REQUEST;
        connection.send(code);
        String data = null;
        if (new WaitingThread(code).waitForInput()) {
            data = WaitingThread.getINPUT(code);
        }
        if (StringMaster.isInteger(data)) {
            Integer integer = StringMaster.getInteger(data);
            if (integer == 1) {
                SUPER_FAST_MODE = true;
            } else if (integer == 2) {
                SUPER_FAST_MODE = true;
            }
        } else {
            initNetData(data);
        }
    }

    // ////////////// GUI \\\\\\\\\\\\\\\\\\
    private static void createGUI() {
        GUI = new DC_GameGUI(game, fullscreen);
        GUI.initGUI();
        game.setGUI(GUI);

    }

    private static void initKeyManager() {
        keyManager = new DC_KeyManager(game.getManager());
        keyManager.init();
        if (GLOBAL_HOTKEYS_ON) {
            new GlobalKeys().initDC_GlobalKeys();
        }
    }

    public static GameLauncher getGameLauncher() {
        if (gameLauncher == null) {
            gameLauncher = new GameLauncher(game, FAST_MODE, SUPER_FAST_MODE, host_client);
        }

        return gameLauncher;
    }

    public static void setGameLauncher(GameLauncher gameLauncher) {
        FAST_DC.gameLauncher = gameLauncher;
    }

}
