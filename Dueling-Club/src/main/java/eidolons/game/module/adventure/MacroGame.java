package eidolons.game.module.adventure;

import eidolons.entity.DC_IdManager;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.adventure.faction.Faction;
import eidolons.game.module.adventure.faction.FactionObj;
import eidolons.game.module.adventure.global.*;
import eidolons.game.module.adventure.rules.HungerRule;
import eidolons.game.module.adventure.town.Town;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.WEATHER;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.core.game.Game;
import eidolons.game.module.adventure.entity.MacroParty;
import eidolons.game.module.adventure.map.Place;
import eidolons.game.module.adventure.map.Region;
import eidolons.game.module.adventure.map.Route;
import eidolons.game.module.adventure.rules.TurnRule;
import eidolons.libgdx.screens.map.editor.MapPointMaster;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.SearchMaster;
import main.system.datatypes.DequeImpl;

/*
 * restored from save file? 
 * world data - each settlement, each encounter, each place, each faction hero - all persistent
 * 
 * 
 */
public class MacroGame extends Game {
    public static MacroGame game;
    DC_Game microGame;
    MacroParty playerParty;
    World world;
    MacroRef ref; // with active party/route/region/place/town
    DequeImpl<TurnRule> turnRules;
    DequeImpl<Faction> factions = new DequeImpl<>();
    private Campaign campaign;
    private GameLoop loop;
    private DAY_TIME time = DAY_TIME.MIDDAY;
    private MapPointMaster pointMaster;
    private RouteMaster routeMaster;
    private Thread gameLoopThread;
    private Faction playerFaction;

    // ...masters

    public MacroGame() {
        game = this;
        init();
    }

    public static MacroGame getGame() {
        return game;
    }

    public static void setGame(MacroGame game) {
        MacroGame.game = game;
    }

    @Override
    public MacroGameState getState() {
        return (MacroGameState) super.getState();
    }

    @Override
    public void init() {
        loop = new MacroGameLoop(this);
        ref = new MacroRef(this);
        state = new MacroGameState(this);
        manager = new MacroGameManager(this);
        master = new MacroGameMaster(this);
        logManager = new Journal(this);
        idManager = new DC_IdManager();
        turnRules = new DequeImpl<>();
        turnRules.add(new HungerRule());
        initObjTypes();
        ObjType cType = DataManager.getType(MacroManager.getCampaignName(),
         MACRO_OBJ_TYPES.CAMPAIGN);
        campaign = new Campaign(this, cType, ref);

        TimeMaster.setCampaign(campaign);
        world = WorldGenerator.generateWorld(ref);
        MacroManager.setWorldName(world.getName());
//        Region region;
//        region = world.getRegion(campaign.getProperty(MACRO_PROPS.REGION));
//        ref.setID(MACRO_KEYS.REGION.toString(), region.getId());
//        ref.setRegion(region);


        pointMaster = new MapPointMaster();
        routeMaster = new RouteMaster();

    }


    public void initObjTypes() {
        MacroManager.initTypes();
        for (OBJ_TYPE TYPE : MACRO_OBJ_TYPES.values()) {
            if (TYPE.getCode() == -1) {
                continue;
            }
            initTYPE(TYPE);
        }

    }

    @Override
    public void start(boolean host) {
//        getManager().getStateManager().resetAllSynchronized();
        loop = new MacroGameLoop(this);
        setGameLoopThread(loop.startInNewThread());

        setRunning(true);
        setStarted(true);
    }

    @Override
    public MacroGameManager getManager() {
        return (MacroGameManager) super.getManager();
    }

    public DequeImpl<FactionObj> getFactions() {
        return getState().getFactions();
    }

    public DequeImpl<Town> getTowns() {
        return getState().getTowns();
    }

    public DequeImpl<Place> getPlaces() {
        return getState().getPlaces();
    }

    public DequeImpl<Region> getRegions() {
        return getState().getRegions();
    }

    public DequeImpl<Route> getRoutes() {
        return getState().getRoutes();
    }

    public DequeImpl<MacroParty> getParties() {
        return getState().getParties();
    }

    public DC_Game getMicroGame() {
        return microGame;
    }

    public void setMicroGame(DC_Game microGame) {
        this.microGame = microGame;
    }

    @Override
    public Obj getCellByCoordinate(Coordinates coordinates) {
        // TODO Auto-generated method stub
        return null;
    }

    public MacroParty getPlayerParty() {
        return playerParty;
    }

    public void setPlayerParty(MacroParty playerParty) {
        this.playerParty = playerParty;
    }

    public World takeWorld() {
        return getGame().getWorld();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public MacroRef getRef() {
        return ref;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public DequeImpl<TurnRule> getTurnRules() {
        return turnRules;
    }

    public void setTurnRules(DequeImpl<TurnRule> turnRules) {
        this.turnRules = turnRules;
    }

    public MacroGameLoop getLoop() {
        return (MacroGameLoop) loop;
    }

    public void setLoop(GameLoop loop) {
        this.loop = loop;
    }

    public RealTimeGameLoop getRealtimeLoop() {
        return (RealTimeGameLoop) loop;
    }

    public DAY_TIME getTime() {
        return time;
    }

    public void setTime(DAY_TIME time) {
        this.time = time;
    }

    public void prepareSetTime(DAY_TIME time) {
        GuiEventManager.trigger(MapEvent.PREPARE_TIME_CHANGED, time);
        setTime(time);
    }

    public MapPointMaster getPointMaster() {
        return pointMaster;
    }

    public Thread getGameLoopThread() {
        return gameLoopThread;
    }

    public void setGameLoopThread(Thread gameLoopThread) {
        this.gameLoopThread = gameLoopThread;
    }

    public WEATHER getWeather() {
        return getLoop().getTimeMaster().getWeather();
    }

    public DIRECTION getWindDirection() {
        return getLoop().getTimeMaster().getWindDirection();
    }

    public void addFaction(Faction faction) {
        factions.add(faction);
    }

    public Faction getFaction(String string) {
        return new SearchMaster<Faction>().find(string, factions);
    }

    public Faction getPlayerFaction() {
        return playerFaction;
    }

    public void setPlayerFaction(Faction playerFaction) {
        this.playerFaction = playerFaction;
    }
}
