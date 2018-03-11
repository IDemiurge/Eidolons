package main.game.module.adventure;

import main.client.cc.logic.party.Party;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.WEATHER;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.DC_IdManager;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.faction.FactionObj;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.core.GameLoop;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Region;
import main.game.module.adventure.map.Route;
import main.game.module.adventure.rules.HungerRule;
import main.game.module.adventure.rules.TurnRule;
import main.game.module.adventure.town.Town;
import main.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.libgdx.screens.map.editor.MapPointMaster;
import main.system.GuiEventManager;
import main.system.MapEvent;
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
    main.game.module.adventure.global.World world;
    MacroRef ref; // with active party/route/region/place/town
    DequeImpl<TurnRule> turnRules;
    private main.game.module.adventure.global.Campaign campaign;
    private GameLoop loop;
    private DAY_TIME time = DAY_TIME.MIDDAY;
    private MapPointMaster pointMaster;
    private RouteMaster routeMaster;
    private Thread gameLoopThread;

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
        logManager = new main.game.module.adventure.global.Journal(this);
        idManager = new DC_IdManager();
        turnRules = new DequeImpl<>();
        turnRules.add(new HungerRule());
        initObjTypes();
        ObjType cType = DataManager.getType(MacroManager.getCampaignName(),
         MACRO_OBJ_TYPES.CAMPAIGN);
        campaign = new main.game.module.adventure.global.Campaign(this, cType, ref);

        main.game.module.adventure.global.TimeMaster.setCampaign(campaign);
        world = main.game.module.adventure.global.WorldGenerator.generateWorld(ref);
        MacroManager.setWorldName(world.getName());
//        Region region;
//        region = world.getRegion(campaign.getProperty(MACRO_PROPS.REGION));
//        ref.setID(MACRO_KEYS.REGION.toString(), region.getId());
//        ref.setRegion(region);

        if (MacroManager.isEditMode()) {
//            String partyName = campaign.getProperty(MACRO_PROPS.CAMPAIGN_PARTY);
//            if (partyName.isEmpty()) {
//                partyName = Launcher.FAST_TEST_PARTY;
//            }
//            try {
//                PartyHelper.loadParty(partyName);
//            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
//            }
        } else {
            Party party = DC_Game.game.getMetaMaster().getPartyManager().getParty();
            if (party == null) {
                return;
            }

            playerParty = new MacroParty(
             getMacroPartyType(party), this, ref,
             party);
        }
//        if (!MacroManager.isLoad()) {
//            Place location = region.getPlace(campaign
//                    .getProperty(MACRO_PROPS.STARTING_LOCATION));
//            playerParty.setRegion(region);
//            playerParty.setCurrentPlace(location);
//        }


        pointMaster = new MapPointMaster();
        routeMaster = new RouteMaster();

    }


    private ObjType getMacroPartyType(Party party) {
        ObjType type = new ObjType(party.getType());
        type.initType();
        return type;
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

    public main.game.module.adventure.global.World takeWorld() {
        return getGame().getWorld();
    }

    public main.game.module.adventure.global.World getWorld() {
        return world;
    }

    public void setWorld(main.game.module.adventure.global.World world) {
        this.world = world;
    }

    public MacroRef getRef() {
        return ref;
    }

    public main.game.module.adventure.global.Campaign getCampaign() {
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
}
