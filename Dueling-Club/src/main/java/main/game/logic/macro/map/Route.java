package main.game.logic.macro.map;

import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.CoordinatesMaster;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.gui.MacroGuiManager;
import main.game.logic.macro.gui.map.obj.RouteComp;
import main.game.logic.macro.town.Town;
import main.game.logic.macro.travel.MacroParty;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.List;

public class Route extends Place {
    private Place dest;
    private Place orig;

    private DequeImpl<Place> places = new DequeImpl<>();
    private DequeImpl<Town> towns = new DequeImpl<>();
    private DequeImpl<Route> routes = new DequeImpl<>();
    private boolean coordinatesValid;

    public Route(MacroGame game, ObjType type, MacroRef ref) {
        super(game, type, ref);
    }

    public Route(MacroGame game, ObjType type, MacroRef ref, String orig,
                 String dest) {
        this(game, type, ref, ref.getRegion().getPlace(orig), ref.getRegion()
                .getPlace(dest));
    }

    public Route(MacroGame game, ObjType type, MacroRef ref, Place orig,
                 Place dest) {
        super(game, type, ref);
        this.setOrigin(orig);
        this.setDestination(dest);
        if (getArea() == null) {
            setArea(orig.getArea());
        }
    }

    public DequeImpl<Route> getLinkedRoutes() {
        return routes;
    }

    public DequeImpl<Place> getLinkedPlaces() {
        return places;
    }

    public DequeImpl<Town> getLinkedTowns() {
        return towns;
    }

    public int getSpeedMod() {
        return getIntParam(MACRO_PARAMS.SPEED_MOD);
    }

    public int getBendFactor() {
        return getIntParam(MACRO_PARAMS.BEND_FACTOR);
    }

    public int getLength() {
        return getIntParam(MACRO_PARAMS.ROUTE_LENGTH);
    }

    public Place getDestination() {
        return dest;
    }

    public void setDestination(Place dest) {
        this.dest = dest;
    }

    public Area getArea() {
        if (area == null) {
            area = orig.getArea();
            if (area == null) {
                area = dest.getArea();
            }
        }
        return area;
    }

    public Place getOrigin() {
        return orig;
    }

    public void setOrigin(Place orig) {
        this.orig = orig;
    }

    public void addLinkedPlace(Place place) {
        if (!getLinkedPlaces().contains(place)) {
            getLinkedPlaces().add(place);
        }
    }

    public void addLinkedRoute(Route place) {
        if (!getLinkedRoutes().contains(place)) {
            getLinkedRoutes().add(place);
        }
    }

    public void addLinkedTown(Town place) {
        if (!getLinkedTowns().contains(place)) {
            getLinkedTowns().add(place);
        }
    }

    @Override
    public void resetCoordinates() {
        if (coordinates != null) {
            return;
        }
        Coordinates c1 = orig.getDefaultMapRenderPoint();
        Coordinates c2 = dest.getDefaultMapRenderPoint();
        // min max enforced from MapComp that knows display-state of Places?
        int displacement = 50;
        int perpendicularOffset;

        int index = 0;
        for (Route route : orig.getRoutes()) {
            if (route.isCoordinatesValid()) {
                if ((route.getOrigin() == dest || route.getOrigin() == orig)
                        && (route.getDestination() == dest || route
                        .getDestination() == orig)) {
                    index++;
                }
            }
        }
        perpendicularOffset = index * MacroGuiManager.getRouteOffset();
        if (index % 2 == 0) {
            perpendicularOffset = -perpendicularOffset;
        }
        // TODO LINKED ROUTES
        // if (isAvailable()) { // not necessarily visible
        // if (dest == MacroManager.getActiveParty().getCurrentLocation())
        // displacement = 90;
        // else if (orig == MacroManager.getActiveParty().getCurrentLocation())
        // displacement = 10;
        // }

        // TODO MIN_MAX
        if (isActive()) {
            MacroManager.getActiveParty().getCurrentDestination();
            displacement = MacroManager.getActiveParty().getIntParam(
                    MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE);
            if (isBackwards(MacroManager.getActiveParty())) {
                displacement = 100 - displacement;
            }
        }
        this.coordinates = CoordinatesMaster.getCoordinateBetween(c1, c2,
                displacement, perpendicularOffset);
        // active route would be {ROUTE_PROGRESS} dependent...
        // the rest, well, display them in the middle?
        // available routes should be 'at hand'
        // known routes should be selectable perhaps...
        setCoordinatesValid(true);
    }

    public boolean isBackwards(MacroParty party) {
        return party.checkBool(DYNAMIC_BOOLS.BACKWARDS_ROUTE_TRAVEL);
    }

    private boolean isActive() {
        return MacroManager.getActiveParty().getCurrentRoute() == this;
    }

    public boolean isAvailable() {
        return dest == MacroManager.getActiveParty().getCurrentLocation()
                || orig == MacroManager.getActiveParty().getCurrentLocation();
    }

    public boolean isCoordinatesValid() {
        return coordinatesValid;
    }

    public void setCoordinatesValid(boolean coordinatesValid) {
        this.coordinatesValid = coordinatesValid;
    }

    public List<String> getAmbushingGroups() {
        return StringMaster
                .openContainer(getProperty(MACRO_PROPS.AMBUSHING_GROUPS));
    }

    public Place getOtherEnd(Place oneEnd) {
        // if linked??? can be traveled on either direction...
        if (oneEnd == dest) {
            return orig;
        }
        return dest;
    }

    @Override
    public int getDefaultSize() {
        return RouteComp.DEFAULT_SIZE;
    }
}
