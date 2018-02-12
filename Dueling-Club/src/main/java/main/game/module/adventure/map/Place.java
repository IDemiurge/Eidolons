package main.game.module.adventure.map;

import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.entity.MapObj;
import main.game.module.adventure.gui.map.obj.PlaceComp;
import main.game.module.adventure.map.area.AreaManager;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.travel.TravelMasterOld;
import main.system.datatypes.DequeImpl;

public class Place extends MapObj {
    protected DequeImpl<Route> routes = new DequeImpl<>();
    private Dungeon topDungeon;

    public Place(MacroGame game, ObjType type, MacroRef ref) {
        super(game, type, ref);

        ObjType objType = DataManager.getType(getProperty(MACRO_PROPS.AREA), MACRO_OBJ_TYPES.AREA);
        if (objType != null) {
            setArea(new Area(game, objType, ref));
        } else {
            // objType =
            // ref.getMacroObj(MACRO_KEYS.AREA);

        }
    }

    public Area getArea() {
        if (area == null) {
            area = AreaManager.getAreaForCoordinate(getCoordinates());
        }
        if (area == null) {
            area = getRegion().getDefaultArea();
        }
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public void toBase() {
        super.toBase();
        resetVisibilityStatus();
    }

    private void resetVisibilityStatus() {
        // TravelMaster

        // from property?

        // some actions might reveal/hide places...
        // but at least there is the normal algorithm to determined
        // visibility...
        // buying maps will unlock certain places or even all places in a region

        // perhaps places should have a 'concealment level', those with low one
        // will be on maps and easy to discover...

    }

    public boolean isVisible() {
        // status DISCOVERED
        if (visibilityStatus == PLACE_VISIBILITY_STATUS.HIDDEN) {
            return false;
        }
        if (visibilityStatus == PLACE_VISIBILITY_STATUS.UNKNOWN) {
            return false;
        }
        return true;
    }

    public DequeImpl<Route> getRoutes() {
        return routes;
    }

    public PLACE_VISIBILITY_STATUS getVisibilityStatus() {
        return visibilityStatus;
    }

    public void setVisibilityStatus(PLACE_VISIBILITY_STATUS visibilityStatus) {
        this.visibilityStatus = visibilityStatus;
        setProperty(MACRO_PROPS.PLACE_VISIBILITY_STATUS, visibilityStatus.toString());
    }

    public void setComp(PlaceComp comp) {
        this.comp = comp;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getDefaultSize() {
        return PlaceComp.DEFAULT_SIZE;
    }

    public Dungeon getTopDungeon() {
        return topDungeon;
    }

    public void setTopDungeon(Dungeon topDungeon) {
        this.topDungeon = topDungeon;
    }

    public void resetCoordinates() {
        int x = getIntParam(MACRO_PARAMS.MAP_POS_X);
        int y = getIntParam(MACRO_PARAMS.MAP_POS_Y);
        setCoordinates(new Coordinates(true, x, y));

    }

    public void addRoute(Route route) {
        if (!getRoutes().contains(route)) {
            getRoutes().add(route);
        }
    }

    public boolean isAvailable() {
        MacroParty party = getGame().getPlayerParty();
        if (party.getCurrentLocation() == this) {
            return true;
        }
        return TravelMasterOld.getAvailablePlaces(party).contains(this);
    }

    public boolean isLinkedToRoute(Route route) {
        return route.getLinkedPlaces().contains(this);
    }

    public String getIconPath() {
        return getProperty(MACRO_PROPS.MAP_ICON);
    }

    public enum PLACE_VISIBILITY_STATUS {
        UNKNOWN, DISCOVERED, HIDDEN, AVAILABLE, CURRENT_LOCATION;
    }

}
