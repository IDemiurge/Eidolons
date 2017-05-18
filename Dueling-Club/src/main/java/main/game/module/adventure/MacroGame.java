package main.game.module.adventure;

import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.DC_IdManager;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.battlecraft.logic.meta.faction.FactionObj;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.battlecraft.logic.meta.faction.FactionObj;
import main.game.module.adventure.MacroRef.MACRO_KEYS;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Region;
import main.game.module.adventure.map.Route;
import main.game.module.adventure.rules.TurnRule;
import main.game.module.adventure.town.Town;
import main.game.module.adventure.travel.HungerRule;
import main.game.module.adventure.travel.MacroParty;
import main.system.datatypes.DequeImpl;

/*
 * restored from save file? 
 * world data - each settlement, each encounter, each place, each faction hero - all persistent
 * 
 * 
 */
public class MacroGame extends Game {
    static MacroGame game;
    DC_Game microGame;
    MacroParty playerParty;
    main.game.module.adventure.global.World world;
    MacroRef ref; // with active party/route/region/place/town
    DequeImpl<TurnRule> turnRules;
    private main.game.module.adventure.global.Campaign campaign;

    // ...masters

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
        ref = new MacroRef(this);
        state = new MacroGameState(this);
        manager = new MacroGameManager(this);
        logManager = new main.game.module.adventure.global.Journal(this);
        idManager = new DC_IdManager();
        turnRules = new DequeImpl<>();
        turnRules.add(new HungerRule());
        initObjTypes();
        ObjType cType = DataManager.getType(MacroManager.getCampaignName(),
                MACRO_OBJ_TYPES.CAMPAIGN);
        campaign = new main.game.module.adventure.global.Campaign(this, cType, ref);
        MacroManager.setCampaignName(campaign.getName());
        main.game.module.adventure.global.TimeMaster.setCampaign(campaign);
        world = main.game.module.adventure.global.WorldGenerator.generateWorld(ref);
        ref.setMacroId(MACRO_KEYS.WORLD, world.getId());
        MacroManager.setWorldName(world.getName());
        Region region;
        region = world.getRegion(campaign.getProperty(MACRO_PROPS.REGION));
        ref.setID(MACRO_KEYS.REGION.toString(), region.getId());
        ref.setRegion(region);

        if (MacroManager.isEditMode()) {
            String partyName = campaign.getProperty(MACRO_PROPS.CAMPAIGN_PARTY);
            if (partyName.isEmpty()) {
                partyName = Launcher.FAST_TEST_PARTY;
            }
            try {
                PartyHelper.loadParty(partyName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (PartyHelper.getParty() == null) {
            return;
        }

        playerParty = new MacroParty(
                getMacroPartyType(PartyHelper.getParty()), this, ref,
                PartyHelper.getParty());
        if (!MacroManager.isLoad()) {
            Place location = region.getPlace(campaign
                    .getProperty(MACRO_PROPS.STARTING_LOCATION));
            playerParty.setRegion(region);
            playerParty.setCurrentPlace(location);
        }

    }

    private ObjType getMacroPartyType(PartyObj party) {
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
        getManager().getStateManager().resetAllSynchronized();

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

}
