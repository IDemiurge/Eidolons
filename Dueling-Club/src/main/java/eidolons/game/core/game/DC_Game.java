package eidolons.game.core.game;

import eidolons.ability.ActionGenerator;
import eidolons.ability.InventoryTransactionManager;
import eidolons.ability.effects.DC_EffectManager;
import eidolons.content.DC_ValueManager;
import eidolons.content.ValueHelper;
import eidolons.entity.DC_IdManager;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.item.DC_InventoryManager;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.logic.battle.test.TestBattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.DC_BattleFieldManager;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.battlefield.DroppedItemManager;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationMaster;
import eidolons.game.battlecraft.logic.dungeon.test.TestDungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.rules.DC_Rules;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.battlecraft.rules.combat.damage.ArmorMaster;
import eidolons.game.core.*;
import eidolons.game.core.atb.AtbController;
import eidolons.game.core.atb.AtbTurnManager;
import eidolons.game.core.launch.LaunchDataKeeper;
import eidolons.game.core.master.combat.CombatMaster;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.explore.ExploreGameLoop;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.macro.entity.town.Town;
import eidolons.system.DC_ConditionMaster;
import eidolons.system.DC_RequirementsManager;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import eidolons.system.hotkey.DC_KeyManager;
import eidolons.system.math.DC_MathManager;
import eidolons.system.test.TestMasterContent;
import eidolons.system.text.DC_LogManager;
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
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.entity.IdManager;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.util.Refactor;

import java.util.*;

import static main.system.launch.CoreEngine.isCombatGame;

/**
 * contains references to everything that may be needed in scope of a single game
 * TODO refactor - put data into GameState!
 * init() should be called to create all Masters
 * battleInit() is a reset method
 * start() creates units and starts GameLoop (DC_TurnManager)
 */
public class DC_Game extends GenericGame {
    public static DC_Game game;

    protected MetaGameMaster metaMaster;
    protected DungeonMaster dungeonMaster;
    protected BattleMaster battleMaster;
    protected CombatMaster combatMaster;

    protected DroppedItemManager droppedItemManager;
    protected InventoryTransactionManager inventoryTransactionManager;
    protected DC_InventoryManager inventoryManager;
    protected DC_GameManager manager;

    protected VisionMaster visionMaster;
    protected TestMasterContent testMaster;
    protected AI_Manager aiManager;
    protected DC_KeyManager keyManager; //where to move?

    protected DC_Rules rules;

    protected GAME_MODES gameMode;
    protected GAME_TYPE gameType;
    protected boolean battleInit; //to Battle!


    protected Map<Coordinates, Map<BattleFieldObject, DIRECTION>> directionMap; // ?!
    protected HashMap<Coordinates, Map<BattleFieldObject, FLIP>> flipMap;

    protected boolean testMode;
    protected boolean dummyPlus;
    protected boolean AI_ON = true;

    protected GameLoop loop;
    protected CombatLoop combatLoop;
    protected ExploreGameLoop exploreLoop;
    protected LaunchDataKeeper dataKeeper;
    @Refactor
    protected Map<BattleFieldObject, Map<String, DC_HeroAttachedObj>> simulationCache; //to simGame!
    protected MusicMaster musicMaster;
    protected DC_BattleFieldGrid grid;

    @Refactor
    public Town town; //TODO
    private boolean bossFight;

    protected DC_GameObjMaster paleMaster;

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
        master = new DC_GameObjMaster(this);
        paleMaster = new DC_GameObjMaster(this, true);
        manager = createGameManager();
        manager.init();

        combatMaster = createCombatMaster();

        requirementsManager = new DC_RequirementsManager(this);
        valueManager = new DC_ValueManager(this);
        if (!isSimulation())
            visionMaster = new VisionMaster(this);
        mathManager = new DC_MathManager(this);
        effectManager = new DC_EffectManager(this);
        droppedItemManager = new DroppedItemManager(this);
        setTestMaster(new TestMasterContent(this));
        conditionMaster = new DC_ConditionMaster();
        logManager = new DC_LogManager(this);

        if (isCombatGame())
            rules = new DC_Rules(this);

        if (!isCombatGame() && !CoreEngine.isDungeonTool() && !CoreEngine.isLevelEditor())
            return;
        if (isSimulation()) {
            return;
        }
        ExplorationMaster master = null;
        if (nextLevel) {
            master = dungeonMaster.getExplorationMaster();
        }
        dungeonMaster = createDungeonMaster();

        //TODO igg demo hack
        if (nextLevel)
            dungeonMaster.setExplorationMaster(master);
        if (!isCombatGame())
            return;
        battleMaster = createBattleMaster();
        musicMaster = MusicMaster.getInstance();
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
        keyManager =  createKeyManager();
        Chronos.logTimeElapsedForMark("GAME_INIT");
    }

    protected DC_KeyManager createKeyManager() {
        return new DC_KeyManager(getManager());
    }

    protected BattleMaster createBattleMaster() {
        return new TestBattleMaster(this);
    }

    protected boolean isLocation() {
        return CoreEngine.isMainGame();
    }

    protected DungeonMaster createDungeonMaster() {
        if (isLocation())
            return new LocationMaster(this);
        return new TestDungeonMaster(this);
    }

    // after meta
    public void dungeonInit() {
    }

    public void battleInit() {
        setSimulation(false);
        if (!isCombatGame()) {
            dungeonMaster.init();
            return;
        }
        ActionGenerator.init();

        getRules().getIlluminationRule().clearCache();
        inventoryTransactionManager = new InventoryTransactionManager(this);
        inventoryManager = new DC_InventoryManager();
        battleMaster.init();
        dungeonMaster.init();
        setOffline(true);

        grid = new DC_BattleFieldGrid(getDungeon());
        battleFieldManager = new DC_BattleFieldManager(this);

        droppedItemManager.init();

        if (AI_ON) {
            aiManager = new AI_Manager(this);
            aiManager.init();
            dungeonMaster.getExplorationMaster().getAiMaster().getExploreAiManager().initialize();
        }
        setBattleInit(true);
    }

    public void start(boolean first) {
        Chronos.mark("GAME_START");
        this.manager.setSbInitialized(true); //TODO legacy?
        getTurnManager().init();

        keyManager.init();
        getGraveyardManager().init();//TODO in init?
        battleMaster.startGame();

        if (getMetaMaster() != null)
            getMetaMaster().gameStarted();
        dungeonMaster.gameStarted();

        if (dungeonMaster.getExplorationMaster() != null) {
            dungeonMaster.getExplorationMaster().init();

        }
        visionMaster.refresh();
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
        getVisionMaster().refresh();
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
            musicMaster.scopeChanged(MUSIC_SCOPE.ATMO);
        getStateManager().newRound();


    }

    private void startCombat() {
        loop = combatLoop;
        exploreLoop.stop();
        EUtils.showInfoText("The Battle is Joined!");

        if (!combatLoop.isStarted() || !combatLoop.checkThreadIsRunning()
//                CoreEngine.isIggDemoRunning()
        )
            loop.startInNewThread();
        else
            combatLoop.resume();

        musicMaster.scopeChanged(MUSIC_SCOPE.BATTLE);
        DC_SoundMaster.playStandardSound(
                RandomWizard.random() ? STD_SOUNDS.NEW__BATTLE_START2
                        : STD_SOUNDS.NEW__BATTLE_START);

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

    @Override
    public void initObjTypes() {
        super.initObjTypes();
        if (CoreEngine.isMacro()) {
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
                    && !CoreEngine.isItemGenerationOff()
            ) {
                itemGenerator = new ItemGenerator(CoreEngine.isFastMode());
                itemGenerator.init();
            }

    }

    public Obj getObjectVisibleByCoordinate(Coordinates c) {
        return getMaster().getObjectVisibleByCoordinate(c);
    }

    public DC_GameObjMaster getMaster() {
        if (PaleAspect.ON)
            return getPaleMaster();
        return (DC_GameObjMaster) master;
    }

    public DC_GameObjMaster getPaleMaster() {
        return paleMaster;
    }

    public Obj getObjectByCoordinate(Coordinates c, boolean cellsIncluded) {
        return getMaster().getObjectByCoordinate(c, cellsIncluded);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded, boolean passableIncluded, boolean overlayingIncluded) {
        return getMaster().getObjectByCoordinate(z, c, cellsIncluded, passableIncluded, overlayingIncluded);
    }

    public Set<BattleFieldObject> getOverlayingObjects(Coordinates c) {
        return getMaster().getOverlayingObjects(c);
    }

    public Set<BattleFieldObject> getObjectsOnCoordinate(Integer z, Coordinates c, Boolean overlayingIncluded, boolean passableIncluded, boolean cellsIncluded) {
        return getMaster().getObjectsOnCoordinate(z, c, overlayingIncluded, passableIncluded, cellsIncluded);
    }

    public Set<DC_Cell> getCellsForCoordinates(Set<Coordinates> coordinates) {
        return getMaster().getCellsForCoordinates(coordinates);
    }

    public Unit getUnitByCoordinate(Coordinates coordinates) {
        return getMaster().getUnitByCoordinate(coordinates);
    }

    public Collection<Unit> getUnitsForCoordinates(Coordinates... coordinates) {
        return getMaster().getUnitsOnCoordinates(coordinates);
    }

    @Override
    public void remove(Obj obj) {
        super.remove(obj);
        if (obj instanceof Unit) {
            removeUnit((Unit) obj);
        }
    }


    public void softRemove(BattleFieldObject obj) {
        //leave obj in gamestate for refs
        if (obj instanceof Unit) {
            removeUnit((Unit) obj);
        } else {
            getMaster().removeStructure((Structure) obj);
        }
    }

    public void removeUnit(Unit unit) {
        getMaster().removeUnit(unit);
    }

    public Set<Obj> getCells() {
        return getMaster().getCells();
    }

    public Set<Unit> getUnits() {
        return getMaster().getUnits();
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
    public DC_Cell getCellByCoordinate(Coordinates coordinates) {
        return getMaster().getCellByCoordinate(coordinates);
    }

    public DC_Player getPlayer(boolean me) {
        return getBattleMaster().getPlayerManager().getPlayer(me);
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


    public Dungeon getDungeon() {
        try {
            return getDungeonMaster().getDungeonWrapper().getDungeon();
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

    public Set<Structure> getStructures() {
        return getMaster().getStructures();
    }

    public DequeImpl<BattleFieldObject> getBfObjects() {
        return getMaster().getBfObjects();
    }

    public Set<BattleFieldObject> getObjectsAt(Coordinates c) {
        return getMaster().getObjectsOnCoordinate(getDungeon().getZ(), c, false, true, false);
    }

    public DC_InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public Set<BattleFieldObject> getObjectsOnCoordinate(Coordinates c) {
        //        return getMaster().getObjectsOnCoordinate(c);
        return getObjectsOnCoordinate(null, c, false, true, false);
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
                LogMaster.log(1, "********* getGameLoop() --> THREAD WAS DEAD! restarting.... "); // igg demo hack
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
        master = new DC_GameObjMaster(this);
        paleMaster = new DC_GameObjMaster(this, true);
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
        getState().addObject(Eidolons.getMainHero());
        if (dungeonMaster.getExplorationMaster() != null) {
            dungeonMaster.getExplorationMaster().
                    getResetter().setResetNotRequired(false);
        }
        visionMaster.reinit();

    }

    protected void clearCaches() {
        combatMaster.getActionManager().clearCache();
    }

    public DC_BattleFieldGrid getGrid() {
        return grid;
    }

    public void initAndStart() {
        if (isInitialized())
            reinit();
        dungeonInit();
        battleInit();
        metaMaster.reinit();
        start(true);
    }

    public void exit(boolean mainMenu) {
        if (combatLoop != null)
            exploreLoop.setExited(true);
        if (combatLoop != null)
            combatLoop.setExited(true);

    }

    public boolean isBossFight() {
        return EidolonsGame.BOSS_FIGHT;
    }

    public void setBossFight(boolean bossFight) {
        this.bossFight = bossFight;
    }

    public boolean toggleVoid(Coordinates c) {
        DC_Cell cell = getCellByCoordinate(c);
        boolean v;
        cell.setVOID(v = !cell.isVOID());
        //what about things on this cell?
        return v;
    }

    public enum GAME_MODES {
        ARENA, SIMULATION, DUEL, ENCOUNTER, DUNGEON_CRAWL, ARENA_ARCADE
    }

    public enum GAME_TYPE {
        SCENARIO, ARCADE, SKIRMISH, TEST,

    }


}
