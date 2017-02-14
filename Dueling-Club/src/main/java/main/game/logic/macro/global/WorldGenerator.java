package main.game.logic.macro.global;

import main.content.MACRO_OBJ_TYPES;
import main.content.parameters.MACRO_PARAMS;
import main.content.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.logic.faction.FactionMaster;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.map.Area;
import main.game.logic.macro.map.Place;
import main.game.logic.macro.map.Region;
import main.game.logic.macro.map.Route;
import main.game.logic.macro.town.Town;
import main.game.logic.macro.travel.AreaManager;
import main.game.logic.macro.travel.TravelMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class WorldGenerator {
    static Region region;
    private static MacroGame game;
    private static World world;

    public static World generateWorld(MacroRef ref) {
        ObjType wType = DataManager.getType(MacroManager.getWorldName(),
                MACRO_OBJ_TYPES.WORLD);
        game = ref.getGame();
        world = new World(ref.getGame(), wType, ref);
        ref.getGame().setWorld(world);
        world.setRegions(generateRegions(ref));
        FactionMaster.generateFactions(ref);
        // parties
        return world;

    }

    public static List<Region> generateRegions(MacroRef ref) {
        List<Region> regions = new LinkedList<>();
        for (String s : StringMaster.openContainer(world
                .getProperty(MACRO_PROPS.REGIONS))) {
            ObjType type = DataManager.getType(s, MACRO_OBJ_TYPES.REGION);
            region = createRegion(type, ref);
            regions.add(region);
            AreaManager.assignPlacesToAreas(region);
            if (!MacroManager.isEditMode()) {
                AreaManager.initRegionAreas(region);
            }

        }
        return regions;
    }

    public static Region createRegion(ObjType type, MacroRef ref) {
        // template
        // add places, towns and routes

        region = new Region(game, type, ref);
        // init default towns/places ; then add randomized
        for (String s : StringMaster.openContainer(region
                .getProperty(MACRO_PROPS.AREAS))) {
            type = DataManager.getType(s, MACRO_OBJ_TYPES.AREA);
            Area area = new Area(ref.getGame(), type, ref);
            region.getAreas().add(area);
        }
        for (String s : StringMaster.openContainer(region
                .getProperty(MACRO_PROPS.PLACES))) {
            Place place = createPlace(ref, s);
            region.addPlace(place);
        }
        for (String s : StringMaster.openContainer(region
                .getProperty(MACRO_PROPS.TOWNS))) {
            Town town = createTown(ref, s);
            region.addTown(town);
        }
        generateRoutes();
        return region;
        /*
		 * r.getProperty(MACRO_PROPS.PLACES) Places - Power level ++ will be
		 * appearing as the game progresses
		 * 
		 * Routes - there should be at least 1 between each 2 places as
		 * "default" (preset) and then we can generate additional randomized
		 * routes For non-preset Places, we can use some metrics...
		 */
    }

    private static void generateRoutes() {
        // TODO external routes
        String property = region.getProperty(MACRO_PROPS.INTERNAL_ROUTES);
        for (String routeTypeName : StringMaster.openContainer(property)) {
            ObjType routeType = DataManager.getType(routeTypeName,
                    MACRO_OBJ_TYPES.ROUTE);
            Route r;
            try {
                r = createRoute(routeType);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            region.addRoute(r);
        }
		/*
		 * discovering stuff... route exploration will add to Route Progress
		 * and when reaching those peripherals, there'll be a chance... and
		 * perhaps it should be offered to go off towards them, and then we
		 * can calc if the place can be reached or not...
		 */
        //
        // routeTypeName = VariableManager.removeVarPart(routeTypeName);
        // // TODO OR getCustomProp(routeTypeName) !
        // ObjType routeType = DataManager.getType(routeTypeName,
        // MACRO_OBJ_TYPES.ROUTE);
        // if (routeType == null)
        // continue;
        // routeType = new ObjType(routeType);
        // routeType.initType();
        // List<String> vars = StringMaster.openContainer(
        // VariableManager.getVarPart(routeTypeName),
        // StringMaster.VAR_SEPARATOR);
        // if (vars.size() == 0) {
        // Route r = createRoute(routeType);
        // region.addRoute(r);
        // continue;
        // }
        //
        // String origin = vars.getOrCreate(0);
        // String destination = vars.getOrCreate(1);
        // vars.remove(0);
        // vars.remove(1); // perhaps same format better?
        // for (String v : vars) { // setRouteValues(r, vars);
        // String valName = v.split(":")[0];
        // String value = v.split(":")[1];
        // routeType.setValue(valName, value);
        // }
        // Route r = createRoute(origin, destination, routeType);
        // region.addRoute(r);
        // // boolean random_blocked = false; if (random_blocked)
        // // continue;
        // // if (destination.contains("@")) {
        // // random_blocked = true;
        // // property = property.replace("@", "");
        // // }
        // // int random_n = 0; while (random_n > 0) {
        // // random_n--;
        // // TODO random route!
        // // if (t == null)
        // // if (StringMaster.isNumber(route, true)) {
        // // random_n = StringMaster.getInteger(route);
        // // continue;
        // // } else {
        // // // TODO random from template
        // // }
        // }
        for (Route r : region.getRoutes()) {
            for (String p : StringMaster.openContainer(r
                    .getProperty(MACRO_PROPS.LINKED_PLACES))) {
                r.addLinkedPlace(region.getPlace(p));
            }
            for (String p : StringMaster.openContainer(r
                    .getProperty(MACRO_PROPS.LINKED_TOWNS))) {
                r.addLinkedTown(region.getTown(p));
            }
            for (String p : StringMaster.openContainer(r
                    .getProperty(MACRO_PROPS.LINKED_ROUTES))) {
                r.addLinkedRoute(region.getRoute(p));
            }
        }
    }

    private static Route createRoute(ObjType t) {
        Route r = new Route(game, t, region.getRef());
        Place orig = region.getPlace(r.getProperty(MACRO_PROPS.ORIGIN));
        orig.addRoute(r);
        Place dest = region.getPlace(r.getProperty(MACRO_PROPS.DESTINATION));
        dest.addRoute(r);
        r.setOrigin(orig);
        r.setDestination(dest);
        r.setParam(MACRO_PARAMS.ROUTE_LENGTH,
                TravelMaster.calculateRouteLength(r), true);
        return r;
    }

    private static Route createRoute(String origin, String destination,
                                     ObjType t) {
        Route r = new Route(game, t, region.getRef(), region.getPlace(origin),
                region.getPlace(destination));
        region.getPlace(origin).getRoutes().add(r);
        region.getPlace(destination).getRoutes().add(r);
        r.setParam(MACRO_PARAMS.ROUTE_LENGTH,
                TravelMaster.calculateRouteLength(r), true);
        return r;
    }

    private static Place createPlace(MacroRef ref, String s) {
        s = formatPointVarString(s);
        String typeName = VariableManager.removeVarPart(s);
        ObjType t = DataManager.getType(typeName, MACRO_OBJ_TYPES.PLACE);
        Place place = new Place(game, t, ref);
        if (VariableManager.getVarPart(s).contains("-")) {
            Coordinates c = new Coordinates(true, VariableManager.getVarPart(s));
            place.setCoordinates(c);
        } else {
            place.resetCoordinates();
        }
        // List<Dungeon> dungeons =
        // DungeonMaster.generateDungeonsForPlace(place);
        return place;
    }

    private static Town createTown(MacroRef ref, String s) {
        s = formatPointVarString(s);
        ObjType t = DataManager.getType(VariableManager.removeVarPart(s),
                MACRO_OBJ_TYPES.TOWN);
        Town town = new Town(game, t, ref);
        if (VariableManager.getVarPart(s).contains("-")) {
            Coordinates c = new Coordinates(true, VariableManager.getVarPart(s));
            town.setCoordinates(c);
        } else {
            town.resetCoordinates();
        }
        return town;
    }

    private static String formatPointVarString(String s) {
        // StringMaster.getSubString(string, open, close, inclusive) //just
        // first 2 vars!
        while (s.endsWith(StringMaster.getVarSeparator())) {
            s = StringMaster.cropLast(s, 1);
        }
        s = s.replace(StringMaster.getVarSeparator(),
                StringMaster.getCoordinatesSeparator());
        return s;
    }

    public static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        WorldGenerator.world = world;
    }
}
