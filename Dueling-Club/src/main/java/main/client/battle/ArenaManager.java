package main.client.battle;

import main.client.battle.Battle.BATTLE_STATS;
import main.client.battle.BattleOptions.ARENA_GAME_OPTIONS;
import main.client.battle.BattleOptions.DIFFICULTY;
import main.content.CONTENT_CONSTS.MAP_BACKGROUND;
import main.content.PARAMS;
import main.data.XLinkedMap;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.DC_Game.GAME_MODES;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DC_ObjInitializer;
import main.game.battlefield.map.DC_Map;
import main.game.logic.dungeon.Dungeon;
import main.game.player.DC_Player;
import main.game.player.Player;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.net.data.MapData;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.CREATE_UNITS_MODEL;

public class ArenaManager {

    private static final String DEFAULT_SIDE = FACING_DIRECTION.NONE.toString();
    private static final String PLAYER_NAME = "You";
    private static final String ENEMY_NAME = "Enemy";
    private static final String DEFAULT_LIVES = "2";
    private static final String DEFAULT_TURNS_BETWEEN_WAVES = "2";
    private static final String DEFAULT_BACKGROUND = null;
    private static final String DEFAULT_TURNS_TO_PREPARE = "2";
    private static String DEFAULT_DIFFICULTY = DIFFICULTY.DISCIPLE.toString();
    private DC_Game game;
    private SpawnManager spawnManager;
    private Battle battle;
    private BattleOptions arenaOptions;
    private DC_Map map;
    private DC_Player player;
    private DC_Player enemyPlayer;
    private WaveAssembler waveAssembler;
    private List<String> partyTypes;
    private ObjType playerHero;
    private BattleConstructor constructor;
    private int battleLevel;

    public ArenaManager(DC_Game game) {
        this.game = game;
    }

    public static String getPlayerName() {
        return PLAYER_NAME;
    }

    public static String getEnemyName() {
        return ENEMY_NAME;
    }

    public static String getDEFAULT_DIFFICULTY() {
        return DEFAULT_DIFFICULTY;
    }

    public static void setDEFAULT_DIFFICULTY(String dEFAULT_DIFFICULTY) {
        DEFAULT_DIFFICULTY = dEFAULT_DIFFICULTY;
    }

    private void initArenaOptions() {
        // TODO
        arenaOptions = new BattleOptions();
        arenaOptions.setValue(ARENA_GAME_OPTIONS.NUMBER_OF_HEROES, ""
                + StringMaster.openContainer(game.getPlayerParty()).size());

        arenaOptions.setValue(ARENA_GAME_OPTIONS.PLAYER_STARTING_SIDE, DEFAULT_SIDE);

//        if (game.getDungeonMaster().getDungeon() != null)
//            arenaOptions.setValue(ARENA_GAME_OPTIONS.BACKGROUND, game.getDungeonMaster()
//                    .getDungeon().getMapBackground());
//        else
//            arenaOptions.setValue(ARENA_GAME_OPTIONS.BACKGROUND,
//                    (DEFAULT_BACKGROUND != null) ? DEFAULT_BACKGROUND : getRandomBackground());

        arenaOptions.setValue(ARENA_GAME_OPTIONS.DIFFICULTY, DEFAULT_DIFFICULTY);
        arenaOptions.setValue(ARENA_GAME_OPTIONS.LIVES, DEFAULT_LIVES);
        arenaOptions.setValue(ARENA_GAME_OPTIONS.TURNS_BETWEEN_WAVES, DEFAULT_TURNS_BETWEEN_WAVES);
        arenaOptions.setValue(ARENA_GAME_OPTIONS.TURNS_TO_PREPARE, DEFAULT_TURNS_TO_PREPARE);
    }

    private String getRandomBackground() {
        MAP_BACKGROUND[] array = MAP_BACKGROUND.values();
        MAP_BACKGROUND map = array[RandomWizard.getRandomInt(array.length)];
        return map.getBackgroundFilePath();
    }

    public void init() {
        // if (arenaOptions == null)
        initArenaOptions();
        initializeMap();
        initializePlayers();

        this.battle = new Battle();
        spawnManager = new SpawnManager(game, this);
        waveAssembler = new WaveAssembler(this);
        setConstructor(new BattleConstructor(this));
        getBattleConstructor().init();
    }

    public void startGame() {

        spawnManager.init();
        LogMaster.log(1, "init ");

        spawnManager.spawnParty(true);
        LogMaster.log(1, "spawnParty ");

        spawnManager.spawnParty(false);
        LogMaster.log(1, "spawnParty ");

        if (game.getData() != null) {
            LogMaster.log(1, "processing UnitData " + game.getData());
            DC_ObjInitializer.processUnitData(game.getData(), game);
            LogMaster.log(1, "processed UnitData ");
        }
        if (!ListMaster.isNotEmpty(game.getUnits())) {
            LogMaster.log(1, "NO UNITS! ");
        }

        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        GuiEventManager.trigger(CREATE_UNITS_MODEL, new EventCallbackParam(game.getUnits()));

        if (!game.isOffline()) {
            saveFacing();
        }
        initializeBattle();
        if (!game.isTestMode())
            if (game.isSkirmishOrScenario())
                try {
                    spawnManager.spawnDungeonCreeps(game.getDungeon());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            else if (isEncountersConstructed())
                try {
                    constructor.construct();
                } catch (Exception e) {
                    e.printStackTrace();
                }

    }

    private void saveFacing() {
        Map<Integer, FACING_DIRECTION> map = new XLinkedMap<>();
        for (DC_HeroObj u : game.getUnits()) {
            map.put(u.getId(), u.getFacing());
        }
        spawnManager.setMultiplayerFacingMap(map);
    }

    private boolean isEncountersConstructed() {
        if (game.isTestMode())
            return false;
        return game.getGameMode() == GAME_MODES.ARENA;
    }

    private void initializeMap() {
        try {
//            this.map = new DungeonMapGenerator().generateMap(game.getDungeonMaster().getDungeon());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map != null)
            return;
        this.map = new DC_Map(game, getMapOptions());
        map.setTheme(false);
        map.setBackground(getBackground());
        map.setName(getMapName());
    }

    public String getMapName() {
        Dungeon dungeon = game.getDungeonMaster().getDungeon();
        if (dungeon != null)
            return dungeon.getName();
        return StringMaster.cropFormat(StringMaster.getLastPathSegment(arenaOptions
                .getValue(ARENA_GAME_OPTIONS.BACKGROUND)));
    }

    private MapData getMapOptions() {
        return new MapData("");
    }

    private String getBackground() {
        // if (mapType == null)
        // mapType = new EnumMaster<MAP_BACKGROUND>()
        // .retrieveEnumConst(MAP_BACKGROUND.class, getArenaOptions()
        // .getValue(ARENA_GAME_OPTIONS.BACKGROUND));
        // return mapType.getBackgroundFilePath();
        return getArenaOptions().getValue(ARENA_GAME_OPTIONS.BACKGROUND);
    }

    private void initializePlayers() {
        player = new DC_Player(PLAYER_NAME, Color.WHITE, true); // emblem?
        try {
            player.setHero_type(getPlayerHeroName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        enemyPlayer = new DC_Player(ENEMY_NAME, Color.BLACK, false); // emblem?
        Player.ENEMY = enemyPlayer;
    }

    private void initializeBattle() {
        battle.setValue(BATTLE_STATS.PLAYER_STARTING_PARTY, game.getPlayerParty());
        battle.setValue(BATTLE_STATS.LEVEL, getBattleLevel() + "");
        battle.setValue(BATTLE_STATS.ROUND, "1");

    }

    public void newRound() {
        spawnManager.newRound();
        battle.setValue(BATTLE_STATS.ROUND, game.getState().getRound() + "");
    }

    public void newWave() {
        spawnManager.newWave();
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public DC_Map getMap() {
        return map;
    }

    public void setMap(DC_Map map) {
        this.map = map;
    }

    public DC_Player getPlayer() {
        return player;
    }

    public void setPlayer(DC_Player player) {
        this.player = player;
    }

    public DC_Player getEnemyPlayer() {
        return enemyPlayer;
    }

    public void setEnemyPlayer(DC_Player enemyPlayer) {
        this.enemyPlayer = enemyPlayer;
    }

    public ObjType getPlayerHeroName() {
        if (playerHero == null) {
            if (game.getParty() != null)
                playerHero = game.getParty().getLeader().getType();
            else
                try {
                    playerHero = new ObjType(game.getData().getEnemyParty().get(0));
                } catch (Exception e) {
                }
        }
        return playerHero;
    }

    public BattleOptions getArenaOptions() {
        return arenaOptions;
    }

    public void setArenaOptions(BattleOptions arenaOptions) {
        this.arenaOptions = arenaOptions;
    }

    public WaveAssembler getWaveAssembler() {
        return waveAssembler;
    }

    public DC_Game getGame() {
        return game;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public int getBattleLevel() {
        battleLevel = 0;

        List<? extends Obj> units = new LinkedList<>(game.getPlayer(true).getControlledUnits());
        if (units.isEmpty() && game.getParty() != null)
            units = new LinkedList<>(game.getParty().getMembers());
        for (Obj unit : units) {
            battleLevel += unit.getIntParam(PARAMS.POWER);
        }

        return battleLevel;
    }

    public BattleConstructor getBattleConstructor() {
        return constructor;
    }

    public void setConstructor(BattleConstructor constructor) {
        this.constructor = constructor;
    }
}
