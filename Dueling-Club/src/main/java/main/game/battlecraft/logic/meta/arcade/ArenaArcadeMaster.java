package main.game.battlecraft.logic.meta.arcade;

import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.battlecraft.logic.battle.arena.UnitGroupMaster;
import main.game.battlecraft.logic.meta.party.PartyManager;
import main.game.battlecraft.logic.battle.arena.SpawnManager;
import main.game.battlecraft.logic.battle.BattleLevel;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.List;

public class ArenaArcadeMaster {
    private static final int DEFAULT_MAX_HEROES = 2;
    private static final String TEST_ARCADE = "Intro";
    ArenaArcade arcade;
    DC_Game game;
    String orPrefix = " OR ";
    String randomPrefix = " rand ";
    private BattleLevel battle;

    public ArenaArcadeMaster(DC_Game game) {
        this.game = game;

    }

    public static boolean isTestMode() {
        return true;
    }

    public void initBattle(BattleLevel battleLevel) {
        game.setEnemyParty(battleLevel.getGroupData());
        game.getDungeonMaster().setDungeonPath(battleLevel.getLevelData());
        UnitGroupMaster.setPowerLevel(arcade.getLevel());
        SpawnManager.setEnemyUnitGroupMode(true);
        // UnitGroupMaster.setUnitList(unitList);
    }

    public BattleLevel constructBattle() {
        int level = arcade.getLevel();
        arcade.toBase();
        String levels = arcade.getProperty(PROPS.ARCADE_LEVELS);
        String groups = arcade.getProperty(PROPS.ARCADE_ENEMY_GROUPS);
        groups = UnitGroupMaster.getGroupForLevel(groups, level);

        String groupData = StringMaster.openContainer(groups).get(level);
        String levelData = StringMaster.openContainer(levels).get(level);
        // support random/choice/...
        return new BattleLevel(arcade, levelData, groupData);
        // difficulty
    }

    public void init(PartyObj party) {
        if (arcade == null) {
            arcade = constructArcade(TEST_ARCADE);
            arcade.setParty(party);
            int maxHeroes = arcade.getIntParam(PARAMS.MAX_HEROES);
            if (maxHeroes == 0) {
                maxHeroes = DEFAULT_MAX_HEROES;
            }
            arcade.getParty().setParam(PARAMS.MAX_HEROES, maxHeroes, true, true);
            arcade.setParam(PARAMS.LEVEL, party.getIntParam(PARAMS.ARCADE_LEVEL));
        }
    }

    public void prebattle(ChoiceSequence cs) {
        if (isTestMode()) {

            battle = constructBattle();
            initBattle(battle);
        } else {
            // add some choices!
        }
    }

    public ArenaArcade constructArcade(String data) {
        ObjType type = DataManager.getType(data, DC_TYPE.ARCADES);
        return new ArenaArcade(type, game);
    }

    public void levelWon() {
        // award(battle);
        if ((1 + arcade.getLevel()) % 2 == 0) {
            arcade.getParty().modifyParameter(PARAMS.MAX_HEROES, 1);
        } else {
            PartyManager.levelUp();
        }
        arcade.getParty().modifyParameter(PARAMS.ARCADE_LEVEL, 1, true);
        // arcade.nextLevel();

    }

    public void arcadeWon(ArenaArcade a) {

    }

    public void continueArcade(PartyObj party) {
        if (arcade == null) {
            init(party);
        }

        battle = constructBattle();
        initBattle(battle);
        Launcher.launchDC();
        // level = chooseLevel(a);
        // launch(level);
    }

    public void saveArcade(ArenaArcade a) {
        a.getHeroData();
        a.getFactionData();
        XML_Converter.wrap("seed", RandomWizard.seed + "");
//		for (b level : a.getBattleLevels()) {
//		}
    }

    public void loadArcade(ArenaArcade a) {
//		setParams(a);
//		launch(a);
    }

    public List<BattleLevel> generateBattleLevels() {
        return null;
        // an option to choose among a certain number?
    }

    public void levelLost() {
        // TODO Auto-generated method stub

    }

    public enum ARCADE_TYPE {
        ARENA, MACRO, SCENARIO
    }

    public enum STANDARD_ARCADE_GROUP {

    }

    public enum STANDARD_ARCADE_LEVEL {
        Dungeon_Hall(""),;
        String battleData;

        private STANDARD_ARCADE_LEVEL(String battleData) {
            this.battleData = battleData;
        }
    }

}
