package main.game.core.launch;

import main.ability.UnitTrainingMaster;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.battlecraft.logic.dungeon.test.TestSpawner;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import main.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import main.game.battlecraft.logic.dungeon.universal.Spawner;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.launch.PresetLauncher.LAUNCH;
import main.game.core.state.Loader;
import main.game.module.adventure.travel.EncounterMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.hotkey.DC_KeyManager;
import main.system.launch.CoreEngine;
import main.system.test.TestMasterContent;
import main.test.Preset;
import main.test.PresetMaster;
import main.test.debug.DebugMaster;
import main.test.frontend.FAST_DC;
import main.test.frontend.Showcase;

import java.util.List;

public class TestLauncher //extends MetaGameMaster
{
    //    @Override
//    protected DC_Game createGame() {
//        return new TestGame();
//    }
    public int ENEMY_CODE = CODE.CHOOSE;
    public int PARTY_CODE = CODE.CHOOSE;
    public boolean VISION_HACK = false;
    public boolean LEADER_MOVES_FIRST = false;
    public String ENEMY_PARTY = ""; //Pirate
    public String PLAYER_PARTY =
     "Luwien Tulanir v3"
     // +",Blauri Kinter v2"
//            "Nelia Valrith;"
     // +"Grufirant Grossklotz;Orthaelion Enloth;Belia Haevril"
//      ";Anfina Ilarfis;Amaltha Soamdath;Belia Haevril"
     //     "Demir;Brother Anthin;Ogsit Tholmir;";//"Guy Fox;Fiona Emrin;Donkel Nogvir;";// "Elberen v2;";//"Bandit Archer";//Zail Adelwyn v4
     ;
    public boolean DUMMY_MODE = false;
    public boolean DUMMY_PP = false;
    public Boolean FAST_MODE;
    public Boolean SUPER_FAST_MODE;
    protected String dungeon = "Cemetary.xml";
    protected Boolean DEBUG_MODE;
    protected Integer PLAYER_CHOICE_OPTION = null;
    protected Integer ENEMY_CHOICE_OPTION = 0;
    protected DC_Game game;
    protected String encounterName;
    protected WORKSPACE_GROUP workspaceFilter;
    protected GAME_SUBCLASS gameType = GAME_SUBCLASS.TEST;

    public TestLauncher(GAME_SUBCLASS gameType) {
        this.gameType = gameType;
    }

    public TestLauncher(DC_Game game, Boolean FAST_MODE, Boolean SUPER_FAST_MODE) {
        this.game = game;
        this.FAST_MODE = BooleanMaster.isTrue(FAST_MODE);
        this.SUPER_FAST_MODE = BooleanMaster.isTrue(SUPER_FAST_MODE);
    }


    protected String initFactionData() {
//        unitGroupLevel = BooleanMaster.isFalse(host_client) ? UnitGroupMaster.getPowerLevel()
//                : DialogMaster.inputInt(UnitGroupMaster.getPowerLevel());
        // Faction faction = chooseFaction();
        // if (random)
        UnitTrainingMaster.setRandom(false);

        return UnitGroupMaster.chooseGroup(true);

    }

    protected void createPreset() {
        String enemy = ENEMY_PARTY;
        String party = PLAYER_PARTY;
        if (encounterName != null) {
            enemy = encounterName;
        }
        String levelFilePath = game.getDungeonMaster().getDungeonWrapper().getLevelFilePath();
        if (StringMaster.isEmpty(levelFilePath)) {
            levelFilePath = game.getDungeonMaster().getDungeonWrapper().getLevelFilePath();
        }
        Preset preset = PresetMaster.createPreset(party, enemy, levelFilePath, true);
        PresetMaster.setPreset(preset);
        PresetMaster.updatePreset();
    }

    protected void savePresetAsLast() {
        if (!FAST_MODE) {
            if (!SUPER_FAST_MODE) {
                PresetMaster.savePreset(PresetMaster.getPreset(), null);
            }
        }
    }

    protected void autosavePreset() {
        PresetMaster.savePreset(PresetMaster.getPreset(), true);
    }


    public LaunchDataKeeper createDataKeeper() {
        if (PresetMaster.getPreset() != null)
            return new LaunchDataKeeper(game, PresetMaster.getPreset());
        return new LaunchDataKeeper(game, PLAYER_PARTY, ENEMY_PARTY, dungeon);
    }


    public DC_Game initDC_Game() {
        if (game == null) {
            if (PresetLauncher.getLaunch() != null) {
                if (PresetLauncher.getLaunch().gameType != null)
                    gameType = PresetLauncher.getLaunch().gameType;
            } else {
                if (Loader.getPendingLoadPath() != null) {
                    return Loader.loadPendingSave();
                }

            }
            game = GameFactory.createGame(gameType);
//                game = new DC_Game(false);
        }
        DC_Game.game = (game);
        initLaunch();
        initFlags();
        if (DEBUG_MODE != null) {
            game.setDebugMode(DEBUG_MODE);
        }
        if (PresetMaster.getPreset() == null) {

            if (DEBUG_MODE == null) {
                game.setDebugMode(Launcher.isDEBUG_MODE_DEFAULT());
            }
            initPlayerParties();
        }
        game.init();
        game.dungeonInit();
        initData();
        game.battleInit();
        if (DUMMY_MODE) {
            game.setDummyMode(true);
            game.setDummyPlus(DUMMY_PP);

            if (DUMMY_PP) {
                RuleMaster.setScope(RULE_SCOPE.TEST);
            }
        }
        try {
            if (PresetMaster.getPreset() == null // &&
             // !BooleanMaster.isTrue(FAST_MODE)
             && BooleanMaster.isFalse(SUPER_FAST_MODE)) {
                createPreset();
//                autosavePreset();
            }
            savePresetAsLast();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public void initFlags() {

        // select code?


        VisionManager.setVisionHacked(VISION_HACK);
        DebugMaster.setOmnivisionOn(VISION_HACK);

        if (PresetMaster.getPreset() == null) {
            if (getFAST_MODE()) {
                if (dungeon == null) {
                    dungeon = DungeonInitializer.RANDOM_DUNGEON;
                }
            }
            if (getSUPER_FAST_MODE()) {
                PLAYER_PARTY = (FAST_DC.PLAYER_PARTY);
                ENEMY_PARTY = "";
            }
        }
        if (game.getBattleMaster() != null) {
//            game.getBattleMaster().getSpawner().init();
        }


    }

    public void initData() {

        LaunchDataKeeper dataKeeper = createDataKeeper();
        if (workspaceFilter != null)
            dataKeeper.getDungeonData().setValue(DUNGEON_VALUE.WORKSPACE_FILTER
             , workspaceFilter.toString());

        game.setDataKeeper(dataKeeper);
    }

    public void initLaunch() {
        LAUNCH launch = PresetLauncher.getLaunch();
        if (launch == null) {
            TestMasterContent.test_on = false;
            return ;
        }
//            if (PresetLauncher.getLaunch().preset != null) {
//                Preset portrait = PresetMaster.loadPreset(PresetLauncher.getLaunch().preset);
//         PresetMaster.setPreset(portrait);
//            } TODO move here from PResetLauncher
        ENEMY_CODE = launch.ENEMY_CODE;
        PARTY_CODE = launch.PARTY_CODE;

        if (!VISION_HACK) {
            VISION_HACK = launch.visionHacked;
        }

        workspaceFilter = launch.workspaceFilter;

        DUMMY_MODE = launch.dummy;
        DUMMY_PP = launch.dummy_pp;
        DEBUG_MODE = launch.debugMode;
        FAST_MODE = launch.fast;

        TestMasterContent.test_on = FAST_MODE;

        if (launch.ruleScope != null) {
            RuleMaster.setScope(launch.ruleScope);
        }
        CoreEngine.setLogicTest(launch.logicTest);
        if (launch.logicTest) {
            initLogicTest();
        }
        CoreEngine.setGraphicsOff(launch.graphicsOff);
        ItemGenerator.setGenerationOn(!launch.itemGenerationOff);
        TestMasterContent.setForceFree(launch.freeActions);

        TestMasterContent.setImmortal(launch.immortal);
        CoreEngine.setGraphicTestMode(launch.graphicsTest);

        UnitTrainingMaster.setSpellsOn(!launch.fast);
        UnitTrainingMaster.setSkillsOn(!launch.fast);

        UnitTrainingMaster.setRandom(!launch.deterministicUnitTraining);
        if (launch.gameMode != null) {
            game.setGameMode(launch.gameMode);
        }
        DC_KeyManager.DEFAULT_CONTROLLER = launch.controller;

//TODO options init!         GameLoop.setMaxAnimTime(launch.maxAnimTime);
    }

    protected void initLogicTest() {
//        ChannelingRule.setTestMode(true);
    }

    protected void initPlayerParties() {
        switch (PARTY_CODE) {
            case CODE.CHOOSE:
                PLAYER_PARTY = choosePlayerUnits(PLAYER_CHOICE_OPTION);
                break;
            case CODE.RANDOM:
                PLAYER_PARTY = randomizeParty();
                break;
            case CODE.NONE:
                PLAYER_PARTY ="";
        }

        switch (ENEMY_CODE) {

            case CODE.CHOOSE:
                ENEMY_PARTY = chooseEnemies(ENEMY_CHOICE_OPTION);
                break;
            case CODE.RANDOM:
                ENEMY_PARTY = randomizeEnemies();
                break;
             case CODE.NONE:
                 ENEMY_PARTY ="";
             break;
        }
        Showcase.launchData+=PLAYER_PARTY+";";
        Showcase.launchData+=ENEMY_PARTY+";";
    }

    public String randomizeParty() {
        String party = "";
        try {
            party = getRandomizedParty(2, 2, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return party;
    }

    public String choosePlayerUnits(Integer OPTION) {
        // TODO
        // if (DialogMaster.confirm("Party?")) {
        // // game.setParty(party);
        // partyName = new ListChooser(SELECTION_MODE.SINGLE, DataManager
        // .getTypeNames(OBJ_TYPES.PARTY), OBJ_TYPES.PARTY).choose();
        // if (partyName != null)
        // return (DataManager.getType(partyName,
        // OBJ_TYPES.PARTY).getProperty(PROPS.MEMBERS));
        // }

        if (workspaceFilter != null) {
            return chooseFiltered(DC_TYPE.CHARS);
        }


        game.setTestMode(true);
        if (OPTION == null) {
            OPTION = DialogMaster.optionChoice("Select party init option", "Group", "Default",
             "Heroes", "Units", "Party");
        }
        switch (OPTION) {
            case 0:
                TestSpawner.setPlayerUnitGroupMode(true);
                // String flip = ListChooser.chooseEnum(FLIP.class);
                // if (flip != null)
                // UnitGroupMaster.setFlip(new
                // EnumMaster<FLIP>().retrieveEnumConst(FLIP.class,
                // flip));
                if (UnitGroupMaster.isFactionMode()) {
                    return initFactionData();
                } else {
                    return UnitGroupMaster.chooseGroup(true);
                }
            case 1:
                return PLAYER_PARTY;
            case 2:
                game.setTestMode(false);

                return chooseCharacters();
            case 3:
                return chooseUnits();
            case 4:
                return chooseParty();
        }
        return chooseCharacters();
    }

    protected String chooseParty() {
        ObjType party = ListChooser.chooseType_(
         DataManager
          .getTypes(DC_TYPE.PARTY )
//         DataManager
//          .getTypesGroup(DC_TYPE.PARTY, "Preset")
         , DC_TYPE.PARTY);
        return party.getProperty(PROPS.MEMBERS);
    }

    public String chooseCharacters() {
        return choose(DC_TYPE.CHARS);
    }

    public String chooseUnits() {
        return choose(DC_TYPE.UNITS);
    }

    public String chooseFiltered(DC_TYPE TYPE) {
        List<ObjType> data = DataManager.getTypes(TYPE);
        if (workspaceFilter != null) {
            data.removeIf(type -> type.getWorkspaceGroup() != workspaceFilter);
        }
        String objects = ListChooser.chooseTypes(data);
        return objects;

    }

    public String choose(DC_TYPE type) {
        String filterGroup = getFilterGroup(type);
        List<String> data = DataManager.getTypeNames(type);
        if (!filterGroup.isEmpty()) {
            data = DataManager.getTypesSubGroupNames(type, filterGroup);
        }


        String objects = new ListChooser(SELECTION_MODE.MULTIPLE, data, type).choose();
        return objects;
    }

    // TODO DIFFERENT PER MODE!
    protected String getFilterGroup(DC_TYPE type) {
        if (type == DC_TYPE.CHARS) {
            return StringMaster.BATTLE_READY;
        }
        if (type == DC_TYPE.UNITS) {
            return "";
        }
        if (type == DC_TYPE.ENCOUNTERS) {
            return StringMaster.BATTLE_READY;
        }
        if (type == DC_TYPE.PARTY) {
            return StringMaster.BATTLE_READY;
        }
        return StringMaster.PRESET;
    }

    public String chooseEnemies(Integer ENEMY_OPTION) {
        if (ENEMY_OPTION == null) {
            ENEMY_OPTION = DialogMaster.optionChoice("Select Enemy init option", "Group",
             "Encounter", "Heroes", "Units", "Default");
        }
        switch (ENEMY_OPTION) {
            case 0:
                Spawner.setEnemyUnitGroupMode(true);
                return UnitGroupMaster.chooseGroup(false);
            case 1:
                encounterName = ListChooser.chooseType(DC_TYPE.ENCOUNTERS);
                if (encounterName != null) {
                    return getEnemiesFromWave(DataManager.getType(encounterName,
                     DC_TYPE.ENCOUNTERS));
                }
            case 2:
                return chooseCharacters();
            case 3:
                return chooseUnits();
            case 4:
                return ENEMY_PARTY;
        }
        // String result = chooseUnits();
        // if (StringMaster.isEmpty(result))
        // result = (chooseCharacters());
        // if (StringMaster.isEmpty(result))
        // return "";
        // return result;

        return chooseUnits();

    }

    protected String randomizeEnemies() {
        ObjType type = DataManager.getRandomType(DC_TYPE.ENCOUNTERS, null);
        return getEnemiesFromWave(type);
    }

    protected String getEnemiesFromWave(ObjType type) {
        Integer power = EncounterMaster.getPower(type, null);
        if (power < EncounterMaster.getPower(StringMaster.openContainer(PLAYER_PARTY))) {
            return (type.getProperty(PROPS.EXTENDED_PRESET_GROUP));
        } else if (power > 2 * EncounterMaster.getPower(StringMaster.openContainer(PLAYER_PARTY))) {
            return (type.getProperty(PROPS.SHRUNK_PRESET_GROUP));
        } else {
            return (type.getProperty(PROPS.PRESET_GROUP));
        }
    }

    protected String getRandomizedParty(int heroesCount, int unitCount, Integer maxLevel,
                                      Integer minLevel) {
        ObjType mainHero = null;
        Loop.startLoop(100);
        while (Loop.loopContinues()) {
            ObjType type = RandomWizard.getRandomType(DC_TYPE.CHARS);
            if (maxLevel != null) {
                if (type.getLevel() > maxLevel) {
                    continue;
                }
            }
            if (minLevel != null) {
                if (type.getLevel() < minLevel) {
                    continue;
                }
            }
            mainHero = type;
        }
        String party = mainHero.getName() + ";";
        Loop.startLoop(100);
        while (unitCount > 0 && Loop.loopContinues()) {
            OBJ_TYPE TYPE = DC_TYPE.UNITS;
            boolean subgroup = true;
            String property = mainHero.getProperty(G_PROPS.DEITY);

            if (!DataManager.isTypeName(property)) {
                property = mainHero.getProperty(G_PROPS.ASPECT);
                subgroup = false;
            }
            if (addRandomUnit(TYPE, property, subgroup, party, minLevel, maxLevel)) {
                unitCount--;
            }
        }
        Loop.startLoop(100);
        while (heroesCount > 0 && Loop.loopContinues()) {
            // party += RandomWizard.getRandomType(OBJ_TYPES.CHARS).getName() +
            // ";";
            if (addRandomUnit(DC_TYPE.CHARS, null, false, party, minLevel, maxLevel)) {
                heroesCount--;
            }
        }
        return party;
    }

    protected boolean addRandomUnit(OBJ_TYPE TYPE, String property, boolean subgroup, String party,
                                  Integer maxLevel, Integer minLevel) {

        ObjType objType = RandomWizard.getRandomType(TYPE, property, subgroup);
        if (objType.checkProperty(G_PROPS.GROUP, "Background")) {
            return false;
        }
        if (!WorkspaceMaster.checkTypeIsGenerallyReady(objType)) {
            return false;
        }
        if (objType.getLevel() > maxLevel) {
            return false;
        }
        if (objType.getLevel() < minLevel) {
            return false;
        }
        party += objType.getName() + ";";
        return true;
    }

    public void setGame(DC_Game game2) {
        this.game = game2;

    }

    public void setDungeon(String DUNGEON) {
        this.dungeon = DUNGEON;

    }

    // protected String getRandomDungeon() {
    // List<String> list = new ArrayList<>();
    // for (ObjType d : DataManager.getTypes(OBJ_TYPES.DUNGEONS)) {
    // if
    // (d.getProperty(G_PROPS.ARCADE_REGION).equalsIgnoreCase(DEFAULT_REGION))
    // list.add(d.getName());
    // }
    // if (list.isEmpty())
    // return dungeon;
    // return new RandomWizard<String>().getRandomListItem(list);
    // }

    public boolean getFAST_MODE() {
        return FAST_MODE;
    }

    public void setFAST_MODE(boolean FAST_MODE) {
        this.FAST_MODE = FAST_MODE;
    }

    public boolean getSUPER_FAST_MODE() {
        return SUPER_FAST_MODE;
    }

    public void setSUPER_FAST_MODE(boolean SUPER_FAST_MODE) {
        this.SUPER_FAST_MODE = SUPER_FAST_MODE;
    }


    public class CODE {
        public final static int PRESET = 0;
        public final static int RANDOM = 1;
        public final static int CHOOSE = 2;
        public final static int NONE = 3;
    }

}
