package main.game.logic.macro.travel;

import main.client.cc.logic.party.PartyObj;
import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.MACRO_PROPS;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.MacroRef.MACRO_KEYS;
import main.game.logic.macro.entity.MacroObj;
import main.game.logic.macro.global.World;
import main.game.logic.macro.map.Area;
import main.game.logic.macro.map.Place;
import main.game.logic.macro.map.Region;
import main.game.logic.macro.map.Route;
import main.game.logic.macro.town.Town;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.math.MathMaster;

import javax.swing.*;
import java.util.List;

public class MacroParty extends MacroObj {
    private Route currentRoute;
    private Place currentPlace;
    private Region region;
    private Town town;
    private Place lastLocation;
    private Place currentDestination;
    private PartyObj party;
    private MACRO_STATUS status;
    private Area area;
    private Place currentExploration;

    public MacroParty(ObjType macroPartyType, MacroGame macroGame,
                      MacroRef ref, PartyObj party) {
        super(macroGame, macroPartyType, ref);
        this.party = party;
        toBase();
        initObjects();
    }

    public void initObjects() {

        World world = getRef().getWorld();
        setRegion(world.getRegion(getProperty(MACRO_PROPS.REGION)));
        setCurrentPlace(region.getPlace(getProperty(MACRO_PROPS.PLACE)));
        setCurrentRoute(region.getRoute(getProperty(MACRO_PROPS.ROUTE)));
        setCurrentDestination(region
                .getPlace(getProperty(MACRO_PROPS.DESTINATION)));
        // backwards
        setCurrentExploration(region
                .getPlace(getProperty(MACRO_PROPS.CURRENT_EXPLORATION)));
        resetMacroStatus();
        //

    }

    private void resetMacroStatus() {
        status = new EnumMaster<MACRO_STATUS>().retrieveEnumConst(
                MACRO_STATUS.class, getProperty(MACRO_PROPS.MACRO_STATUS));

    }

    @Override
    public void init() {
        add();
    }

    @Override
    public void toBase() {
        if (MacroManager.isEditMode()) {
            return;
        }
        super.toBase();
        if (party != null) {
            party.setMacroParty(this);
            party.toBase();
        }
        resetGoldShares();
        for (Unit hero : getMembers()) {
            hero.setParam(MACRO_PARAMS.TRAVEL_SPEED,
                    "" + TravelMaster.getTravelSpeedDynamic(hero)
                    // , true //TODO
            );
        }
        setParam(MACRO_PARAMS.TRAVEL_SPEED,
                getMinParam(MACRO_PARAMS.TRAVEL_SPEED), true);

        // resetParamAsSum(MACRO_PARAMS.CONSUMPTION, false);
        // resetParamAsSum(PARAMS.CARRYING_CAPACITY, false);
        // resetParamAsSum(PARAMS.C_CARRYING_WEIGHT, false);
        //
        // resetParamAsMax(MACRO_PARAMS.EXPLORE_SPEED, false);
        // resetParamAsMin(MACRO_PARAMS.TRAVEL_SPEED, false);
        //
        // calculateWeight();
        // calculateSpeed();
    }

    public void resetGoldShares() {
        int amount = 100;

        for (Unit m : getMembers()) {
            if (m == getLeader()) {
                continue;
            }
            int share = 100 / getMembers().size();
            m.setParam(MACRO_PARAMS.C_SHARED_GOLD_PERCENTAGE, share);

            share = MathMaster.applyMod(share,
                    m.getIntParam(MACRO_PARAMS.GOLD_SHARE));
            amount -= share;
            m.setParam(MACRO_PARAMS.C_GOLD_SHARE, share);
        }
        getLeader().modifyParameter(MACRO_PARAMS.C_GOLD_SHARE, amount);
    }

    @Override
    public void newTurn() {
        toBase();
        if (status == MACRO_STATUS.CAMPING) {
            RestMaster.applyMacroMode(this);
        }
    }

    public void useTime(int hours) {
        for (Unit h : getMembers()) {
            h.getMacroMode();
        }

    }

    public Integer getSharedGold() {
        Integer gold = getLeader().getIntParam(PARAMS.GOLD);
        for (Unit m : getMembers()) {
            if (m == getLeader()) {
                continue;
            }
            gold += MathMaster.applyMod(m.getIntParam(PARAMS.GOLD),
                    m.getIntParam(MACRO_PARAMS.C_SHARED_GOLD_PERCENTAGE));
        }
        return gold;
    }

    public int getMaxParam(PARAMETER p) {
        return party.getMaxParam(p, false);
    }

    public int getMinParam(PARAMETER p) {
        return party.getMinParam(p, false);
    }

    public int getParamSum(PARAMETER p) {
        return party.getParamSum(p, false);
    }

    public int getMinParam(PARAMETER p, boolean units) {
        return party.getMinParam(p, units);
    }

    public int getMaxParam(PARAMETER p, boolean units) {
        return party.getMaxParam(p, units);
    }

    public ImageIcon getIcon() {
        return party.getIcon();
    }

    public String getImagePath() {
        return party.getImagePath();
    }

    public Player getOwner() {
        return party.getOwner();
    }

    public Player getOriginalOwner() {
        return party.getOriginalOwner();
    }

    public void addMember(Unit hero) {
        party.addMember(hero);

    }

    public void removeMember(Unit hero) {
        party.removeMember(hero);
    }

    public List<Unit> getMembers() {
        return party.getMembers();
    }

    public Unit getLeader() {
        return party.getLeader();
    }

    public List<Unit> getMercs() {
        return party.getMercs();
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(Route currentRoute) {
        Integer id = null;
        String value = "";
        if (currentRoute != null) {
            id = currentRoute.getId();
            value = currentRoute.getName();
        }
        getRef().setMacroId(MACRO_KEYS.ROUTE, id);
        setProperty(MACRO_PROPS.ROUTE, value);
        this.currentRoute = currentRoute;
    }

    public Place getCurrentLocation() {
        return currentPlace;
    }

    public void setCurrentPlace(Place newPlace) {
        Integer id = null;
        String value = "";
        setLastLocation(getCurrentLocation());
        if (newPlace instanceof Town) {
            setTown((Town) newPlace);
        } else {
            setTown(null);
        }
        if (newPlace instanceof Route) {
            setCurrentRoute((Route) newPlace);
        } else {
            setCurrentRoute(null);
        }
        if (newPlace != null) {
            id = newPlace.getId();
            value = newPlace.getName();
            if (newPlace.getArea() != null) {
                if (newPlace.getArea() != getArea()) {
                    setArea(newPlace.getArea());
                }
            }
        }

        getRef().setMacroId(MACRO_KEYS.PLACE, id);
        setProperty(MACRO_PROPS.PLACE, value);
        this.currentPlace = newPlace;

    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        getRef().setMacroId(MACRO_KEYS.REGION, region.getId());
        this.region = region;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        Integer id = null;
        String value = "";
        if (town != null) {
            id = town.getId();
            value = town.getName();
        }
        getRef().setMacroId(MACRO_KEYS.TOWN, id);
        setProperty(MACRO_PROPS.PLACE, value);
        this.town = town;
    }

    public Place getLastLocation() {
        if (lastLocation == null) {
            return getCurrentLocation();
        }
        return lastLocation;
    }

    public void setLastLocation(Place lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Place getCurrentDestination() {
        return currentDestination;
    }

    public void setCurrentDestination(Place currentDestination) {
        this.currentDestination = currentDestination;
        if (currentDestination == null) {
            removeProperty(MACRO_PROPS.DESTINATION);
        } else {
            setProperty(MACRO_PROPS.DESTINATION, currentDestination.getName());
        }
    }

    public Area getArea() {
        if (area == null) {
            area = getRegion().getArea(getProperty(MACRO_PROPS.AREA));
        }
        if (area == null && getCurrentLocation() != null) {
            area = getCurrentLocation().getArea();
        }
        if (area == null && getCurrentRoute() != null) {
            area = getCurrentRoute().getArea();
        }
        return area;
    }

    public void setArea(Area area) {
        getRef().setMacroId(MACRO_KEYS.AREA, area.getId());
        this.area = area;
        setProperty(MACRO_PROPS.AREA, area.getName());
    }

    public MACRO_STATUS getStatus() {
        return status;
    }

    public void setStatus(MACRO_STATUS status) {
        this.status = status;
        if (status == null) {
            removeProperty(MACRO_PROPS.MACRO_STATUS);
        } else {
            setProperty(MACRO_PROPS.MACRO_STATUS, status.toString());
        }
    }

    public int getExploreCapacity() {
        int capacity = party.getMaxParam(PARAMS.DETECTION);
        // ++ speed;
        for (Unit m : getMembers()) {
            capacity += m.getIntParam(PARAMS.DETECTION) / 2;
        }
        return capacity;
    }

    public int getSurvivalCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    public PartyObj getMicroParty() {
        return party;
    }

    public Place getCurrentExploration() {
        return currentExploration;
    }

    public void setCurrentExploration(Place place) {
        if (place != null) {
            setProperty(MACRO_PROPS.CURRENT_EXPLORATION, place.getName());
        } else {
            removeProperty(MACRO_PROPS.CURRENT_EXPLORATION);
        }
        this.currentExploration = place;
    }

}
