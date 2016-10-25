package main.game.logic.macro.map;

import main.content.parameters.MACRO_PARAMS;
import main.content.properties.MACRO_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroRef.MACRO_KEYS;
import main.game.logic.macro.entity.MacroObj;
import main.game.logic.macro.town.Town;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

public class Region extends MacroObj {
    private float milesToPixels;
    /*
     * calculating the basic length of routes from one place to another...
     *
     * as far as turns are concerned... 1-3 perhaps, with float points resulting
     * in randomization?
     */
    private DequeImpl<Place> places = new DequeImpl<>();
    private DequeImpl<Town> towns = new DequeImpl<>();
    private DequeImpl<Route> routes = new DequeImpl<>();
    private DequeImpl<Area> areas = new DequeImpl<>();

    // ++ AREAS?
    public Region(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    // init places

    public String getMapImagePath() {
        return getProperty(MACRO_PROPS.MAP_IMAGE);
    }

    public DequeImpl<Route> getRoutes() {
        return routes;
    }

    public DequeImpl<Place> getPlaces() {
        return places;
    }

    public DequeImpl<Place> getPlacesNoTowns() {
        DequeImpl<Place> list = new DequeImpl<>();
        for (Place p : places) {
            if (p instanceof Town)
                continue;
            list.add(p);
        }
        return list;
    }

    public void setRef(Ref ref) {
        ref.setID(MACRO_KEYS.REGION.toString(), getId());
        super.setRef(ref);
    }

    public DequeImpl<Town> getTowns() {
        return towns;
    }

    public Place getPlace(String name) {
        for (Place p : places)
            if (StringMaster.compareByChar(p.getName(), name))
                return p;
        for (Place p : towns)
            if (StringMaster.compareByChar(p.getName(), name))
                return p;
        return null;
    }

    public Town getTown(String name) {
        for (Town p : towns)
            if (StringMaster.compareByChar(p.getName(), name))
                return p;
        return null;
    }

    public Area getArea(String name) {
        for (Area a : areas)
            if (StringMaster.compareByChar(a.getName(), name))
                return a;
        return null;
    }

    public Route getRoute(String name) {
        for (Route p : routes)
            if (StringMaster.compareByChar(p.getName(), name))
                return p;
        return null;
    }

    public float getMilePerPixel() {
        if (milesToPixels == 0) {
            milesToPixels = new Float(getParam(MACRO_PARAMS.MILE_TO_PIXEL));
        }
        return milesToPixels;
    }

    public void addPlace(Place place) {
        if (!places.contains(place))
            places.add(place);
    }

    public void addTown(Town town) {
        if (places.contains(town))
            return;
        towns.add(town);
        places.add(town);
    }

    public void addRoute(Route route) {
        if (!getRoutes().contains(route))
            getRoutes().add(route);
    }

    public DequeImpl<Area> getAreas() {
        return areas;
    }

    public void addArea(Area area) {
        if (areas.contains(area))
            return;
        areas.add(area);

    }

    public Area getDefaultArea() {
        return getArea(type.getProperty(MACRO_PROPS.AREA));
    }

}
