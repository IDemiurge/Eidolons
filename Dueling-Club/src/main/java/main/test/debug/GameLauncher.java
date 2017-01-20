package main.test.debug;

import main.ability.UnitMaster;
import main.client.battle.SpawnManager;
import main.client.dc.Launcher;
import main.client.game.NetGame;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.UnitGroupMaster;
import main.game.battlefield.VisionManager;
import main.game.logic.dungeon.DungeonMaster;
import main.game.logic.macro.travel.EncounterMaster;
import main.rules.mechanics.RuleMaster;
import main.rules.mechanics.RuleMaster.RULE_SCOPE;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.launch.CoreEngine;
import main.system.test.TestMasterContent;
import main.test.Preset;
import main.test.PresetMaster;
import main.test.frontend.FAST_DC;

import java.util.List;

public class GameLauncher {
    private static GameLauncher instance;
    public final boolean NET_FAST_MODE = true;
    private final boolean factionMode = true;
    public int ENEMY_CODE = CODE.CHOOSE;
    public int PARTY_CODE = CODE.CHOOSE; // preset generic-code?
    public boolean VISION_HACK = false;
    // private boolean RANDOMIZE_PARTY = false;
    // private boolean RANDOMIZE_ENEMIES_PARTY = true;
    public boolean LEADER_MOVES_FIRST = false;
    public String ENEMY_PARTY = "Troglodyte;";
    public String PLAYER_PARTY = "Bandit Archer";
    public boolean DUMMY_MODE = false;
    public boolean DUMMY_PP = false;
    public Boolean FAST_MODE;
    public Boolean SUPER_FAST_MODE;
    private Integer PLAYER_CHOICE_OPTION = null;
    private Integer ENEMY_CHOICE_OPTION = 0;
    private Boolean host_client;
    private NetGame netGame;
    private DC_Game game;
    private String partyName;
    private String encounterName;
    private Integer unitGroupLevel;
    private boolean factionLeaderRequired;
    private String dungeon;

    public GameLauncher(DC_Game game, Boolean host_client) {
        this(game, null, null, host_client);
    }

    public GameLauncher(DC_Game game, Boolean FAST_MODE, Boolean SUPER_FAST_MODE,
                        Boolean host_client) {
        this.game = game;
        this.host_client = host_client;
        this.FAST_MODE = FAST_MODE;
        this.SUPER_FAST_MODE = SUPER_FAST_MODE;
        instance = this;
    }

    public static GameLauncher getInstance() {
        return instance;
    }

    private String initFactionData() {
        unitGroupLevel = BooleanMaster.isFalse(host_client) ? UnitGroupMaster.getPowerLevel()
                : DialogMaster.inputInt(UnitGroupMaster.getPowerLevel());
        UnitGroupMaster.setPowerLevel(unitGroupLevel);
        UnitGroupMaster.setFactionLeaderRequired(factionLeaderRequired);
        // Faction faction = chooseFaction();
        // if (random)
        UnitMaster.setRandom(false);

        return UnitGroupMaster.chooseGroup(true);

    }

    private void createPreset() {
        String enemy = ENEMY_PARTY;
        String party = PLAYER_PARTY;
        if (partyName != null)
            party = partyName;
        if (encounterName != null)
            enemy = encounterName;
        String levelFilePath = game.getDungeonMaster().getDungeon().getLevelFilePath();
        if (StringMaster.isEmpty(levelFilePath)) {
            levelFilePath = game.getDungeonMaster().getDungeon().getLevelFilePath();
        }
        Preset preset = PresetMaster.createPreset(party, enemy, levelFilePath, true);
        PresetMaster.setPreset(preset);
        PresetMaster.updatePreset();
    }

    private void savePresetAsLast() {
        if (!FAST_MODE)
            if (!SUPER_FAST_MODE)
        PresetMaster.savePreset(PresetMaster.getPreset(), null);
    }

    private void autosavePreset() {
        PresetMaster.savePreset(PresetMaster.getPreset(), true);
    }

    private void initMultiplayerFlags() {
        DUMMY_MODE = false;
        VISION_HACK = true;
    }

    public void selectiveInit() {

        if (FAST_MODE == null)
            FAST_MODE = DialogMaster.confirm("FAST_MODE?");
        if (SUPER_FAST_MODE == null) {
            SUPER_FAST_MODE = DialogMaster.confirm("SUPER_FAST_MODE?");
        }

        PLAYER_PARTY = choosePlayerUnits(PLAYER_CHOICE_OPTION);
        ENEMY_PARTY = chooseEnemies(ENEMY_CHOICE_OPTION);

        String dungeon = game.getDungeonMaster().chooseDungeonLevel();
        if (dungeon != null) {
            game.getDungeonMaster().setDungeonPath(dungeon);
            game.getDungeonMaster().initDungeon(dungeon);
        }

        initData();
    }

    public DC_Game initDC_Game() {

        initData();
        if (PresetMaster.getPreset() == null) {

            game.setDebugMode(Launcher.isDEBUG_MODE_DEFAULT());
            initPlayerParties();
            if (PARTY_CODE != CODE.NONE)
                game.setPlayerParty(PLAYER_PARTY);
            if (ENEMY_CODE != CODE.NONE)
                game.setEnemyParty(ENEMY_PARTY);

            game.init();
        } else {
            game.init();

        }

        if (DUMMY_MODE) {
            RuleMaster.setScope(RULE_SCOPE.TEST);
            game.setDummyMode(true);
            game.setDummyPlus(DUMMY_PP);
            TestMasterContent.setForceFree(!DUMMY_PP);
        }
        try {
            if (PresetMaster.getPreset() == null // &&
                    // !BooleanMaster.isTrue(FAST_MODE)
                    && !SUPER_FAST_MODE) {
                createPreset();
                autosavePreset();
            }
            savePresetAsLast();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public void initData() {
        if (FAST_MODE == null)
            FAST_MODE = false;
        if (SUPER_FAST_MODE == null)
            SUPER_FAST_MODE = false;

        if (game == null)
            game = new DC_Game(false);
        game.setPlayerParty(PLAYER_PARTY);
        game.setEnemyParty(ENEMY_PARTY);
        if (host_client != null)
            initMultiplayerFlags();
        DC_Game.setGame(game);
        // select code?
        VisionManager.setVisionHacked(VISION_HACK);
        DebugMaster.setOmnivisionOn(VISION_HACK);

        if (PresetMaster.getPreset() == null){
            if (BooleanMaster.isTrue(FAST_MODE)  ) {
                if (netGame == null && dungeon == null)
                    DungeonMaster.RANDOM_DUNGEON = true;
                else
                    DungeonMaster.setDEFAULT_DUNGEON_LEVEL(dungeon);
            }

            if (BooleanMaster.isTrue(getFAST_MODE())) {
                DungeonMaster.RANDOM_DUNGEON = true;
                CoreEngine.animationTestMode = true;
                TestMasterContent.test_on = false;
            }
            if (BooleanMaster.isTrue(getSUPER_FAST_MODE())) {
           PLAYER_PARTY=(FAST_DC.PLAYER_PARTY);
            ENEMY_PARTY="";
            game.setEnemyParty("");
        }
        }
        if (game.getArenaManager() != null)
            game.getArenaManager().getSpawnManager().init();
    }

    private void initPlayerParties() {
        switch (PARTY_CODE) {
            case CODE.CHOOSE:
                PLAYER_PARTY = choosePlayerUnits(PLAYER_CHOICE_OPTION);
                break;
            case CODE.RANDOM:
                ENEMY_PARTY = randomizeParty();
                break;
        }

        switch (ENEMY_CODE) {

            case CODE.CHOOSE:
                ENEMY_PARTY = chooseEnemies(ENEMY_CHOICE_OPTION);
                break;
            case CODE.RANDOM:
                ENEMY_PARTY = randomizeEnemies();
                break;
            // case CODE.NONE:
            // break;
        }
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
        game.setTestMode(true);
        if (OPTION == null)
            OPTION = DialogMaster.optionChoice("Select party init option", "Group", "Default",
                    "Heroes", "Units", "Party");
        switch (OPTION) {
            case 0:
                SpawnManager.setPlayerUnitGroupMode(true);
                // String flip = ListChooser.chooseEnum(FLIP.class);
                // if (flip != null)
                // UnitGroupMaster.setFlip(new
                // EnumMaster<FLIP>().retrieveEnumConst(FLIP.class,
                // flip));
                if (UnitGroupMaster.isFactionMode())
                    return initFactionData();
                else
                    return UnitGroupMaster.chooseGroup(true);
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

    private String chooseParty() {
        ObjType party = ListChooser.chooseType_(DataManager
                .getTypesGroup(OBJ_TYPES.PARTY, "Preset"), OBJ_TYPES.PARTY);
        return party.getProperty(PROPS.MEMBERS);
    }

    public String chooseCharacters() {
        return choose(OBJ_TYPES.CHARS);
    }

    public String chooseUnits() {
        return choose(OBJ_TYPES.UNITS);
    }

    public String choose(OBJ_TYPES type) {
        String filterGroup = getFilterGroup(type);
        List<String> data = DataManager.getTypeNames(type);
        if (!filterGroup.isEmpty())
            data = DataManager.getTypesSubGroupNames(type, filterGroup);

        String objects = new ListChooser(SELECTION_MODE.MULTIPLE, data, type).choose();
        return objects;
    }

    // TODO DIFFERENT PER MODE!
    private String getFilterGroup(OBJ_TYPES type) {
        if (type == OBJ_TYPES.CHARS) {
            return StringMaster.BATTLE_READY;
        }
        if (type == OBJ_TYPES.UNITS) {
            return "";
        }
        if (type == OBJ_TYPES.ENCOUNTERS) {
            return StringMaster.BATTLE_READY;
        }
        if (type == OBJ_TYPES.PARTY) {
            return StringMaster.BATTLE_READY;
        }
        return StringMaster.PRESET;
    }

    public String chooseEnemies(Integer ENEMY_OPTION) {
        if (ENEMY_OPTION == null)
            ENEMY_OPTION = DialogMaster.optionChoice("Select Enemy init option", "Group",
                    "Encounter", "Heroes", "Units", "Default");
        switch (ENEMY_OPTION) {
            case 0:
                SpawnManager.setEnemyUnitGroupMode(true);
                return UnitGroupMaster.chooseGroup(false);
            case 1:
                encounterName = ListChooser.chooseType(OBJ_TYPES.ENCOUNTERS);
                if (encounterName != null)
                    return getEnemiesFromWave(DataManager.getType(encounterName,
                            OBJ_TYPES.ENCOUNTERS));
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

    private String randomizeEnemies() {
        ObjType type = DataManager.getRandomType(OBJ_TYPES.ENCOUNTERS, null);
        return getEnemiesFromWave(type);
    }

    private String getEnemiesFromWave(ObjType type) {
        Integer power = EncounterMaster.getPower(type, null);
        if (power < EncounterMaster.getPower(StringMaster.openContainer(PLAYER_PARTY)))
            return (type.getProperty(PROPS.EXTENDED_PRESET_GROUP));
        else if (power > 2 * EncounterMaster.getPower(StringMaster.openContainer(PLAYER_PARTY)))
            return (type.getProperty(PROPS.SHRUNK_PRESET_GROUP));
        else
            return (type.getProperty(PROPS.PRESET_GROUP));
    }

    private String getRandomizedParty(int heroesCount, int unitCount, Integer maxLevel,
                                      Integer minLevel) {
        ObjType mainHero = null;
        Loop.startLoop(100);
        while (Loop.loopContinues()) {
            ObjType type = RandomWizard.getRandomType(OBJ_TYPES.CHARS);
            if (maxLevel != null)
                if (type.getLevel() > maxLevel)
                    continue;
            if (minLevel != null)
                if (type.getLevel() < minLevel)
                    continue;
            mainHero = type;
        }
        String party = mainHero.getName() + ";";
        Loop.startLoop(100);
        while (unitCount > 0 && Loop.loopContinues()) {
            OBJ_TYPE TYPE = OBJ_TYPES.UNITS;
            boolean subgroup = true;
            String property = mainHero.getProperty(G_PROPS.DEITY);

            if (!DataManager.isTypeName(property)) {
                property = mainHero.getProperty(G_PROPS.ASPECT);
                subgroup = false;
            }
            if (addRandomUnit(TYPE, property, subgroup, party, minLevel, maxLevel))
                unitCount--;
        }
        Loop.startLoop(100);
        while (heroesCount > 0 && Loop.loopContinues()) {
            // party += RandomWizard.getRandomType(OBJ_TYPES.CHARS).getName() +
            // ";";
            if (addRandomUnit(OBJ_TYPES.CHARS, null, false, party, minLevel, maxLevel))
                heroesCount--;
        }
        return party;
    }

    private boolean addRandomUnit(OBJ_TYPE TYPE, String property, boolean subgroup, String party,
                                  Integer maxLevel, Integer minLevel) {

        ObjType objType = RandomWizard.getRandomType(TYPE, property, subgroup);
        if (objType.checkProperty(G_PROPS.GROUP, "Background"))
            return false;
        if (!WorkspaceMaster.checkTypeIsGenerallyReady(objType))
            return false;
        if (objType.getLevel() > maxLevel)
            return false;
        if (objType.getLevel() < minLevel)
            return false;
        party += objType.getName() + ";";
        return true;
    }

    public void setGame(DC_Game game2) {
        this.game = game2;

    }

    public void setDungeon(String DUNGEON) {
        this.dungeon = DUNGEON;

    }

    // private String getRandomDungeon() {
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

    public Boolean getFAST_MODE() {
        return FAST_MODE;
    }

    public void setFAST_MODE(Boolean FAST_MODE) {
        this.FAST_MODE = FAST_MODE;
    }

    public Boolean getSUPER_FAST_MODE() {
        return SUPER_FAST_MODE;
    }

    public void setSUPER_FAST_MODE(Boolean SUPER_FAST_MODE) {
        this.SUPER_FAST_MODE = SUPER_FAST_MODE;
    }

    public class CODE {
        public final static int PRESET = 0;
        public final static int RANDOM = 1;
        public final static int CHOOSE = 2;
        public final static int NONE = 3;
    }

}
