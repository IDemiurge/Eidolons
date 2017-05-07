package main.game.core.game;

import main.ability.ActionGenerator;
import main.ability.InventoryTransactionManager;
import main.ability.effects.DC_EffectManager;
import main.client.cc.gui.lists.dc.DC_InventoryManager;
import main.client.cc.logic.items.ItemGenerator;
import main.client.cc.logic.party.PartyObj;
import main.client.cc.logic.spells.SpellGenerator;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.content.DC_ValueManager;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.PROPERTY;
import main.data.XLinkedMap;
import main.data.xml.XML_Reader;
import main.entity.Ref;
import main.entity.obj.*;
import main.entity.obj.attach.DC_HeroAttachedObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.ai.AI_Manager;
import main.game.battlecraft.ai.tools.DC_Bf_Analyzer;
import main.game.battlecraft.logic.battlefield.*;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.options.UIOptions;
import main.game.bf.pathing.PathingManager;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.battlecraft.logic.battlefield.vision.VisionMaster;
import main.game.core.GameLoop;
import main.game.core.state.DC_GameState;
import main.game.core.state.DC_StateManager;
import main.game.battlecraft.logic.meta.arcade.ArcadeManager;
import main.game.battlecraft.logic.meta.arcade.ArenaArcadeMaster;
import main.game.battlecraft.logic.battle.arena.ArenaManager;
import main.game.battlecraft.logic.battle.BattleManager;
import main.game.battlecraft.logic.battle.BattleOptions;
import main.game.battlecraft.logic.battle.player.DC_Player;
import main.game.logic.battle.player.Player;
import main.game.battlecraft.logic.battle.player.PlayerMaster;
import main.game.battlecraft.logic.battle.turn.DC_TurnManager;
import main.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import main.game.battlecraft.rules.combat.damage.ArmorMaster;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.entity.active.DC_ActionManager;
import main.game.battlecraft.rules.DC_Rules;
import main.game.battlecraft.rules.mechanics.WaitRule;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.obj.drawing.GuiMaster;
import main.system.DC_ConditionMaster;
import main.system.DC_RequirementsManager;
import main.system.auxiliary.log.Chronos;
import main.system.datatypes.DequeImpl;
import main.system.entity.IdManager;
import main.system.entity.ValueHelper;
import main.system.graphics.AnimationManager;
import main.system.hotkey.DC_KeyManager;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;
import main.system.net.DC_IdManager;
import main.system.test.TestMasterContent;
import main.system.text.DC_LogManager;
import main.system.text.ToolTipMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.PresetLauncher;
import main.test.PresetMaster;
import main.test.debug.DebugMaster;

import java.util.*;

/**
 * contains references to everything that may be needed in scope of a single game
 * TODO refactor - put data into GameState!
 * <p>
 * init() should be called to create all Masters
 * battleInit() is a reset method
 * start() creates units and starts GameLoop (DC_TurnManager)
 */
public class DC_Game extends MicroGame {
    public static DC_Game game;
    protected DC_AttackMaster attackMaster;
    protected ArmorMaster armorMaster;
    protected ArmorMaster armorSimulator;
    private DC_GameManager manager;
    private VisionMaster visionMaster;
    private DebugMaster debugMaster;
    private TestMasterContent testMaster;
    private ArenaManager arenaManager;
    private BattleManager battleManager;

    private ToolTipMaster toolTipMaster;
    private PlayerMaster playerMaster;
    private GuiMaster guiMaster;
    private ArenaArcadeMaster arenaArcadeMaster;
    private BattleOptions arenaOptions;
    private InventoryTransactionManager inventoryTransactionManager;
    private DC_InventoryManager inventoryManager;
    private ArcadeManager arcadeManager;
    private AI_Manager aiManager;
    private AnimationManager animationManager;
    private DroppedItemManager droppedItemManager;
    private DungeonMaster dungeonMaster;
    private DC_KeyManager keyManager;
    private boolean AI_ON = true;
    private Thread gameLoopThread;
    private DC_Rules rules;
    private GAME_MODES gameMode;
    private GAME_TYPE gameType;
    private String playerParty;
    private Map<Unit, Map<String, DC_HeroAttachedObj>> simulationCache;
    private PartyObj party;
    private DC_GameData data;
    private boolean battleInit;
    private String enemyParty;
    private boolean paused;
    private Map<Coordinates, Map<Unit, DIRECTION>> directionMap;
    private HashMap<Coordinates, Map<Unit, FLIP>> flipMap;
    private boolean testMode;
    private boolean dummyPlus;
    private GameLoop loop;

    public DC_Game() {
        this(false);
    }

    public DC_Game(boolean simulation) {
        Game.game = this;
        game = this;
        this.simulation = true;
        this.simulation = simulation;
        setGameMode((isSimulation()) ? GAME_MODES.SIMULATION : GAME_MODES.ARENA);
        initState();
        initMasters();
    }

    public static void setGame(DC_Game game2) {
        game = game2;

    }

    public void initState() {

        setState(new DC_GameState(this));
    }

    public void initMasters() {

        master = new DC_GameMaster(this);
//        if (!CoreEngine.isArcaneVault()) {
        manager = new DC_GameManager(getState(), this);
        manager.init();
//        } //TODO FIX classdefnotfound!
        this.setIdManager(new DC_IdManager( this));
        guiMaster = new GuiMaster(this);
        armorMaster = new ArmorMaster(false);
        armorSimulator = new ArmorMaster(true);
        toolTipMaster = new ToolTipMaster(this);
        requirementsManager = new DC_RequirementsManager(this);
        valueManager = new DC_ValueManager(this);
        visionMaster = VisionManager.getMaster();
        actionManager = new DC_ActionManager(this);
        turnManager = new DC_TurnManager(this);
        movementManager = new DC_MovementManager(this);
        attackMaster = new DC_AttackMaster(this);
        mathManager = new DC_MathManager(this);
        effectManager = new DC_EffectManager(this);
        animationManager = new AnimationManager(this);
        droppedItemManager = new DroppedItemManager(this);
        dungeonMaster = new DungeonMaster(this);
        valueHelper = new ValueHelper(this);
        setTestMaster(new TestMasterContent(this));
        arenaArcadeMaster = new ArenaArcadeMaster(this);
        arcadeManager = new ArcadeManager(this);
        conditionMaster = new DC_ConditionMaster();
        logManager = new DC_LogManager(this);
        rules = new DC_Rules(this);
        if (!isSimulation()){
        keyManager = new DC_KeyManager( getManager());
        keyManager.init();
}
    }

    @Override
    public void init() {
        Chronos.mark("GAME_INIT");
        //TempEventManager.trigger("create-cell-object" + i + ":" + j, objects);

        initObjTypes();
        if (!CoreEngine.isLevelEditor() && (!CoreEngine.isArcaneVault() || !XML_Reader.isMacro())) {
            ItemGenerator.init();
            SpellGenerator.init();
            ActionGenerator.init();
        }

        if (PresetMaster.getPreset() != null) {
            PresetLauncher.launchPreset();
        }
        if (!simulation) {
            battleInit();
        }

        setInitialized(true);
        Chronos.logTimeElapsedForMark("GAME_INIT");
    }

    @Override
    public DC_BattleFieldManager getBattleFieldManager() {

        if (battleFieldManager == null) {
            battleFieldManager = new DC_BattleFieldManager(this );
        }
        return (DC_BattleFieldManager) super.getBattleFieldManager();
    }
//call each time Battlefield is launched
    public void battleInit() {

        inventoryTransactionManager = new InventoryTransactionManager(this);
        inventoryManager = new DC_InventoryManager(this);
        arenaManager = new ArenaManager(this);
        arenaManager.init();
        battleManager = new BattleManager(this);

        player1 = arenaManager.getPlayer();
        player2 = arenaManager.getEnemyPlayer();
        DC_Player.NEUTRAL.setGame(this);
        player1.setGame(this);
        player2.setGame(this);
        unitData1 = "";
        unitData2 = "";

            setOffline(true);
            if (isAI_ON()) {
                player2.setAi(true);
            } else {
                setAnalyzer(new DC_Bf_Analyzer((MicroGame) Game.game));
            }

        // if (battlefield == null) {

        battlefield = new DC_BattleField(new DC_BattleFieldGrid(getDungeon()));
        setGraveyardManager(new DC_GraveyardManager(this));
        battleFieldManager = new DC_BattleFieldManager(this );
        movementManager.setGrid(battlefield.getGrid());
        // }
        if (getUiOptions() == null) {
            setUiOptions(new UIOptions());
        }

        if (AI_ON) {
            aiManager = new AI_Manager(this);
            aiManager.init();
        }
        setBattleInit(true);
    }


    public void start(boolean first) {
        Chronos.mark("GAME_START");

        this.manager.setSbInitialized(true);
        turnManager.init();

        if (isDebugMode()) {
            debugMaster = new DebugMaster(getState() );
        }

        arenaManager.startGame();
        getGraveyardManager().init();

        getState().gameStarted(first);

        // TODO: 30.10.2016 insert gui init here

        startGameLoop();

        if (playerMaster == null) {
            playerMaster = new PlayerMaster(game, getPlayer(true), getPlayer(false));
        }
        playerMaster.initPlayerFlags();

        Chronos.logTimeElapsedForMark("GAME_START");
    }


    private void startGameLoop() {
        setRunning(true);

        if (getGameLoopThread() == null) {
            setGameLoopThread(new Thread(() -> {
                if (!CoreEngine.isGraphicsOff()) {
                    WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
                }
                loop = new GameLoop(this);
                loop.start();
                setStarted(true);
                main.system.auxiliary.log.LogMaster.log(1, "Game Loop exit ");
                return;

            }, "Game Loop"));
            getGameLoopThread().start();
        }

    }

    public DC_StateManager getStateManager() {
        return getManager().getStateManager();
    }

    public void stop() {
        setRunning(false);
        setStarted(false);
    }

    public MicroObj createAndInitUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        return createUnit(type, x, y, owner, ref);
    }

    @Override
    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        BattleFieldObject unit = ((BattleFieldObject) super.createUnit(type, x, y, owner, ref.getCopy()));
        game.getState().addObject(unit);
        unit.toBase();
        unit.resetObjects();
        unit.afterEffects();
        return unit;
    }

    public void destruct() {
        state.getObjMaps().clear();
        state.getObjects().clear();
        state.getAttachments().clear();
        state.getTriggers().clear();
        state.getTypeMap().clear();
        getUnits().clear();
        getStructures().clear();
        getCells().clear();
    }

    public void exit(boolean mainMenu) throws InterruptedException {
        // idManager;
        stop();
        dungeonMaster.setDungeon(null);
        getManager().setSelectedInfoObj(null);
        WaitRule.reset();
        for (Obj obj : getObjects(DC_TYPE.BUFFS)) {
            BuffObj buff = (BuffObj) obj;
            if (buff.isDispelable() || !buff.isPermanent()) {
                buff.kill();
            }
        }
        state.reset();
        logManager.clear();
        for (Obj obj : getUnits()) {
            if (!obj.getOriginalOwner().isMe()) {
                obj.kill(obj, false, true);
            }
            if (!mainMenu && obj.getOwner().isMe()) {
                continue;
            }

            state.removeObject(obj.getId());
        }
        if (mainMenu) {
            getMaster().clear();
        }

        for (Obj obj : getCells()) {
            obj.kill(obj, false, true);
            state.removeObject(obj.getId());
        }

    }

    @Override
    public void initObjTypes() {
        super.initObjTypes();
        if (Launcher.isMacroMode()) {
            for (OBJ_TYPE TYPE : MACRO_OBJ_TYPES.values()) {
                if (TYPE.getCode() == -1) {
                    continue;
                }
                initTYPE(TYPE);
            }
        }
    }

    public boolean isSkirmishOrScenario() {
        return getMaster().isSkirmishOrScenario();
    }

    public Obj getObjectVisibleByCoordinate(Coordinates c) {
        return getMaster().getObjectVisibleByCoordinate(c);
    }

    public DC_GameMaster getMaster() {
        return (DC_GameMaster) master;
    }


    public Obj getObjectByCoordinate(Coordinates c, boolean cellsIncluded) {
        return getMaster().getObjectByCoordinate(c, cellsIncluded);
    }


    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded, boolean passableIncluded, boolean overlayingIncluded) {
        return getMaster().getObjectByCoordinate(z, c, cellsIncluded, passableIncluded, overlayingIncluded);
    }

    public List<Unit> getOverlayingObjects(Coordinates c) {
        return getMaster().getOverlayingObjects(c);
    }

    public List<Unit> getObjectsOnCoordinate(Integer z, Coordinates c, Boolean overlayingIncluded, boolean passableIncluded, boolean cellsIncluded) {
        return getMaster().getObjectsOnCoordinate(z, c, overlayingIncluded, passableIncluded, cellsIncluded);
    }

    public List<DC_Cell> getCellsForCoordinates(List<Coordinates> coordinates) {
        return getMaster().getCellsForCoordinates(coordinates);
    }

    public Unit getUnitByCoordinate(Coordinates coordinates) {
        return getMaster().getUnitByCoordinate(coordinates);
    }

    public Collection<Unit> getUnitsForCoordinates(Coordinates... coordinates) {
        return getMaster().getUnitsOnCoordinates(coordinates);
    }

    public void removeUnit(Unit unit) {
        getMaster().removeUnit(unit);
    }

    public Set<Obj> getCells() {
        return getMaster().getCells();
    }

    public DequeImpl<Unit> getUnits() {
        return getMaster().getUnits();
    }

    public Map<Coordinates, List<Unit>> getUnitMap() {
        return getMaster().getUnitMap();
    }

    public Map<Coordinates, List<Unit>> getUnitCache() {
        return getMaster().getUnitCache();
    }

    @Override
    public IdManager getIdManager() {
        if (idManager != null) {
            return idManager;
        }

        return idManager = new DC_IdManager(  this);

    }




    public boolean isAI_ON() {
        return AI_ON;
    }


    public DC_GameManager getManager() {
        return manager;
    }

    public void setManager(DC_GameManager manager) {
        this.manager = manager;
    }


    public VisionMaster getVisionMaster() {
        return visionMaster;
    }

    @Override
    public DC_BattleField getBattleField() {
        return (DC_BattleField) super.getBattleField();
    }

    @Override
    public DC_GameState getState() {
        return (DC_GameState) state;
    }

    public synchronized DebugMaster getDebugMaster() {
        if (debugMaster == null) {
            if (getBattleField() != null) {
                debugMaster = new DebugMaster(getState()  );
            }
        }
        return debugMaster;
    }

    public DC_Rules getRules() {
        return rules;
    }

    public void setRules(DC_Rules rules) {
        this.rules = rules;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GAME_MODES getGameMode() {
        return gameMode;
    }

    public void setGameMode(GAME_MODES gameMode) {
        this.gameMode = gameMode;
    }

    public PathingManager getPathingManager() {
        return getMovementManager().getPathingManager();
    }

    public void setPlayerParty(String playerParty) {
        if (getPlayer(true) != null) {
            getPlayer(true).setPartyDataString(playerParty);
        }
        this.playerParty = playerParty;
    }

    public synchronized String getPlayerParty() {
        if (getParty() != null) {
            return getParty().getProperty(PROPS.MEMBERS);
        }

        return playerParty;
    }

    public void setPlayerParty(PartyObj party) {
        this.setParty(party);
    }

    // public Obj getUnitByCoordinate(Coordinates c) {
    // return getObjectByCoordinate(c, false);
    // }

    public InventoryTransactionManager getInventoryTransactionManager() {

        return inventoryTransactionManager;
    }


    public Set<Coordinates> getCoordinates() {
        return getBattleField().getGrid().getCellCompMap().keySet();
    }

    public AI_Manager getAiManager() {
        return aiManager;
    }

    @Override
    public DC_RequirementsManager getRequirementsManager() {
        return (DC_RequirementsManager) super.getRequirementsManager();
    }

    @Override
    public DC_Cell getCellByCoordinate(Coordinates coordinates) {
        return getMaster().getCellByCoordinate(coordinates);
    }

    public DC_Player getPlayer(boolean me) {
        if (playerMaster != null) {
            return playerMaster.getPlayer(me);
        }
        return (DC_Player) super.getPlayer(me);
    }

    public DC_HeroAttachedObj getSimulationObj(Unit dc_HeroObj, ObjType type, PROPERTY prop) {
        try {
            return getSimulationCache().get(dc_HeroObj).get(type.getName() + prop.getShortName());
        } catch (Exception e) {
            return null;
        }
    }

    public void addSimulationObj(Unit dc_HeroObj, ObjType type, DC_HeroAttachedObj item,
                                 PROPERTY prop) {

        Map<String, DC_HeroAttachedObj> cache = getSimulationCache().get(dc_HeroObj);
        if (cache == null) {
            cache = new XLinkedMap<>();
            getSimulationCache().put(dc_HeroObj, cache);
        }
        cache.put(type.getName() + prop.getShortName(), item);

    }

    public Map<Unit, Map<String, DC_HeroAttachedObj>> getSimulationCache() {
        if (simulationCache == null) {
            simulationCache = new XLinkedMap<>();
        }
        return simulationCache;
    }


    public BattleManager getBattleManager() {
        return battleManager;
    }


    public PartyObj getParty() {
        return party;
    }

    public void setParty(PartyObj party) {
        this.party = party;
    }

    public Thread getGameLoopThread() {
        return gameLoopThread;
    }

    public void setGameLoopThread(Thread gameLoopThread) {
        this.gameLoopThread = gameLoopThread;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public DroppedItemManager getDroppedItemManager() {
        return droppedItemManager;
    }

    public DC_AttackMaster getAttackMaster() {
        return attackMaster;
    }

    public DungeonMaster getDungeonMaster() {
        return dungeonMaster;
    }

    public DC_GameData getData() {
        if (data == null) {
            setData(new DC_GameData(""));
        }
        return data;
    }

    public void setData(DC_GameData data) {
        this.data = data;
    }


    public TestMasterContent getTestMaster() {
        return testMaster;
    }

    public void setTestMaster(TestMasterContent testMaster) {
        this.testMaster = testMaster;
    }

    public ArmorMaster getArmorMaster() {
        return armorMaster;
    }

    public ArmorMaster getArmorSimulator() {
        return armorSimulator;
    }

    @Override
    public DC_TurnManager getTurnManager() {
        return (DC_TurnManager) super.getTurnManager();
    }

    public ArcadeManager getArcadeManager() {
        return arcadeManager;
    }

    public boolean isBattleInit() {
        return battleInit;
    }

    public void setBattleInit(boolean battleInit) {
        this.battleInit = battleInit;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void checkAddUnit(Unit obj) {
        if (!obj.isHidden()) {
            if (!getUnits().contains(obj)) {
                getUnits().add(obj);
            }
        }
    }

    public Dungeon getDungeon() {
        if (getDungeonMaster() == null) {
            return null;
        }
        return getDungeonMaster().getDungeon();
    }


    public GAME_TYPE getGameType() {
        return gameType;
    }

    public void setGameType(GAME_TYPE game_mode) {
        this.gameType = game_mode;
    }


    public Unit getMainHero() {
        return (Unit) getPlayer(true).getHeroObj();
    }

    public Map<Coordinates, Map<Unit, FLIP>> getFlipMap() {
        if (flipMap == null) {
            flipMap = new HashMap<>();
        }
        return flipMap;
    }

    public Map<Coordinates, Map<Unit, DIRECTION>> getDirectionMap() {
        if (directionMap == null) {
            directionMap = new HashMap<>();
        }
        return directionMap;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getEnemyParty() {
        return enemyParty;
    }

    public void setEnemyParty(String property) {
        this.enemyParty = property;
        if (getPlayer(false) != null) {
            getPlayer(false).setPartyDataString(property);
        }
    }

    @Override
    public DC_LogManager getLogManager() {
        return (DC_LogManager) super.getLogManager();
    }

    public ToolTipMaster getToolTipMaster() {
        return toolTipMaster;
    }

    public ArenaArcadeMaster getArenaArcadeMaster() {
        return arenaArcadeMaster;
    }

    public GuiMaster getGuiMaster() {
        return guiMaster;
    }

    public boolean isDummyPlus() {
        return dummyPlus;
    }

    public void setDummyPlus(boolean dummyPlus) {
        this.dummyPlus = dummyPlus;
    }

    public DequeImpl<Structure> getStructures() {
        return getMaster().getStructures();
    }

    public DequeImpl<BattleFieldObject> getBfObjects() {
        DequeImpl<BattleFieldObject> list = new DequeImpl();
        list.addAll(getUnits());
        list.addAll(getStructures());

        return list;
    }

    public DC_InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public List<Unit> getObjectsOnCoordinate(Coordinates c) {
        return getMaster().getObjectsOnCoordinate(c);
    }

    public Obj getObjectByCoordinate(Coordinates
                                      c) {
        return getObjectByCoordinate(c, false);
    }

    public GameLoop getLoop() {
        return loop;
    }

    public void setLoop(GameLoop loop) {
        this.loop = loop;
    }

    public DC_KeyManager getKeyManager() {
        return keyManager;
    }


    public enum GAME_MODES {
        ARENA, SIMULATION, DUEL, ENCOUNTER, DUNGEON_CRAWL, ARENA_ARCADE
    }

    public enum GAME_TYPE {
        SCENARIO, ARCADE, SKIRMISH,

    }

}
