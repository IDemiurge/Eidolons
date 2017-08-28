package main.game.core.game;

import main.ability.ActionGenerator;
import main.ability.InventoryTransactionManager;
import main.ability.effects.DC_EffectManager;
import main.client.cc.gui.lists.dc.DC_InventoryManager;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.DC_TYPE;
import main.content.DC_ValueManager;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.PROPERTY;
import main.data.XLinkedMap;
import main.data.xml.XML_Reader;
import main.entity.DC_IdManager;
import main.entity.Ref;
import main.entity.active.DC_ActionManager;
import main.entity.obj.*;
import main.entity.obj.attach.DC_HeroAttachedObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.ai.BfAnalyzer;
import main.game.battlecraft.ai.AI_Manager;
import main.game.battlecraft.logic.battle.test.TestBattleMaster;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.logic.battlefield.DC_BattleField;
import main.game.battlecraft.logic.battlefield.DC_BattleFieldManager;
import main.game.battlecraft.logic.battlefield.DroppedItemManager;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.battlecraft.logic.battlefield.vision.VisionMaster;
import main.game.battlecraft.logic.dungeon.test.TestDungeonMaster;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.game.battlecraft.rules.DC_Rules;
import main.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import main.game.battlecraft.rules.combat.damage.ArmorMaster;
import main.game.battlecraft.rules.mechanics.WaitRule;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.GraveyardManager;
import main.game.bf.MovementManager;
import main.game.core.DC_TurnManager;
import main.game.core.GameLoop;
import main.game.core.launch.LaunchDataKeeper;
import main.game.core.launch.PresetLauncher;
import main.game.core.master.combat.CombatMaster;
import main.game.core.state.DC_GameState;
import main.game.core.state.DC_StateManager;
import main.game.logic.battle.player.Player;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.system.DC_ConditionMaster;
import main.system.DC_RequirementsManager;
import main.system.GuiEventManager;
import main.system.auxiliary.log.Chronos;
import main.system.datatypes.DequeImpl;
import main.system.entity.IdManager;
import main.system.entity.ValueHelper;
import main.system.graphics.AnimationManager;
import main.system.hotkey.DC_KeyManager;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;
import main.system.test.TestMasterContent;
import main.system.text.DC_LogManager;
import main.system.util.Refactor;
import main.test.PresetMaster;
import main.test.debug.DebugMaster;

import java.util.*;

import static main.system.GuiEventType.UPDATE_LIGHT;

/**
 * contains references to everything that may be needed in scope of a single game
 * TODO refactor - put data into GameState!
 * <portrait>
 * init() should be called to create all Masters
 * battleInit() is a reset method
 * start() creates units and starts GameLoop (DC_TurnManager)
 */
public class DC_Game extends MicroGame {
    public static DC_Game game;

    protected MetaGameMaster metaMaster;
    protected DungeonMaster dungeonMaster;
    protected BattleMaster battleMaster;
    protected CombatMaster combatMaster;

    private DroppedItemManager droppedItemManager;
    private InventoryTransactionManager inventoryTransactionManager;
    private DC_InventoryManager inventoryManager;
    private DC_GameManager manager;

    private VisionMaster visionMaster;
    private DebugMaster debugMaster;
    private TestMasterContent testMaster;
    private AI_Manager aiManager;
    private AnimationManager animationManager; //heavy, but still just a trash!
    private DC_KeyManager keyManager; //where to move?

    private DC_Rules rules;

    private GAME_MODES gameMode;
    private GAME_TYPE gameType;
    private boolean battleInit; //to Battle!

    private boolean paused; //to game loop!

    private Map<Coordinates, Map<BattleFieldObject, DIRECTION>> directionMap; // ?!
    private HashMap<Coordinates, Map<BattleFieldObject, FLIP>> flipMap;

    private boolean testMode;
    private boolean dummyPlus;
    private boolean AI_ON = true;

    private Thread gameLoopThread;
    private GameLoop loop;
    private LaunchDataKeeper dataKeeper;
    @Refactor
    private Map<Unit, Map<String, DC_HeroAttachedObj>> simulationCache; //to simGame!

    public DC_Game() {
        this(false);
    }

    public DC_Game(boolean simulation) {
        Game.game = this;
        game = this;
        this.simulation = true;
        this.simulation = simulation;

        setGameMode((isSimulation()) ? GAME_MODES.SIMULATION : GAME_MODES.ARENA);

        setState(new DC_GameState(this));
        initMasters();
        init();
    }

    private void initMasters() {

        master = new DC_GameMaster(this);
//        if (!CoreEngine.isArcaneVault()) {
        manager = new DC_GameManager(getState(), this);
        manager.init();
//        } //TODO FIX classdefnotfound!
        this.setIdManager(new DC_IdManager(this));
        combatMaster = createCombatMaster();

        requirementsManager = new DC_RequirementsManager(this);
        valueManager = new DC_ValueManager(this);
        visionMaster = VisionManager.getMaster();
        mathManager = new DC_MathManager(this);
        effectManager = new DC_EffectManager(this);
        animationManager = new AnimationManager(this);
        droppedItemManager = new DroppedItemManager(this);
        valueHelper = new ValueHelper(this);
        setTestMaster(new TestMasterContent(this));
        conditionMaster = new DC_ConditionMaster();
        logManager = new DC_LogManager(this);
        rules = new DC_Rules(this);

        dungeonMaster = createDungeonMaster();
        battleMaster = createBattleMaster();
    }

    protected CombatMaster createCombatMaster() {
        return new CombatMaster(this);
    }

    @Override
    public void init() {
        Chronos.mark("GAME_INIT");
        //TempEventManager.trigger("create-cell-object" + i + ":" + j, objects);

        initObjTypes();
        if (!CoreEngine.isLevelEditor() && (!CoreEngine.isArcaneVault() ||
         !XML_Reader.isMacro())
         && !CoreEngine.isItemGenerationOff()
         )
            ItemGenerator.init();
//    TODO to battle init!
//            SpellGenerator.init();
//            ActionGenerator.init();
        //to engine!

        if (PresetMaster.getPreset() != null) {
            PresetLauncher.launchPreset();
        }

        setInitialized(true);
        Chronos.logTimeElapsedForMark("GAME_INIT");
    }

    protected BattleMaster createBattleMaster() {
        return new TestBattleMaster(this);
    }

    protected DungeonMaster createDungeonMaster() {
//        return new LocationMaster(this);
        return new TestDungeonMaster(this);
    }
    // before all other masters?

    // after meta
    public void dungeonInit() {
    }

    public void battleInit() {
//            SpellGenerator.init();
        setSimulation(false);    ActionGenerator.init();


        inventoryTransactionManager = new InventoryTransactionManager(this);
        inventoryManager = new DC_InventoryManager(this);

        battleMaster.init();
        dungeonMaster.init();

        setOffline(true);


        // if (battlefield == null) {

        battlefield = new DC_BattleField(new DC_BattleFieldGrid(getDungeon()));
         battleFieldManager = new DC_BattleFieldManager(this);
        getMovementManager().setGrid(battlefield.getGrid());
        // }


        if (AI_ON) {
            aiManager = new AI_Manager(this);
            aiManager.init();
        }
        setBattleInit(true);
    }

    public void start(boolean first) {
        Chronos.mark("GAME_START");
        this.manager.setSbInitialized(true); //TODO legacy?
        getTurnManager().init();

        if (isDebugMode()) {
            debugMaster = new DebugMaster(getState());
        }
        keyManager = new DC_KeyManager(getManager());
        keyManager.init();
        getGraveyardManager().init();//TODO in init?
        battleMaster.startGame();
        dungeonMaster.gameStarted();
        getState().gameStarted(first);
        if (getMetaMaster()!=null )
        getMetaMaster().gameStarted();
        GuiEventManager.trigger(UPDATE_LIGHT, null);

        // TODO: 30.10.2016 insert gui init here

        startGameLoop();

        Chronos.logTimeElapsedForMark("GAME_START");
    }

    private void startGameLoop() {
        setRunning(true);

        if (getGameLoopThread() == null) {
            setGameLoopThread(new Thread(() -> {
                if (!CoreEngine.isGraphicsOff()) {
//                    WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
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

    @Override
    public DC_BattleFieldManager getBattleFieldManager() {

        if (battleFieldManager == null) {
            battleFieldManager = new DC_BattleFieldManager(this);
        }
        return (DC_BattleFieldManager) super.getBattleFieldManager();
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
        // TODO review this! only for arcade-game, btw!
        stop();
//        dungeonMaster.setDungeon(null);
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

    public List<BattleFieldObject> getOverlayingObjects(Coordinates c) {
        return getMaster().getOverlayingObjects(c);
    }

    public List<BattleFieldObject> getObjectsOnCoordinate(Integer z, Coordinates c, Boolean overlayingIncluded, boolean passableIncluded, boolean cellsIncluded) {
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

    @Override
    public IdManager getIdManager() {
        return idManager;
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
                debugMaster = new DebugMaster(getState());
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

    @Deprecated
    public GAME_MODES getGameMode() {
        return gameMode;
    }

    @Deprecated
    public void setGameMode(GAME_MODES gameMode) {
        this.gameMode = gameMode;
    }

    public InventoryTransactionManager getInventoryTransactionManager() {

        return inventoryTransactionManager;
    }

    public BfAnalyzer getAnalyzer() {
        return getCombatMaster().getBfAnalyzer();
    }

    public CombatMaster getCombatMaster() {
        return combatMaster;
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
        return getBattleMaster().getPlayerManager().getPlayer(me);
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


    public DungeonMaster getDungeonMaster() {
        return dungeonMaster;
    }

    public TestMasterContent getTestMaster() {
        return testMaster;
    }

    public void setTestMaster(TestMasterContent testMaster) {
        this.testMaster = testMaster;
    }

    @Override
    public DC_TurnManager getTurnManager() {
        return combatMaster.getTurnManager();
    }

    @Override
    public MovementManager getMovementManager() {
        return combatMaster.getMovementManager();
    }

    @Override
    public GraveyardManager getGraveyardManager() {
        return combatMaster.getGraveyardManager();
    }

    public BfAnalyzer getBfAnalyzer() {
        return combatMaster.getBfAnalyzer();
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

    public Dungeon getDungeon() {
        return getDungeonMaster().getDungeonWrapper().getDungeon();
    }

    public GAME_TYPE getGameType() {
        return gameType;
    }

    public void setGameType(GAME_TYPE game_mode) {
        this.gameType = game_mode;
    }

    public Map<Coordinates, Map<BattleFieldObject, FLIP>> getFlipMap() {
        if (flipMap == null) {
            flipMap = new HashMap<>();
        }
        return flipMap;
    }

    public Map<Coordinates, Map<BattleFieldObject, DIRECTION>> getDirectionMap() {
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

    @Override
    public DC_LogManager getLogManager() {
        return (DC_LogManager) super.getLogManager();
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

    public BattleMaster getBattleMaster() {
        return battleMaster;
    }

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public LaunchDataKeeper getDataKeeper() {
        if (dataKeeper == null)
            dataKeeper = new LaunchDataKeeper();
        return dataKeeper;
    }

    public void setDataKeeper(LaunchDataKeeper dataKeeper) {
        this.dataKeeper = dataKeeper;
    }

    //how should these be called properly?
    @Refactor
    public DC_HeroAttachedObj getSimulationObj(Unit dc_HeroObj, ObjType type, PROPERTY prop) {
        try {
            return getSimulationCache().get(dc_HeroObj).get(type.getName() + prop.getShortName());
        } catch (Exception e) {
            return null;
        }
    }

    @Refactor
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

    @Override
    public DC_ActionManager getActionManager() {
        return combatMaster.getActionManager();
    }

    public DC_AttackMaster getAttackMaster() {
        return combatMaster.getAttackMaster();
    }

    public ArmorMaster getArmorMaster() {
        return combatMaster.getArmorMaster();
    }

    public ArmorMaster getArmorSimulator() {
        return combatMaster.getArmorSimulator();
    }

    public GameLoop getGameLoop() {
        return loop;
    }

    public enum GAME_MODES {
        ARENA, SIMULATION, DUEL, ENCOUNTER, DUNGEON_CRAWL, ARENA_ARCADE
    }

    public enum GAME_TYPE {
        SCENARIO, ARCADE, SKIRMISH, TEST,

    }



}
