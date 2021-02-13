package macro;

import eidolons.entity.DC_IdManager;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import libgdx.map.editor.MapPointMaster;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.faction.Faction;
import eidolons.macro.entity.faction.FactionObj;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.entity.town.Town;
import eidolons.macro.generation.WorldGenerator;
import eidolons.macro.global.Journal;
import eidolons.macro.global.World;
import eidolons.macro.map.Place;
import eidolons.macro.map.Region;
import eidolons.macro.map.Route;
import eidolons.macro.map.travel.RouteMaster;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.WEATHER;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.core.game.Game;
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
    World world;
    MacroParty playerParty;
    MacroRef ref; // with active party/route/region/place/town
    DequeImpl<Faction> factions = new DequeImpl<>();

    private MacroGameLoop loop;
    private MapPointMaster pointMaster;
    private Faction playerFaction;


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
        logManager = new Journal(this);
        idManager = new DC_IdManager();

        initObjTypes();
        world =  generateWorld( );

        pointMaster = MapPointMaster.getInstance();
        RouteMaster routeMaster = new RouteMaster();

    }

    protected World generateWorld() {
        return WorldGenerator.generateWorld(ref);
    }


    public void initObjTypes() {
        //        SaveMaster.initTypes();
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
        loop.startInNewThread();

        setRunning(true);
        setStarted(true);
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

    public World getWorld() {
        return world;
    }
    public void setWorld(World world) {
        this.world = world;
    }

    public MacroRef getRef() {
        return ref;
    }


    public MacroGameLoop getLoop() {
        return loop;
    }

    public RealTimeGameLoop getRealtimeLoop() {
        return loop;
    }

    public DAY_TIME getTime() {
        return getLoop().getTimeMaster().getDayTime();
    }

    public MapPointMaster getPointMaster() {
        return pointMaster;
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
