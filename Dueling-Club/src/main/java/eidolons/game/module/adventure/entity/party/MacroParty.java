package eidolons.game.module.adventure.entity.party;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.MacroManager;
import eidolons.game.module.adventure.entity.MacroRef;
import eidolons.game.module.adventure.entity.MacroRef.MACRO_KEYS;
import eidolons.game.module.adventure.entity.MapObj;
import eidolons.game.module.adventure.entity.faction.Faction;
import eidolons.game.module.adventure.map.area.Area;
import eidolons.game.module.adventure.map.Place;
import eidolons.game.module.adventure.map.Route;
import eidolons.game.module.adventure.entity.town.Town;
import eidolons.game.module.adventure.map.travel.old.RestMasterOld;
import eidolons.game.module.adventure.map.travel.old.TravelMasterOld;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.MACRO_PROPS;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import javax.swing.*;
import java.util.List;

public class MacroParty extends MapObj {
    private Route currentRoute;
    private Place currentPlace;
    private Town town;
    private Place lastLocation;
    private Place currentDestination;
    private Party party;
    private MACRO_STATUS status;
    private Area area;
    private Place currentExploration;
    private float routeProgress;

    public MacroParty(ObjType macroPartyType, MacroGame macroGame,
                      MacroRef ref) {
        super(macroGame, macroPartyType, ref);
        DC_Player player = (DC_Player) ref.getPlayer();
        if (player != null)
            setFaction(player.getFaction());
    }

    public MacroParty(ObjType macroPartyType, MacroGame macroGame,
                      MacroRef ref, Party party) {
        super(macroGame, macroPartyType, ref);
        this.party = party;
        toBase();
        initObjects();
    }

    @Override
    public String getNameAndCoordinate() {

        return getName() + StringMaster.wrapInParenthesis(getX() + "," + getY()
         + "," + ((isMine() ? "player" : getFaction().getName()
        )));
    }

    @Override
    public Faction getFaction() {
        return super.getFaction();
    }

    public void initObjects() {
//        World world = getRef().getWorld();
//        setRegion(world.getRegion(getProperty(MACRO_PROPS.REGION)));
//        setCurrentPlace(region.getPlace(getProperty(MACRO_PROPS.PLACE)));
//        setCurrentRoute(region.getRoute(getProperty(MACRO_PROPS.ROUTE)));
//        setCurrentDestination(region
//                .getPlace(getProperty(MACRO_PROPS.DESTINATION)));
        // backwards
//        setCurrentExploration(region
//                .getPlace(getProperty(MACRO_PROPS.CURRENT_EXPLORATION)));
        resetMacroStatus();
        //

    }

    private void resetMacroStatus() {
        status = new EnumMaster<MACRO_STATUS>().retrieveEnumConst(
         MACRO_STATUS.class, getProperty(MACRO_PROPS.MACRO_STATUS));

    }

    @Override
    public void init() {
        addToState();
    }

    @Override
    public void toBase() {
        if (MacroManager.isEditMode()) {
            return;
        }
        super.toBase();
        if (getParty() != null) {
            getParty().setMacroParty(this);
            getParty().toBase();
        }
        resetGoldShares();
        for (Unit hero : getMembers()) {
            hero.setParam(MACRO_PARAMS.TRAVEL_SPEED,
             "" + TravelMasterOld.getTravelSpeedDynamic(hero)
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
            RestMasterOld.applyMacroMode(this);
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
        return getParty().getMaxParam(p, false);
    }

    public int getMinParam(PARAMETER p) {
        return getParty().getMinParam(p, false);
    }

    public int getParamSum(PARAMETER p) {
        return getParty().getParamSum(p, false);
    }

    public int getMinParam(PARAMETER p, boolean units) {
        return getParty().getMinParam(p, units);
    }

    public int getMaxParam(PARAMETER p, boolean units) {
        return getParty().getMaxParam(p, units);
    }

    public ImageIcon getIcon() {
        return getParty().getIcon();
    }

    public String getImagePath() {
        return getParty().getImagePath();
    }

    public Player getOwner() {
        return getParty().getOwner();
    }

    @Override
    public void setOwner(Player owner) {
        getParty().setOwner(owner);
    }

    public Player getOriginalOwner() {
        return getParty().getOriginalOwner();
    }

    @Override
    public void setOriginalOwner(Player originalOwner) {
        getParty().setOriginalOwner(originalOwner);
    }

    public void addMember(Unit hero) {
        getParty().addMember(hero);

    }

    public void removeMember(Unit hero) {
        getParty().removeMember(hero);
    }

    public List<Unit> getMembers() {
        return getParty().getMembers();
    }

    public Unit getLeader() {

        return getParty().getLeader();
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
        int capacity = getParty().getMaxParam(PARAMS.DETECTION);
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

    public int getTravelSpeed() {
        return 300;
//        return getMinParam(MACRO_PARAMS.TRAVEL_SPEED); //meters per minute
    }

    public Party getMicroParty() {
        return getParty();
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

    public float getRouteProgress() {
        return routeProgress;
    }

    public void setRouteProgress(float routeProgress) {
        this.routeProgress = routeProgress;
    }

    public MACRO_STATUS getStatus(Unit hero) {
        MACRO_STATUS heroStatus = new EnumMaster<MACRO_STATUS>().retrieveEnumConst(MACRO_STATUS.class,
         hero.getProperty(MACRO_PROPS.MACRO_STATUS));
        if (heroStatus != null)
            return heroStatus;
        return MACRO_STATUS.IDLE;
    }

    public String getMemberRank(Unit hero) {
        if (hero.equals(getLeader())) {
            return "Leader";
        }
        return "Companion";
//        return "Mercenary";
    }

    @Override
    public int getDefaultSize() {
        return 96;
    }

    public Party getParty() {
        if (party == null) {
            party = new Party(getType());
        }
        return party;
    }
}
