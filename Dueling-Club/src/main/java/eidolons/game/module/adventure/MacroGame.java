package eidolons.game.module.adventure;

import eidolons.entity.DC_IdManager;
import eidolons.game.module.adventure.entity.MacroRef;
import eidolons.game.module.adventure.entity.faction.Faction;
import eidolons.game.module.adventure.entity.faction.FactionObj;
import eidolons.game.module.adventure.entity.party.MacroParty;
import eidolons.game.module.adventure.entity.town.Town;
import eidolons.game.module.adventure.generation.WorldGenerator;
import eidolons.game.module.adventure.global.Journal;
import eidolons.game.module.adventure.global.World;
import eidolons.game.module.adventure.map.Place;
import eidolons.game.module.adventure.map.Region;
import eidolons.game.module.adventure.map.Route;
import eidolons.game.module.adventure.map.travel.RouteMaster;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.libgdx.screens.map.editor.MapPointMaster;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.WEATHER;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
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
    private RouteMaster routeMaster;
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
        world = WorldGenerator.generateWorld(ref);
        MacroManager.setWorldName(world.getName());

        pointMaster = MapPointMaster.getInstance();
        routeMaster = new RouteMaster();

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
