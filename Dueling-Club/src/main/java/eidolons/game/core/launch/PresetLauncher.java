package eidolons.game.core.launch;

import eidolons.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import eidolons.ability.UnitTrainingMaster;
import eidolons.client.cc.logic.items.ItemGenerator;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import eidolons.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_Game.GAME_MODES;
import eidolons.game.core.game.GameFactory.GAME_SUBCLASS;
import eidolons.game.core.launch.TestLauncher.CODE;
import eidolons.game.core.state.Loader;
import eidolons.game.core.state.Saver;
import eidolons.game.module.adventure.faction.FactionMaster;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.controls.EmitterController;
import eidolons.libgdx.anims.particles.ParticleManager;
import eidolons.libgdx.anims.particles.lighting.LightingManager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;
import eidolons.system.controls.Controller.CONTROLLER;
import main.system.data.DataUnit;
import main.system.launch.CoreEngine;
import eidolons.system.test.TestMasterContent;
import eidolons.test.Preset;
import eidolons.test.Preset.PRESET_DATA;
import eidolons.test.Preset.PRESET_OPTION;
import eidolons.test.PresetMaster;
import eidolons.test.debug.DebugMaster;
import eidolons.test.debug.DebugMaster.DEBUG_FUNCTIONS;
import eidolons.test.frontend.FAST_DC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eidolons.test.Preset.PRESET_DATA.FIRST_DUNGEON;

public class PresetLauncher {
    public final static String[] LAUNCH_OPTIONS_TUTORIAL = {
     "Basic", "Advanced", "Trial",
    };
    public static final int OPTION_NEW = 4;
    public final static String[] LAUNCH_OPTIONS = {
     "AI", "Gui", "Last", "Recent", "New", "Anims", "Load", "Exploration", "Exploration Test",
//           "Usability", "Emitters","Light",     "Standoff Test", "Standoff Preset", "Standoff",
     "Profiling"

    };
    public static int PRESET_OPTION = -1;
    public static String PRESET_LAUNCH;
    static LAUNCH launch;
    private static boolean isInitLaunch = true;
    private static DataUnit<?> data;

    static {
        LAUNCH.Profiling.gameMode = GAME_MODES.ARENA;


        LAUNCH.Standoff_Test.gameMode = GAME_MODES.ARENA;
        LAUNCH.Standoff_Test.gameType = GAME_SUBCLASS.ARENA;
        LAUNCH.Standoff_Test.dungeonPath = "Pit.xml";

        LAUNCH.Standoff_Preset.gameMode = GAME_MODES.ARENA;
        LAUNCH.Standoff_Preset.gameType = GAME_SUBCLASS.ARENA;
        LAUNCH.Standoff_Preset.dungeonPath = "Pit.xml";
        LAUNCH.Standoff_Preset.maxAnimTime = 2000;

        LAUNCH.Standoff.gameMode = GAME_MODES.ARENA;
        LAUNCH.Standoff.gameType = GAME_SUBCLASS.ARENA;

        LAUNCH.Standoff.workspaceFilter = WORKSPACE_GROUP.FOCUS;
        LAUNCH.Standoff.PARTY_CODE = CODE.CHOOSE;
        LAUNCH.Standoff.maxAnimTime = 2000;

        LAUNCH.Gui.graphicsTest = true;
        LAUNCH.Gui.visionHacked = true;
        LAUNCH.Gui.freeActions = true;

        LAUNCH.Anims.visionHacked = true;
        LAUNCH.AI.visionHacked = true;
        LAUNCH.Anims.logChannelsOn = new LOG_CHANNEL[]{
         LOG_CHANNEL.ANIM_DEBUG
        };
        LAUNCH.AI.logChannelsOn = new LOG_CHANNEL[]{
         LOG_CHANNEL.AI_DEBUG,
         LOG_CHANNEL.AI_DEBUG2,
        };

        LAUNCH.JUnit.graphicsOff = true;
        LAUNCH.Profiling.dungeonPath = "Test\\Broken Ravenguard Fort.xml";
    }

    static {
        LAUNCH.AI.logicTest = true;
    }

    public static DataUnit<?> getData() {
        return data;
    }

    public static Boolean chooseLaunchOption() {
        if (PRESET_LAUNCH != null)
            return init(PRESET_LAUNCH);

        int i = PRESET_OPTION;
        if (i == -1) {
            if (!FAST_DC.forceRunGT) {
                i = DialogMaster.optionChoice("", LAUNCH_OPTIONS);
            } else {
                i = 1;
            }
        }
        return init(LAUNCH_OPTIONS[i]);

    }

    private static Boolean init(String launchOption) {
        if (isInitLaunch) {
            launch = initLaunch(launchOption);
        }
        Preset p = null;
        if (launch != null) {
            return customInit(launch);
        } else {
            switch (launchOption) {

                case "Last":
                    Preset lastPreset = PresetMaster.loadLastPreset();
                    UnitGroupMaster.setFactionMode(false);
                    UnitTrainingMaster.setRandom(false);
                    PresetMaster.setPreset(lastPreset);
                    break;
                case "Recent":
                    chooseRecentPreset();
                    break;
                case "New":
                    FAST_DC.getLauncher().DUMMY_MODE = false;
                    FAST_DC.getLauncher().DUMMY_PP = false;
                    if (FactionMaster.isFactionsSupported())
                        UnitGroupMaster.setFactionMode(DialogMaster.confirm("Faction Mode?"));
                    return null;
                case "Superfast":
                    FAST_DC.getLauncher().DUMMY_MODE = true;
                    FAST_DC.getLauncher().DUMMY_PP = true;
                    FAST_DC.getLauncher().setSUPER_FAST_MODE(true);
                    return false;
                case "Load":
//                    File save = ListChooser.chooseFile(Saver.getSavePath());
//                   if (save!=null ){
//                       Loader.setPendingLoadPath(save.getPath());
//                   }

                    Loader.setPendingLoadPath(Saver.getSavePath() + "test.xml");
                    break;

            }
        }

        return null;
    }

    private static boolean customInit(LAUNCH launch) {
        switch (launch) {
            case EXPLORATION:
            case EXPLORATION_TEST:
                ExplorationMaster.setTestMode(true);
                return true;
            case Emitters:
                ParticleManager.setAmbienceOn(true);
                EmitterController.setTestMode(true);
                CoreEngine.setActionTargetingFiltersOff(true);
                return true;
            case Anims:
                CoreEngine.animationTestMode = true;
                EmitterController.overrideKeys = true;
                TestMasterContent.setAddAllSpells(true);
                TestMasterContent.setImmortal(false);
                CoreEngine.setActionTargetingFiltersOff(true);
                return true;
            case Gui:
                CoreEngine.setGuiTestMode(true);
                return true;
            case Light:
                LightingManager.setLightOn(true);
                LightingManager.setTestMode(true);
                CoreEngine.setActionTargetingFiltersOff(true);
                return true;
        }
        return false;
    }

    public static LAUNCH getLaunch() {
        return launch;
    }

    public static LAUNCH initLaunch(String option) {
        launch = new EnumMaster<LAUNCH>().retrieveEnumConst(LAUNCH.class, option);
        if (launch == null) {
            return null;
        }
        if (launch.logChannelsOn != null) {
            Arrays.stream(launch.logChannelsOn).forEach(c -> {
                c.setOn(true);
            });
        }
        if (launch.logChannelsOff != null) {
            Arrays.stream(launch.logChannelsOff).forEach(c -> {
                c.setOn(false);
            });
        }

        if (launch.preset != null) {
            Preset p = PresetMaster.loadPreset(launch.preset);
            PresetMaster.setPreset(p);
        }
        if (launch.dungeonPath != null) {
//            DungeonMaster.setDEFAULT_DUNGEON_PATH(launch.dungeonPath);
        }
        if (launch.dungeonType != null) {
//            DungeonMaster.setDEFAULT_DUNGEON(launch.dungeonType);
        }
        return launch;
    }

    public static Preset chooseRecentPreset() {
        List<Preset> recent = PresetMaster.getRecentPresets();
        return choosePreset(recent);
    }

    public static Preset choosePreset() {
        List<Preset> presets = PresetMaster.getPresets();
        return choosePreset(presets);
    }

    public static Preset choosePreset(List<Preset> presets) {
        List<String> list = new ArrayList<>();
        for (Preset s : presets) {
            list.add(s.toString());
        }
        String result = new ListChooser(SELECTION_MODE.SINGLE, list, false).choose();
        if (result == null) {
            return null;
        }
        Preset preset = PresetMaster.findPreset(result);
        if (preset != null) {
            PresetMaster.setPreset(preset);
        }
        return preset;
    }

    public static void launchPresetDynamically() {
        Preset profile = choosePreset();
        boolean newDungeon = false;
        String oldDungeon = PresetMaster.getPreset().getValue(FIRST_DUNGEON);
        if (!profile.getValue(FIRST_DUNGEON).equals(oldDungeon)) {
            newDungeon = true; // preCheck after launch?
        }
        launchPreset(profile);
        if (newDungeon) {
//            DC_Game.game.getDungeonMaster().initDungeonLevelChoice();
        }
//        TODO
        DC_Game.game.getDebugMaster().executeDebugFunctionNewThread(DEBUG_FUNCTIONS.CLEAR);
        // preCheck new dungeon

    }

    public static void launchPreset() {
        launchPreset(PresetMaster.getPreset());
    }

    public static void initPresetData(DungeonData dungeonData, Preset preset) {
        data = dungeonData;
        launchPreset(preset);
    }

    public static void launchPreset(Preset profile) {
        PresetMaster.setPreset(profile);
        if (data == null)
            data = new DungeonData();
        for (PRESET_DATA item : PRESET_DATA.values()) {

            String value = profile.getValue(item);
            String valueName = "";

            if (value != null) {
                switch (item) {
                    case CONTENT_SCOPE:
                        initContentScope(value);
                        break;
                    case OPTIONS:
                        initOptions(value);
                        break;
                    case PRESET_OPTION_PARAMS:
                        initOptionParams(value);
                        break;
                    case ENEMY_PARTY:
//                      TODO   initDefaultParty(value, false);
                        break;
                    case ENEMIES:
                    case PLAYER_UNITS:
                        valueName = PARTY_VALUE.MEMBERS.toString();
                        break;
                    case PLAYER_PARTY:
//                       valueName= PARTY_VALUE.PARTY_NAME;
                        break;
                    case DUNGEONS:
                        initDungeonsList(value);
                        break;
                    case FIRST_DUNGEON:
                        if (value.contains("."))
                            valueName = DUNGEON_VALUE.PATH.toString();
                        else
                            valueName = DUNGEON_VALUE.TYPE_NAME.toString();
                        break;
                }
            }
            data.setValue(valueName, value);
        }
        /*
         * set dungeon, layer, encounter, party, options, ...
		 */
    }

    private static void initOptions(String value) {
        for (String optionString : StringMaster.open(value)) {
            PRESET_OPTION option = new EnumMaster<PRESET_OPTION>().retrieveEnumConst(
             PRESET_OPTION.class, optionString);
            switch (option) {
                case DEBUG:
                    DC_Game.game.setDebugMode(true);
                    break;
                case ITEM_GENERATION_OFF:
                    ItemGenerator.setGenerationOn(false);
                    break;
                case OMNIVISION:
                    DebugMaster.setOmnivisionOn(true);
                    break;
                default:
                    break;

            }
        }
    }

//    private static void initDefaultParty(String value, boolean me) {
//        ObjType type = null;
//        if (!value.contains(StringMaster.getSeparator())) {
//            type = DataManager.getType(value, DC_TYPE.PARTY);
//        }
//        if (type == null) {
//            if (me) {
//                DC_Game.game.setPlayerParty(value);
//            } else {
//                DC_Game.game.setEnemyParty(value);
//            }
//        } else if (me) {
//            DC_Game.game.setPlayerParty(type.getProperty(PROPS.MEMBERS));
//        } else {
//            DC_Game.game.setEnemyParty(type.getProperty(PROPS.MEMBERS));
//        }
//    }
//
//    private static void initPlayerUnits(String value, boolean b) {
//        DC_Game.game.getData().setPlayerUnitData(value);
//        DC_Game.game.setPlayerParty(value);
//
//    }
//
//    private static void initEnemies(String value, boolean b) {
//
//        DC_Game.game.getData().setPlayer2UnitData(value);
//        DC_Game.game.setEnemyParty(value);
//    }
//
//    private static void initFirstDungeon(String name) {
//        DC_Game.game.getDungeonMaster().setChooseLevel(false);
//        DC_Game.game.getDungeonMaster().setDungeonPath(name);
//    }

    private static void initOptionParams(String value) {
        // DebugMaster.setOmnivisionOn(VISION_HACK);
        // game.setDebugMode(FAST_MODE || SUPER_FAST_MODE);
        // CoreEngine.setLogicTest(true);

    }

    private static void initDungeonsList(String value) {
        for (String name : StringMaster.open(value)) {
            // else TODO
            // DungeonMaster.getPendingDungeons().add(name);

        }

    }

    private static void initContentScope(String value) {
        // TODO Auto-generated method stub

    }

    public enum LAUNCH {
        EXPLORATION("exploration.xml", RULE_SCOPE.FULL, null),
        EXPLORATION_TEST("exploration test.xml", RULE_SCOPE.TEST, true),
        AI("ai.xml", RULE_SCOPE.TEST, false),
        Gui("graphics test.xml", RULE_SCOPE.BASIC, true),
        Playtest("ai full.xml", RULE_SCOPE.FULL, null),
        Anims(null, RULE_SCOPE.BASIC, true),
        Emitters(true),
        Light("light preview.xml", RULE_SCOPE.BASIC, true),
        JUnit(null
//         "junit.xml"
         , RULE_SCOPE.FULL, null),
        Profiling(true),
        Standoff(null, RULE_SCOPE.FULL, null),
        Standoff_Test(null, RULE_SCOPE.TEST, true),
        Standoff_Preset(null, RULE_SCOPE.FULL, null),
        Usability("Usability.xml", RULE_SCOPE.FULL, null);
        public Boolean immortal;
        public CONTROLLER controller;
        public String preset;
        public String dungeonType;
        public String dungeonPath;
        public RULE_SCOPE ruleScope;
        public boolean graphicsTest;
        public boolean debugMode;
        public boolean dummy;
        public boolean dummy_pp;
        public boolean visionHacked;
        public boolean fast;
        public boolean actionTargetingFiltersOff;
        public boolean freeActions;
        public boolean itemGenerationOff;
        public boolean deterministicUnitTraining;
        public LOG_CHANNEL[] logChannelsOn;
        public LOG_CHANNEL[] logChannelsOff;
        public boolean graphicsOff;
        public boolean logicTest;
        public int ENEMY_CODE;
        public int PARTY_CODE;
        public GAME_MODES gameMode;
        public WORKSPACE_GROUP workspaceFilter;
        public int maxAnimTime;
        public GAME_SUBCLASS gameType;

        //test launches
        LAUNCH() {
            deterministicUnitTraining = true;

        }

        LAUNCH(String preset, RULE_SCOPE ruleScope, Boolean dummyPlus) {
            this(dummyPlus);
            this.preset = preset;
            this.ruleScope = ruleScope;
        }

        LAUNCH(Boolean dummyPlus) {
            if (dummyPlus != null) {
                if (dummyPlus) {
                    initDummyPlusFlags();
                } else {
                    initDummyFlags();
                }
            }
        }

        LAUNCH(String preset,
               RULE_SCOPE ruleScope,
               boolean dummy,
               boolean dummy_pp,
               boolean visionHacked,
               boolean fast,
               boolean actionTargetingFiltersOff,
               boolean freeActions,
               boolean itemGenerationOff,
               boolean deterministicUnitTraining) {
            this.preset = preset;
            this.ruleScope = ruleScope;
            this.dummy = dummy;
            this.dummy_pp = dummy_pp;
            this.visionHacked = visionHacked;
            this.fast = fast;
            this.actionTargetingFiltersOff = actionTargetingFiltersOff;
            this.freeActions = freeActions;
            this.itemGenerationOff = itemGenerationOff;
            this.deterministicUnitTraining = deterministicUnitTraining;
        }

        private void initDummyFlags() {
            this.debugMode = true;
            this.dummy = true;
            this.fast = true;
            ruleScope = RULE_SCOPE.TEST;

        }

        private void initDummyPlusFlags() {
            this.debugMode = true;
            this.dummy = true;
            this.dummy_pp = true;
            this.visionHacked = true;
            this.freeActions = true;
            this.fast = true;
            ruleScope = RULE_SCOPE.TEST;

        }


    }

    public enum LAUNCH_MODS {
        //modify launch!
        SELECT_DUNGEON,
        SELECT_party,
        SELECT_enemy,
        SELECT_config,

    }

}