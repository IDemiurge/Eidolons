package eidolons.game.core.game;

import eidolons.ability.ActionGenerator;
import eidolons.ability.InventoryTransactionManager;
import eidolons.ability.effects.DC_EffectManager;
import eidolons.content.values.DC_ValueManager;
import eidolons.content.values.ValueHelper;
import eidolons.entity.DC_IdManager;
import eidolons.entity.mngr.action.DC_ActionManager;
import eidolons.entity.mngr.item.DC_InventoryManager;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.GridCell;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.attach.DC_HeroAttachedObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.logic.battlefield.DC_BattleFieldManager;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.battlefield.DroppedItemManager;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.mission.quest.QuestMissionMaster;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.game.battlecraft.rules.DC_Rules;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.battlecraft.rules.combat.damage.armor.ArmorMaster;
import eidolons.game.core.*;
import eidolons.game.core.atb.AtbController;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.launch.LaunchDataKeeper;
import eidolons.game.core.master.combat.CombatMaster;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.game.exploration.handlers.ExploreGameLoop;
import eidolons.entity.mngr.item.ItemGenerator;
import eidolons.system.DC_ConditionMaster;
import eidolons.system.DC_RequirementsManager;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.hotkey.DC_KeyManager;
import eidolons.system.math.DC_MathManager;
import eidolons.system.test.TestMasterContent;
import eidolons.system.text.DC_GameLogManager;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.PROPERTY;
import main.data.XLinkedMap;
import main.data.xml.XML_Reader;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.GraveyardManager;
import main.game.bf.directions.DIRECTION;
import main.game.core.game.Game;
import main.game.core.game.GenericGame;
import main.game.core.state.GameState;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.Refactor;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.entity.IdManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.launch.Launch;

import java.util.*;

import static main.system.launch.Flags.isCombatGame;

/**
 * contains references to everything that may be needed in scope of a single game TODO refactor - put data into
 * GameState! init() should be called to create all Masters battleInit() is a reset method start() creates units and
 * starts GameLoop (DC_TurnManager)
 */
public class DC_Game extends GenericGame {
    public static DC_Game game;

    protected MetaGameMaster metaMaster;
    protected DungeonMaster dungeonMaster;
    protected MissionMaster missionMaster;
    protected CombatMaster combatMaster;
    protected MusicMaster musicMaster;

    protected InventoryTransactionManager inventoryTransactionManager;
    protected DC_InventoryManager inventoryManager;
    protected DC_GameManager manager;

    protected VisionMaster visionMaster;
    protected TestMasterContent testMaster;
    protected AI_Manager aiManager;
    protected DC_KeyManager keyManager; //where to move?

    protected DC_Rules rules;
    private DroppedItemManager droppedItemManager;

    protected GAME_MODES gameMode;
    protected GAME_TYPE gameType;
    protected boolean battleInit; //to Battle!


    protected Map<Coordinates, Map<BattleFieldObject, DIRECTION>> directionMap; // ?!
    protected Map<Coordinates, FLIP> flipMap = new LinkedHashMap<>();

    protected boolean testMode;
    protected boolean dummyPlus;
    protected boolean AI_ON = true;

    protected GameLoop loop;
    protected CombatLoop combatLoop;
    protected ExploreGameLoop exploreLoop;
    protected LaunchDataKeeper dataKeeper;
    @Refactor
    protected Map<BattleFieldObject, Map<String, DC_HeroAttachedObj>> simulationCache; //to simGame!
    protected DC_BattleFieldGrid grid;

    public DC_Game() {
        this(false);
    }

    public DC_Game(boolean simulation) {
        this(simulation, true);
    }

    public DC_Game(boolean simulation, boolean readyToInit) {
        Game.game = this;
        game = this;
        this.simulation = simulation;

        setGameMode((isSimulation()) ? GAME_MODES.SIMULATION : GAME_MODES.ARENA);

        setState(new DC_GameState(this));
        if (readyToInit) {
            firstInit();

        }
    }

    protected void firstInit() {
        initMasters();
        if (isCombatGame())
            initGameLoops();
        init();
    }

    public void initGameLoops() {
        exploreLoop = new ExploreGameLoop(this);
        combatLoop = new CombatLoop(this);
        loop = exploreLoop;
    }

    public void initMasters() {
        initMasters(false);
    }

    public void initMasters(boolean nextLevel) {

        setIdManager(new DC_IdManager(this));
        objMaster = new DC_GameObjMaster(this);
        manager = createGameManager();
        manager.init();

        combatMaster = createCombatMaster();

        requirementsManager = new DC_RequirementsManager();
        valueManager = new DC_ValueManager(this);
        if (!isSimulation())
            visionMaster = createVisionMaster();
        mathManager = new DC_MathManager(this);
        effectManager = new DC_EffectManager(this);
        setTestMaster(createTestMaster());
        conditionMaster = new DC_ConditionMaster();
        if (!isSimulation()) {
            logManager = new DC_GameLogManager(this);
        }
        if (!CoreEngine.isArcaneVault())
            rules = new DC_Rules(this);

        if (!isCombatGame() && !Flags.isDungeonTool() && !CoreEngine.isLevelEditor())
            return;
        if (isSimulation()) {
            return;
        }
        ExplorationMaster master = null;
        if (nextLevel) {
            master = dungeonMaster.getExplorationMaster();
        }
        dungeonMaster = createDungeonMaster();

        //TODO DC main - transit
        if (nextLevel)
            dungeonMaster.setExplorationMaster(master);
        if (!isCombatGame())
            return;
        missionMaster = createBattleMaster();
        musicMaster = MusicMaster.getInstance();
    }

    protected TestMasterContent createTestMaster() {
        return new TestMasterContent(this);
    }

    protected VisionMaster createVisionMaster() {
        return new VisionMaster(this);
    }

    protected DC_GameManager createGameManager() {
        return new DC_GameManager(getState(), this);
    }

    protected CombatMaster createCombatMaster() {
        return new CombatMaster(this);
    }

    @Override
    public void init() {
        Chronos.mark("GAME_INIT");

        initObjTypes();

        setInitialized(true);
        keyManager = createKeyManager();
        Chronos.logTimeElapsedForMark("GAME_INIT");
    }

    protected DC_KeyManager createKeyManager() {
        return new DC_KeyManager(getManager());
    }

    protected MissionMaster createBattleMaster() {
        return new QuestMissionMaster(this);
    }

    protected boolean isLocation() {
        return Flags.isMainGame();
    }

    protected DungeonMaster createDungeonMaster() {
        return new LocationMaster(this);
    }

    Map<Module, GameState> stateMap = new HashMap<>();

    public void enterModule(Module module) {
        if (grid == null) {
            grid = new DC_BattleFieldGrid(module);
        } else
            grid.setModule(module);

        if (state != null) {
            stateMap.put(module, state);
        } else {
            state = stateMap.get(module);
            if (state == null) {
                state = new DC_GameState(this);
                stateMap.put(module, state);
            }
        }
    }

    public void battleInit() {
        setSimulation(false);
        if (!isCombatGame()) {
            dungeonMaster.init();
            return;
        }
        ActionGenerator.init();

        getVisionMaster().getIllumination().clearCache();
        inventoryTransactionManager = new InventoryTransactionManager(this);
        inventoryManager = new DC_InventoryManager();
        missionMaster.init();
        if (AI_ON) {
            aiManager = new AI_Manager(this);
        }
        dungeonMaster.init();

        battleFieldManager = new DC_BattleFieldManager(this, getModule().getId(),
                getModule().getEffectiveWidth(), getModule().getEffectiveHeight());

        droppedItemManager = new DroppedItemManager(this);
        droppedItemManager.init();

        getDungeonMaster().getModuleLoader().loadInitial();
        getDungeonMaster().loadingDone();
        // getMetaMaster().loadingDone();
        if (AI_ON) {
            aiManager.init();
        }
        setBattleInit(true);
    }

    public Module getModule() {
        return getMetaMaster().getModuleMaster().getCurrent();
    }

    public void start(boolean first) {
        Chronos.mark("GAME_START");
        this.manager.setSbInitialized(true); //TODO legacy?
        getTurnManager().init();

        keyManager.init();
        getGraveyardManager().init();//TODO in init?
        // missionMaster.startGame();

        if (getMetaMaster() != null)
            getMetaMaster().gameStarted();
        dungeonMaster.gameStarted();

        if (dungeonMaster.getExplorationMaster() != null) {
            dungeonMaster.getExplorationMaster().init();

        }
        //        visionMaster.refresh();
        getMetaMaster().getDialogueManager().introDialogue();
        DialogueManager.afterDialogue(() -> {
            fireEvent(new Event(Event.STANDARD_EVENT_TYPE.INTRO_FINISHED, new Ref()));
        });
        startGameLoop(first);
        getManager().reset();


        Chronos.logTimeElapsedForMark("GAME_START");
        GuiEventManager.trigger(GuiEventType.GAME_STARTED, this);
    }

    public void startGameLoop() {
        startGameLoop(true);
        //        getVisionMaster().refresh();
    }


    public void startGameLoop(boolean first) {
        //        if (MusicMaster.isOn())
        //            GuiEventManager.trigger(MUSIC_START, null);

        boolean explore = ExplorationMaster.isExplorationOn();
        getState().gameStarted(first); // ?

        if (explore) {
            startExploration();
        } else {
            startCombat();
        }

        setRunning(true);
        setStarted(true);

    }

    private void startExploration() {

        loop = exploreLoop;
        if (combatLoop.isStarted()) {
            combatLoop.stop();
            try {
                combatLoop.endCombat();
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }
        }
        if (exploreLoop.isStarted())
            exploreLoop.resume();
        else
            exploreLoop.startInNewThread();
        if (isStarted())
            musicMaster.scopeChanged(MusicEnums.MUSIC_SCOPE.ATMO);
        getStateManager().newRound();


    }

    private void startCombat() {
        loop = combatLoop;
        exploreLoop.stop();
        EUtils.showInfoText("The Battle is Joined!");
        getState().setChaosLevel(0);
        if (!combatLoop.isStarted() || !combatLoop.checkThreadIsRunning()
            //                CoreEngine.isIggDemoRunning()
        )
            loop.startInNewThread();
        else
            combatLoop.resume();

        musicMaster.scopeChanged(MusicEnums.MUSIC_SCOPE.BATTLE);

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
        return (DC_BattleFieldManager) super.getBattleFieldManager();
    }

    @Override
    public MicroObj createObject(ObjType type, int x, int y, Player owner, Ref ref) {
        BattleFieldObject unit = ((BattleFieldObject) super.createObject(type, x, y, owner, ref.getCopy()));
        game.getState().addObject(unit);
        unit.toBase();
        unit.resetObjects();
        unit.afterEffects();
        return unit;
    }


    @Override
    public void initObjTypes() {
        super.initObjTypes();
        if (Flags.isMacro()) {
            for (OBJ_TYPE TYPE : MACRO_OBJ_TYPES.values()) {
                if (TYPE.getCode() == -1) {
                    continue;
                }
                initTYPE(TYPE);
            }
        }
        if (!CoreEngine.isLevelEditor())
            if ((!CoreEngine.isArcaneVault()
                    || !XML_Reader.isMacro())
                    && !Flags.isItemGenerationOff()
            ) {
                itemGenerator = new ItemGenerator(Flags.isFastMode());
                itemGenerator.init();
            }

    }

    public Obj getObjectVisibleByCoordinate(Coordinates c) {
        return getObjMaster().getObjectVisibleByCoordinate(c);
    }

    public DC_GameObjMaster getObjMaster() {
        return (DC_GameObjMaster) objMaster;
    }

    public Obj getObjectByCoordinate(Coordinates c, Boolean overlayingIncluded) {
        return getObjMaster().getObjectByCoordinate(c, overlayingIncluded);
    }

    public Set<BattleFieldObject> getOverlayingObjects(Coordinates c) {
        return getObjMaster().getOverlayingObjects(c);
    }

    public Set<BattleFieldObject> getObjectsNoOverlaying(Coordinates c) {
        return getObjMaster().getObjectsOnCoordinate(c, false);
    }

    public Set<BattleFieldObject> getObjectsOnCoordinateNoOverlaying(Coordinates c) {
        return getObjectsOnCoordinate(c, false);
    }

    public Set<BattleFieldObject> getObjectsOnCoordinateAll(Coordinates c) {
        return getObjMaster().getObjectsOnCoordinate(c, true);
    }

    public Set<BattleFieldObject> getObjectsOnCoordinate(Coordinates c, Boolean overlayingIncluded) {
        return getObjMaster().getObjectsOnCoordinate(c, overlayingIncluded);
    }

    public Set<GridCell> getCellsForCoordinates(Set<Coordinates> coordinates) {
        return getObjMaster().getCellsForCoordinates(coordinates);
    }

    public Unit getUnitByCoordinate(Coordinates coordinates) {
        return getObjMaster().getUnitByCoordinate(coordinates);
    }

    public Collection<Unit> getUnitsForCoordinates(Coordinates... coordinates) {
        return getObjMaster().getUnitsOnCoordinates(coordinates);
    }

    @Override
    public void remove(Obj obj) {
        super.remove(obj);
        if (obj instanceof BattleFieldObject) {
            if (((BattleFieldObject) obj).isWall()) {
                manager.setWallResetRequired(true);
            }
            softRemove((BattleFieldObject) obj);
        }
    }


    public void softRemove(BattleFieldObject obj) {
        //leave obj in gamestate for refs
        if (obj instanceof Unit) {
            removeUnit((Unit) obj);
        } else {
            getObjMaster().removeStructure((Structure) obj);
        }
    }

    public void removeUnit(Unit unit) {
        getObjMaster().removeUnit(unit);
    }

    public Set<GridCell> getCells() {
        return getObjMaster().getCellsSet();
    }

    public Set<Unit> getUnits() {
        return getObjMaster().getUnits();
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
    public DC_GameState getState() {
        return (DC_GameState) state;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        super.setDebugMode(debugMode);
    }

    public DC_Rules getRules() {
        return rules;
    }

    public void setRules(DC_Rules rules) {
        this.rules = rules;
    }

    public GAME_MODES getGameMode() {
        return gameMode;
    }

    public void setGameMode(GAME_MODES gameMode) {
        this.gameMode = gameMode;
    }

    public InventoryTransactionManager getInventoryTransactionManager() {

        return inventoryTransactionManager;
    }


    public CombatMaster getCombatMaster() {
        return combatMaster;
    }

    public Set<Coordinates> getCoordinates() {
        return grid.getCoordinatesSet();
    }

    public AI_Manager getAiManager() {
        return aiManager;
    }

    @Override
    public DC_RequirementsManager getRequirementsManager() {
        return (DC_RequirementsManager) super.getRequirementsManager();
    }

    @Override
    public GridCell getCell(Coordinates coordinates) {
        return getObjMaster().getCellByCoordinate(coordinates);
    }

    public DC_Player getPlayer(boolean me) {
        return getMissionMaster().getPlayerManager().getPlayer(me);
    }

    public Thread getGameLoopThread() {
        return loop.getThread();
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
    public GenericTurnManager getTurnManager() {
        return combatMaster.getTurnManager();
    }

    public AtbController getAtbController() {
        return ((AtbTurnManager) combatMaster.getTurnManager()).getAtbController();
    }

    @Override
    public DC_MovementManager getMovementManager() {
        return (DC_MovementManager) combatMaster.getMovementManager();
    }

    @Override
    public GraveyardManager getGraveyardManager() {
        return combatMaster.getGraveyardManager();
    }

    public boolean isBattleInit() {
        return battleInit;
    }

    public void setBattleInit(boolean battleInit) {
        this.battleInit = battleInit;
    }

    public boolean isPaused() {
        return getLoop().isPaused();
    }


    public Floor getDungeon() {
        try {
            return getDungeonMaster().getFloorWrapper().getFloor();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return null;
    }

    public GAME_TYPE getGameType() {
        return gameType;
    }

    public void setGameType(GAME_TYPE game_mode) {
        this.gameType = game_mode;
    }

    public Map<Coordinates, FLIP> getFlipMap() {
        return flipMap;
    }

    public void setFlipMap(Map<Coordinates, FLIP> flipMap) {
        this.flipMap = flipMap;
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
    public DC_GameLogManager getLogManager() {
        return (DC_GameLogManager) super.getLogManager();
    }

    public boolean isDummyPlus() {
        return dummyPlus;
    }

    public void setDummyPlus(boolean dummyPlus) {
        this.dummyPlus = dummyPlus;
    }

    public Set<Structure> getStructures() {
        return getObjMaster().getStructures();
    }

    public DequeImpl<BattleFieldObject> getBfObjects() {
        return getObjMaster().getBfObjects();
    }


    public DC_InventoryManager getInventoryManager() {
        return inventoryManager;
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

    public MissionMaster getMissionMaster() {
        return missionMaster;
    }

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public <E extends MetaGame> void setMetaMaster(MetaGameMaster<E> metaMaster) {
        this.metaMaster = metaMaster;
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
    public DC_HeroAttachedObj getSimulationObj(BattleFieldObject dc_HeroObj, ObjType type, PROPERTY prop) {
        try {
            return getSimulationCache().get(dc_HeroObj).get(type.getName() + prop.getShortName());
        } catch (Exception e) {
            return null;
        }
    }

    @Refactor
    public void addSimulationObj(BattleFieldObject dc_HeroObj, ObjType type, DC_HeroAttachedObj item,
                                 PROPERTY prop) {

        Map<String, DC_HeroAttachedObj> cache = getSimulationCache().get(dc_HeroObj);
        if (cache == null) {
            cache = new XLinkedMap<>();
            getSimulationCache().put(dc_HeroObj, cache);
        }
        cache.put(type.getName() + prop.getShortName(), item);

    }

    public ValueHelper getValueHelper() {
        return getValueManager().getValueHelper();
    }

    @Override
    public DC_ValueManager getValueManager() {
        return (DC_ValueManager) super.getValueManager();
    }

    public Map<BattleFieldObject, Map<String, DC_HeroAttachedObj>> getSimulationCache() {
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
        if (loop.getThread() != null)
            if (!loop.getThread().isAlive()) {
                LogMaster.log(1, "********* getGameLoop() --> THREAD WAS DEAD! restarting.... ");
                // EA check
                if (loop == combatLoop) {
                    combatLoop.endCombat();
                } else {
                    //TODO what now?
                    startCombat();
                }
            }
        return loop;
    }

    public void reinit() {
        reinit(false);
    }

    public void reinit(boolean restart) {
        objMaster = new DC_GameObjMaster(this);
        List<Obj> cachedObjects = new ArrayList<>();
        if (!restart)
            for (Obj sub : getState().getObjects().values()) {
                if (sub == null)
                    continue;
                if (sub instanceof Unit)
                    continue;
                if (sub.getRef() == null)
                    continue;
                if (sub.getRef().getSourceObj() == null)
                    continue;
                if (sub.getRef().getSourceObj().isMine()) {
                    cachedObjects.add(sub);
                }
            }
        this.setState(new DC_GameState(this));
        this.setManager(createGameManager());
        this.getManager().init();
        clearCaches();
        for (Obj sub : cachedObjects) {
            getState().addObject(sub);
        }
        getState().addObject(Core.getMainHero());
        visionMaster.reinit();

    }

    @Override
    public BattleFieldObject createObject(ObjType type, int x, int y, Player owner) {
        return (BattleFieldObject) super.createObject(type, x, y, owner);
    }

    @Override
    public BattleFieldObject createObject(ObjType type, Coordinates c, Player owner) {
        return (BattleFieldObject) super.createObject(type, c, owner);
    }

    protected void clearCaches() {
        combatMaster.getActionManager().clearCache();
    }

    public DC_BattleFieldGrid getGrid() {
        return grid;
    }

    public void initAndStart() {
        Launch.START(Launch.LaunchPhase._10_level_init);
        if (isInitialized())
            reinit();
        MusicMaster.preload(MusicEnums.MUSIC_SCOPE.ATMO);
        battleInit();
        metaMaster.reinit();
        Launch.END(Launch.LaunchPhase._10_level_init);

        Launch.START(Launch.LaunchPhase._11_dc_start);
        start(true);
        Launch.END(Launch.LaunchPhase._11_dc_start);
    }

    public void exit(boolean mainMenu) {
        if (combatLoop != null)
            exploreLoop.setExited(true);
        if (combatLoop != null)
            combatLoop.setExited(true);

    }

    @Override
    public boolean isWall(Coordinates c) {
        return getGrid().isWallCoordinate(c);
    }

    public boolean isBossFight() {
        return getGameMode()==GAME_MODES.BOSS_FIGHT;
    }

    public ColorMapDataSource getColorMapDS() {
        if (dungeonMaster == null) {
            return null;
        }
        return dungeonMaster.getColorMap();
    }

    public void engageEvent(Object... args) {
        getDungeonMaster().getExplorationMaster().getEngagementHandler().
                getEvents().addEvent(args);
    }

    public enum GAME_MODES {
        ARENA, BOSS_FIGHT, SIMULATION, DUNGEON_CRAWL
    }

    public enum GAME_TYPE {
         CRAWL, COMBAT, BOSS

    }

    @Override
    public boolean checkModule(Obj obj) {
        return getMetaMaster().getModuleMaster().isWithinModule(obj);
    }
}
