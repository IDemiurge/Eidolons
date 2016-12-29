package main.game;

import main.ability.ActionGenerator;
import main.ability.InventoryManager;
import main.ability.UnitMaster;
import main.ability.effects.DC_EffectManager;
import main.client.battle.ArenaManager;
import main.client.battle.Battle;
import main.client.battle.BattleManager;
import main.client.battle.BattleOptions;
import main.client.battle.arcade.ArcadeManager;
import main.client.cc.logic.items.ItemGenerator;
import main.client.cc.logic.party.PartyObj;
import main.client.cc.logic.spells.SpellGenerator;
import main.client.dc.Launcher;
import main.client.game.NetGame;
import main.client.game.gui.DC_GameGUI;
import main.client.net.DC_Communicator;
import main.client.net.GameConnector;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.client.net.HostClientConnection;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.*;
import main.content.properties.PROPERTY;
import main.data.XLinkedMap;
import main.data.XList;
import main.data.xml.XML_Reader;
import main.entity.Ref;
import main.entity.obj.*;
import main.entity.obj.specific.BuffObj;
import main.entity.type.ObjType;
import main.game.ai.logic.DC_Analyzer;
import main.game.battlefield.*;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.map.DC_Map;
import main.game.battlefield.options.UIOptions;
import main.game.battlefield.pathing.PathingManager;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.DungeonMaster;
import main.game.meta.ArenaArcadeMaster;
import main.game.player.DC_Player;
import main.game.player.Player;
import main.game.player.PlayerMaster;
import main.game.turn.DC_TurnManager;
import main.rules.DC_ActionManager;
import main.rules.DC_Rules;
import main.rules.action.ActionRule;
import main.rules.mechanics.WaitRule;
import main.swing.components.obj.drawing.GuiMaster;
import main.system.DC_ConditionMaster;
import main.system.DC_RequirementsManager;
import main.system.ai.AI_Manager;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.IdManager;
import main.system.datatypes.DequeImpl;
import main.system.graphics.AnimationManager;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;
import main.system.net.DC_IdManager;
import main.system.net.data.PartyData;
import main.system.test.TestMasterContent;
import main.system.text.DC_LogManager;
import main.system.text.ToolTipMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.system.util.ValueHelper;
import main.test.PresetLauncher;
import main.test.PresetMaster;
import main.test.debug.DebugMaster;
import main.test.libgdx.GameScreen;

import javax.swing.*;
import java.util.*;

;

public class DC_Game extends MicroGame {
    public static DC_Game game;
    protected DequeImpl<DC_HeroObj> units;
    protected DC_AttackMaster attackMaster;
    protected ArmorMaster armorMaster;
    protected ArmorMaster armorSimulator;
    private Map<Coordinates, List<DC_HeroObj>> unitMap;
    private GameConnector connector;
    private HostedGame hostedGame;
    private DC_GameGUI GUI;
    private boolean AI_ON = true;
    private DC_GameManager manager;
    private VisionManager visionManager;
    private DebugMaster debugMaster;
    private TestMasterContent testMaster;
    private Thread gameLoopThread;
    private DC_Rules rules;
    private ArenaManager arenaManager;
    private BattleManager battleManager;
    private BattleOptions arenaOptions;
    private Battle battle;
    private DC_Map map;
    private GAME_MODES gameMode;
    private GAME_TYPE gameType;
    private String playerParty;
    private InventoryManager inventoryManager;
    private AI_Manager aiManager;
    private Map<DC_HeroObj, Map<String, DC_HeroAttachedObj>> simulationCache;
    private PartyObj party;
    private AnimationManager animationManager;
    private DroppedItemManager droppedItemManager;
    private DungeonMaster dungeonMaster;
    private DC_GameData data;
    private DequeImpl<ActionRule> actionRules;
    private ArcadeManager arcadeManager;
    private boolean guiBroken;
    private boolean battleInit;
    private String enemyParty;
    private boolean paused;
    private Map<Coordinates, Map<DC_HeroObj, DIRECTION>> directionMap;
    private HashMap<Coordinates, Map<DC_HeroObj, FLIP>> flipMap;
    private Map<Coordinates, List<DC_HeroObj>> unitCache = new HashMap<>();
    private boolean testMode;
    private ToolTipMaster toolTipMaster;
    private Boolean hostClient;
    private PlayerMaster playerMaster;
    private NetGame netGame;
    private boolean dummyPlus;
    private GuiMaster guiMaster;
    private ArenaArcadeMaster arenaArcadeMaster;

    public DC_Game(Player player1, Player player2, GameConnector connector, HostedGame hostedGame,
                   PartyData partyData1, PartyData partyData2) {
        this(player1, player2, hostedGame.getTitle(), partyData1.getObjData(), partyData2
                .getObjData());
        this.setHostedGame(hostedGame);
        setHost(hostedGame.isHost());
        this.setConnector(connector);
        connection = connector.getConnection();

    }

    public DC_Game(HostClientConnection connection, HostedGame hostedGame, DC_Player... players) {
        this.setConnector(connection.getGameConnector());
        this.connection = connection;
        this.setHostedGame(hostedGame);
        setHost(hostedGame.isHost());
        playerMaster = new PlayerMaster(this, players);
        // TODO players' data should already be available?
    }

    public DC_Game(NetGame netGame, DC_Player... players) {
        this.setConnector(netGame.getGameHost().getGameConnector());
        this.hostedGame = netGame.getHostedGame();
        this.netGame = netGame;
        setHost(hostedGame.isHost());
        playerMaster = new PlayerMaster(this, players);
    }

    public DC_Game(Player player1, Player player2, String gamename, String objData, String objData2) {
        super(player1, player2, gamename, objData, objData2);
    }

    public DC_Game(DC_GameData data) {
        this(data.getPlayer1(), data.getPlayer2(), data.getName(), data.getPlayerUnitData(), data
                .getPlayer2UnitData());
        this.data = data;
    }

    public DC_Game() {
        this.simulation = true;
    }

    public DC_Game(boolean simulation) {
        this.simulation = simulation;
        setGameMode((isSimulation()) ? GAME_MODES.SIMULATION : GAME_MODES.DUNGEON_CRAWL);
    }

    public static void setGame(DC_Game game2) {
        game = game2;

    }

    @Override
    public void init() {
        Chronos.mark("GAME_INIT");
        units = new DequeImpl<DC_HeroObj>() {
            public boolean add(DC_HeroObj e) {
                if (e.isHidden())
                    return false;
                return super.add(e);
            }
        };
        //TempEventManager.trigger("create-cell-object" + i + ":" + j, objects);
        Game.game = this;
        game = this;

        setState(new DC_GameState(this));
        manager = new DC_GameManager(getState(), this);
        mngr = manager;
        this.setIdManager(new DC_IdManager(getConnector(), this));
        guiMaster = new GuiMaster(this);
        armorMaster = new ArmorMaster(false);
        armorSimulator = new ArmorMaster(true);
        toolTipMaster = new ToolTipMaster(this);
        requirementsManager = new DC_RequirementsManager(this);
        valueManager = new DC_ValueManager(this);
        visionManager = new VisionManager(this);
        actionManager = new DC_ActionManager(this);
        turnManager = new DC_TurnManager(this);
        movementManager = new DC_MovementManager(this);
        attackMaster = new DC_AttackMaster(this);
        mathManager = new DC_MathManager(this);
        effectManager = new DC_EffectManager(this);
        animationManager = new AnimationManager(this);
        dialogManager = new DialogManager();
        droppedItemManager = new DroppedItemManager(this);
        dungeonMaster = new DungeonMaster(this);
        valueHelper = new ValueHelper(this);
        setTestMaster(new TestMasterContent(this));
        arenaArcadeMaster = new ArenaArcadeMaster(this);
        arcadeManager = new ArcadeManager(this);
        conditionMaster = new DC_ConditionMaster();

        logManager = new DC_LogManager(this);
        rules = new DC_Rules(this);
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
            battleFieldManager = new DC_BattleFieldManager(this, battlefield);
        }
        return (DC_BattleFieldManager) super.getBattleFieldManager();
    }

    public void battleInit() {

        inventoryManager = new InventoryManager(this);
        arenaManager = new ArenaManager(this);
        arenaManager.init();
        battleManager = new BattleManager(this);
        this.battle = arenaManager.getBattle();
        this.map = arenaManager.getMap();

        player1 = arenaManager.getPlayer();
        player2 = arenaManager.getEnemyPlayer();
        DC_Player.NEUTRAL.setGame(this);
        player1.setGame(this);
        player2.setGame(this);
        unitData1 = "";
        unitData2 = "";

        communicator = new DC_Communicator(this);
        if (netGame == null && connection == null) {
            setOffline(true);
            if (isAI_ON()) {
                player2.setAi(true);
            } else {
                setAnalyzer(new DC_Analyzer((MicroGame) Game.game));
            }

        } else {
            setOffline(false);

        }
        // if (battlefield == null) {
        battlefield = new DC_BattleField(map, player1, player2, (DC_GameState) getState());
        setGraveyardManager(new GraveyardManager(battlefield));
        battleFieldManager = new DC_BattleFieldManager(this, battlefield);
        movementManager.setGrid(battlefield.getGrid());
        // }
        if (getUiOptions() == null)
            setUiOptions(new UIOptions());

        if (AI_ON) {
            aiManager = new AI_Manager(this);
            aiManager.init();
        }
        setBattleInit(true);
    }

    public void playerJoined(HostClientConnection hcc, String data) {
        if (isHost()) {
            if (getGameMode() == GAME_MODES.DUEL) {
                connection = hcc;

                // getPlayer(false).setConnection(hcc);
            }
            // else
            // communicator.addConnection(hcc);

        } else {
            DC_Player player = playerMaster.initPlayer(hcc, data);
            if (player.isEnemy())// ??? TODO
                setEnemyParty(player.getPartyDataString());
        }
        getCommunicator().setConnectionHandler(hcc);
    }

    public DC_Communicator getCommunicator() {
        if (communicator == null) {
            communicator = new DC_Communicator(this);
            connector.setCommunicator(communicator);
            if (connection != null)
                communicator.setConnectionHandler(connection);
        }
        return (DC_Communicator) communicator;
    }

    public void start(boolean first) {
        Chronos.mark("GAME_START");

        mngr.setSbInitialized(true);
        turnManager.init();
      if (  !CoreEngine.isSwingOn()){

            WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
            WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
        }else
        if (!battlefield.isInitialized())
            // gui starts building while logic is getting ready TODO
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        battlefield.init();
                    } catch (Exception e) {
                        e.printStackTrace();
                        setGuiBroken(true);
                    }
                    battlefield.setInitialized(true);
                }
            });
        else
            // when is this really called? TODO
            battlefield.init();

        if (isDebugMode())
            debugMaster = new DebugMaster(getState(), getBattleField().getBuilder());

        arenaManager.startGame();
        game.getGraveyardManager().init();

        getState().gameStarted(first);

        // TODO: 30.10.2016 insert gui init here

        startGameLoop();

        if (hostClient != null)
            if (hostClient) {
                waitForPlayers();
            } else {
                getConnection().send(HOST_CLIENT_CODES.CHECK_READY, getPlayer(true).getName());
            }
        if (playerMaster == null)
            playerMaster = new PlayerMaster(game, getPlayer(true), getPlayer(false));
        playerMaster.initPlayerFlags();

        if (GameScreen.getInstance()!=null )
        GameScreen.getInstance().PostGameStart();
        Chronos.logTimeElapsedForMark("GAME_START");
    }

    private void waitForPlayers() {
        if (connection == null)
            if (hostClient) {
                // PlayerWaitDialog dialog = new
                // PlayerWaitDialog(getConnector().getGameHost());
                // dialog.show(); TODO
                // WaitMaster.waitForInput()

                // modal dialogue to prevent host from "playing" already...
                // 'ready' button and player statuses

                // when closed, game will be ready to transmit action-commands!
                // ModalDialog dialogue =new ModalDialog(){
                // };
                // dialogue.s
            }

    }

    public boolean isGuiBroken() {
        return guiBroken;
    }

    protected void setGuiBroken(boolean b) {
        guiBroken = b;
    }

    private void startGameLoop() {
        setRunning(true);
        if (getGameLoopThread() == null) {
            setGameLoopThread(new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true)
                        try {
                            state.newRound();
                            Thread.sleep(0);//release remains time quota
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    // while (true) {
                    // if (turnManager.makeTurn()) {
                    // manager.endTurn();
                    // } else {
                    // // error
                    // }
                    // }
                }
            }, "Game Loop"));
            getGameLoopThread().start();
        } else {
            // synchronized (gameLoopThread) {
            // gameLoopThread.notify();
            // }
            // state.init();
            // WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
            WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, false);
        }

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
        DC_HeroObj unit = ((DC_HeroObj) super.createUnit(type, x, y, owner, ref.getCopy()));
        game.getState().addObject(unit);
        unit.toBase();
        unit.resetObjects();
        unit.afterEffects();
        return unit;
    }

    public void destruct() {
        state.getObjMaps().clear();
        state.getObjects().clear();
        state.getAttachedEffects().clear();
        state.getAttachedObjects().clear();
        state.getAttachedTriggers().clear();
        state.getAttachments().clear();
        state.getAttachmentsMap().clear();
        state.getTriggers().clear();
        state.getTypeMaps().clear();
        state.getTypes().clear();
        getUnits().clear();
        getCells().clear();
    }

    public void exit(boolean mainMenu) throws InterruptedException {
        // idManager;
        stop();
        dungeonMaster.setDungeon(null);
        getManager().setSelectedInfoObj(null);
        WaitRule.reset();
        for (Obj obj : getObjects(OBJ_TYPES.BUFFS)) {
            BuffObj buff = (BuffObj) obj;
            if (buff.isDispelable() || !buff.isPermanent())
                buff.kill();
        }
        state.reset();
        logManager.clear();
        for (Obj obj : getUnits()) {
            // if (party.getMembers().contains(obj))
            // continue;

            if (!obj.getOriginalOwner().isMe()) {
                obj.kill(obj, false, true);
            }
            if (!mainMenu && obj.getOwner().isMe())
                continue;

            state.removeObject(obj.getId());
        }
        if (mainMenu)
            getUnits().clear();

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
                if (TYPE.getCode() == -1)
                    continue;
                initTYPE(TYPE);
            }
        }
    }

    @Override
    public IdManager getIdManager() {
        if (idManager != null)
            return idManager;

        return idManager = new DC_IdManager(null, this);

    }

    public HostedGame getHostedGame() {
        return hostedGame;
    }

    public void setHostedGame(HostedGame hostedGame) {
        this.hostedGame = hostedGame;
    }

    public DC_GameGUI getGUI() {
        return GUI;
    }

    public void setGUI(DC_GameGUI GUI) {
        this.GUI = GUI;
    }

    public JFrame getWindow() {
        if (getGUI() == null)
            return null;
        return getGUI().getWindow();
    }

    public boolean isAI_ON() {
        return AI_ON;
    }

    public void setAI_ON(boolean b) {
        AI_ON = b;
    }

    public DC_GameManager getManager() {
        return manager;
    }

    public void setManager(DC_GameManager manager) {
        this.manager = manager;
    }

    public void removeUnit(DC_HeroObj unit) {
        getUnits().remove(unit);
    }

    public DequeImpl<DC_HeroObj> getUnits() {
        return units;
    }

    public VisionManager getVisionManager() {
        return visionManager;
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
        if (debugMaster == null)
            if (getBattleField() != null)
                return new DebugMaster(getState(), getBattleField().getBuilder());
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
        if (ArenaArcadeMaster.isTestMode())
            return GAME_MODES.ARENA_ARCADE;
        return gameMode;
    }

    public void setGameMode(GAME_MODES gameMode) {
        this.gameMode = gameMode;
    }

    public PathingManager getPathingManager() {
        return getMovementManager().getPathingManager();
    }

    public void setPlayerParty(String playerParty) {
        if (getPlayer(true) != null)
            getPlayer(true).setPartyDataString(playerParty);
        this.playerParty = playerParty;
    }

    public synchronized String getPlayerParty() {
        if (getParty() != null)
            return getParty().getProperty(PROPS.MEMBERS);

        return playerParty;
    }

    public void setPlayerParty(PartyObj party) {
        this.setParty(party);
    }

    // public Obj getUnitByCoordinate(Coordinates c) {
    // return getObjectByCoordinate(c, false);
    // }
    public Obj getObjectVisibleByCoordinate(Coordinates c) {
        return getObjectByCoordinate(null, c, true);
    }

    public Obj getObjectVisibleByCoordinate(Integer z, Coordinates c) {
        return getObjectByCoordinate(z, c, true);
    }

    public Obj getObjectByCoordinate(Coordinates c, boolean cellsIncluded) {
        return getObjectByCoordinate(null, c, cellsIncluded, true, true);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded) {
        return getObjectByCoordinate(z, c, cellsIncluded, true, true);
    }

    public Obj getObjectByCoordinate(Integer z, Coordinates c, boolean cellsIncluded,
                                     boolean passableIncluded, boolean overlayingIncluded) {
        if (c == null)
            return null;
        List<DC_HeroObj> list = getObjectsOnCoordinate(c);
        if (list.isEmpty()) {
            if (cellsIncluded)
                return getCellByCoordinate(c);
            return null;
        }
        // ObjComponent objComp = null;
        // if (overlayingIncluded)
        // objComp = battlefield.getGrid(z).getOverlayingObjMap().get(c);
        // if (objComp == null)
        // objComp = battlefield.getGrid(z).getObjCompMap().get(c);
        // if (objComp == null)
        // if (passableIncluded)
        // objComp = battlefield.getGrid(z).getPassableObjMap().get(c);
        // if (objComp == null)
        // if (cellsIncluded)
        // objComp = battlefield.getGrid(z).getCellCompMap().get(c);
        // if (objComp != null)
        // return objComp.getObj();
        return list.get(0);
    }

    @Override
    public List<DC_HeroObj> getObjectsOnCoordinate(Coordinates c) {
        // [QUICK FIX] - consider no-reset coordinate changes for AI etc
        List<DC_HeroObj> units = getUnitCache().get(c);
        if (units != null)
            return units;
        units = getObjectsOnCoordinate(null, c, null, true, false);
        getUnitCache().put(c, units);
        return units;
    }

    public List<DC_HeroObj> getOverlayingObjects(Coordinates c) {
        return getObjectsOnCoordinate(null, c, true, true, false);

    }

    public List<DC_HeroObj> getObjectsOnCoordinate(Integer z, Coordinates c,
                                                   Boolean overlayingIncluded, boolean passableIncluded, boolean cellsIncluded) {
        // TODO auto adding cells won't work!
        if (c == null)
            return null;

        if (z == null)
            z = getDungeon().getZ();
        XList<DC_HeroObj> list = new XList<>();

        for (DC_HeroObj unit : getUnits()) {
            if (overlayingIncluded != null) {
                if (overlayingIncluded) {
                    if (!unit.isOverlaying())
                        continue;
                } else {
                    if (unit.isOverlaying())
                        continue;
                }
            }

            if (!passableIncluded) {
                if (unit.isPassable()) {
                    continue;
                }
            }
            if (unit.getZ() != z) {
                continue;
            }
            if (unit.getCoordinates().equals(c)) {
                list.add(unit);
            }
        }

        // ObjComponent objComp = null;
        // if (overlayingIncluded)
        // objComp = battlefield.getGrid(z).getOverlayingObjMap().get(c);
        // {
        // if (objComp != null)
        // list.addAllCast(objComp.getObjects());
        // }
        // objComp = battlefield.getGrid(z).getObjCompMap().get(c);
        //
        // if (objComp != null)
        // list.addAllCast(objComp.getObjects());
        // if (passableIncluded) {
        // objComp = battlefield.getGrid(z).getPassableObjMap().get(c);
        // if (objComp != null)
        // list.addAllCast(objComp.getObjects());
        // }
        // if (cellsIncluded)
        // objComp = battlefield.getGrid(z).getCellCompMap().get(c);
        // {
        // if (objComp != null)
        // list.addAllCast(objComp.getObjects());
        // }

        return list;
    }

    public void remove(Obj obj) {
        state.removeObject(obj.getId());
        obj.removed();
        if (obj instanceof DC_HeroObj)
            getUnits().remove(obj);
    }

    public InventoryManager getInventoryManager() {

        return inventoryManager;
    }

    @Override
    public DC_Cell getCellByCoordinate(Coordinates coordinates) {
        return (DC_Cell) battlefield.getCell(coordinates);
    }

    public List<DC_Cell> getCellsForCoordinates(List<Coordinates> coordinates) {
        List<DC_Cell> list = new LinkedList<>();
        for (Coordinates c : coordinates)
            list.add(getCellByCoordinate(c));
        return list;
    }

    public DC_HeroObj getUnitByCoordinate(Coordinates coordinates) {
        return (DC_HeroObj) battlefield.getObj(coordinates);
    }

    public Collection<Obj> getUnitsForCoordinates(Coordinates... coordinates) {
        return getUnitsForCoordinates(new HashSet<>(Arrays.asList(coordinates)));
    }

    public Collection<Obj> getUnitsForCoordinates(Set<Coordinates> coordinates) {
        Collection<Obj> list = new LinkedList<>();
        for (Coordinates c : coordinates)
            for (DC_HeroObj unit : getUnits())
                if (unit.getCoordinates().equals(c))
                    list.add(unit);
        return list; // TODO z-coordinate?
    }

    public Collection<? extends Obj> getCellsForCoordinates(Set<Coordinates> coordinates) {
        Collection<Obj> list = new LinkedList<>();
        for (Coordinates c : coordinates)
            for (Obj cell : getCells())
                if (cell.getCoordinates().equals(c))
                    list.add(cell);
        return list;
    }

    public Set<Obj> getCells() {
        return new HashSet<>(battlefield.getGrid().getCells());
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

    public DC_Player getPlayer(boolean me) {
        if (playerMaster != null)
            return playerMaster.getPlayer(me);
        return (DC_Player) super.getPlayer(me);
    }

    public DC_HeroAttachedObj getSimulationObj(DC_HeroObj dc_HeroObj, ObjType type, PROPERTY prop) {
        try {
            return getSimulationCache().get(dc_HeroObj).get(type.getName() + prop.getShortName());
        } catch (Exception e) {
            return null;
        }
    }

    public void addSimulationObj(DC_HeroObj dc_HeroObj, ObjType type, DC_HeroAttachedObj item,
                                 PROPERTY prop) {

        Map<String, DC_HeroAttachedObj> cache = getSimulationCache().get(dc_HeroObj);
        if (cache == null) {
            cache = new XLinkedMap<String, DC_HeroAttachedObj>();
            getSimulationCache().put(dc_HeroObj, cache);
        }
        cache.put(type.getName() + prop.getShortName(), item);

    }

    public Map<DC_HeroObj, Map<String, DC_HeroAttachedObj>> getSimulationCache() {
        if (simulationCache == null)
            simulationCache = new XLinkedMap<DC_HeroObj, Map<String, DC_HeroAttachedObj>>();
        return simulationCache;
    }

    public void setSimulationCache(Map<DC_HeroObj, Map<String, DC_HeroAttachedObj>> simulationCache) {
        this.simulationCache = simulationCache;
    }

    public BattleManager getBattleManager() {
        return battleManager;
    }

    public void setBattleManager(BattleManager battleManager) {
        this.battleManager = battleManager;
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

    public void setAttackMaster(DC_AttackMaster attackMaster) {
        this.attackMaster = attackMaster;
    }

    public DungeonMaster getDungeonMaster() {
        return dungeonMaster;
    }

    public DC_GameData getData() {
        if (data == null)
            setData(new DC_GameData(""));
        return data;
    }

    public void setData(DC_GameData data) {
        this.data = data;
    }

    public DequeImpl<ActionRule> getActionRules() {
        return actionRules;
    }

    public void setActionRules(DequeImpl<ActionRule> actionRules2) {
        this.actionRules = actionRules2;
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

    public void checkAddUnit(DC_HeroObj obj) {
        if (!obj.isHidden())
            if (!getUnits().contains(obj))
                getUnits().add((DC_HeroObj) obj);
    }

    public Dungeon getDungeon() {
        return getDungeonMaster().getDungeon();
    }

    public GAME_TYPE getGameType() {
        return gameType;
    }

    public void setGameType(GAME_TYPE game_mode) {
        this.gameType = game_mode;
    }

    public boolean isSkirmishOrScenario() {
        return getGameType() == GAME_TYPE.SKIRMISH || getGameType() == GAME_TYPE.SCENARIO;
    }

    public DC_HeroObj getMainHero() {
        return (DC_HeroObj) getPlayer(true).getHeroObj();
    }

    public Map<Coordinates, Map<DC_HeroObj, FLIP>> getFlipMap() {
        if (flipMap == null)
            flipMap = new HashMap<Coordinates, Map<DC_HeroObj, FLIP>>();
        return flipMap;
    }

    public Map<Coordinates, Map<DC_HeroObj, DIRECTION>> getDirectionMap() {
        if (directionMap == null)
            directionMap = new HashMap<>();
        return directionMap;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public Map<Coordinates, List<DC_HeroObj>> getUnitMap() {
        if (unitMap == null)
            unitMap = new HashMap<>();
        return unitMap;
    }

    public void setUnitMap(Map<Coordinates, List<DC_HeroObj>> unitMap) {
        this.unitMap = unitMap;
    }

    public String getEnemyParty() {
        return enemyParty;
    }

    public void setEnemyParty(String property) {
        this.enemyParty = property;
        if (getPlayer(false) != null)
            getPlayer(false).setPartyDataString(property);
    }

    public Map<Coordinates, List<DC_HeroObj>> getUnitCache() {
        return unitCache;
    }

    public void setUnitCache(Map<Coordinates, List<DC_HeroObj>> unitCache) {
        this.unitCache = unitCache;
    }

    public ToolTipMaster getToolTipMaster() {
        return toolTipMaster;
    }

    public ArenaArcadeMaster getArenaArcadeMaster() {
        return arenaArcadeMaster;
    }

    public Boolean getHostClient() {
        return hostClient;
    }

    public void setHostClient(Boolean hostClient) {
        this.hostClient = hostClient;
        if (hostClient != null)
            UnitMaster.setRandom(false);
    }

    public GameConnector getConnector() {
        return connector;
    }

    public void setConnector(GameConnector connector) {
        this.connector = connector;
        connector.setGame(this);
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

    public enum GAME_TYPE {
        SCENARIO, ARCADE, SKIRMISH,

    }

    public enum GAME_MODES {
        ARENA, SIMULATION, DUEL, ENCOUNTER, DUNGEON_CRAWL, ARENA_ARCADE
    }

}
