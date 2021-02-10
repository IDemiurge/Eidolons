package macro.generation;

import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.screens.map.town.navigation.data.NavigationMaster;
import macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.faction.Faction;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.entity.town.Town;
import eidolons.macro.global.World;
import eidolons.macro.map.Place;
import eidolons.macro.map.Region;
import eidolons.macro.map.Route;
import eidolons.macro.map.area.Area;
import main.content.DC_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class WorldGenerator {
    static Region region;
    private static MacroGame game;
    private static World world;

    private final static String defaultWorldName = "Test World";

    public static World generateWorld(MacroRef ref) {
        ObjType wType = DataManager.getType(defaultWorldName,
         MACRO_OBJ_TYPES.WORLD);
        game = ref.getGame();
        world = new World(ref.getGame(), wType, ref);
        ref.getGame().setWorld(world);
        world.setRegions(generateRegions(ref));

//        FactionMaster.generateFactions(ref);
        // parties
        return world;

    }

    public static List<Region> generateRegions(MacroRef ref) {
        List<Region> regions = new ArrayList<>();
        for (String s : ContainerUtils.open(world
         .getProperty(MACRO_PROPS.REGIONS))) {
            ObjType type = DataManager.getType(s, MACRO_OBJ_TYPES.REGION);
            region = createRegion(type, ref);
            regions.add(region);
//            AreaManager.assignPlacesToAreas(region);
//            if (!MacroManager.isEditMode()) {
//                AreaManager.initRegionAreas(region);
//            }

        }
        return regions;
    }

    public static Region createRegion(ObjType type, MacroRef ref) {
        // template
        // add places, towns and routes

        region = new Region(game, type, ref);
        // init default towns/places ; then add randomized
        for (String s : ContainerUtils.open(region
         .getProperty(MACRO_PROPS.AREAS))) {
            type = DataManager.getType(s, MACRO_OBJ_TYPES.AREA);
            Area area = new Area(ref.getGame(), type, ref);
            region.getAreas().add(area);
        }
        for (String s : ContainerUtils.open(region
         .getProperty(MACRO_PROPS.PARTIES))) {
            try {
                MacroParty party = createParty(ref, s);
                region.addParty(party);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        for (String s : ContainerUtils.open(region
         .getProperty(MACRO_PROPS.PLACES))) {
            Place place = createPlace(ref, s);
            if (place != null)
                region.addPlace(place);
        }
        for (String s : ContainerUtils.open(region
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

    private static void generateFactions() {
//        for (String sub : ContainerUtils.openContainer(world.getProperty(MACRO_PROPS.FACTIONS))) {
//            boolean me = world.checkProperty(MACRO_PROPS.PLAYER_FACTION, sub);
//            ObjType type = DataManager.getType(sub, MACRO_OBJ_TYPES.FACTIONS);
//            if (type == null) {
//                type = new ObjType(sub, MACRO_OBJ_TYPES.FACTIONS);
//            }
//            FLAG_COLOR color =
//             new EnumMaster<FLAG_COLOR>().retrieveEnumConst
//              (FLAG_COLOR.class, type.getProperty(PROPS.FLAG_COLOR));
//            if (color == null)
//                color = FLAG_COLOR.BROWN;
//            DC_Player player = new DC_Player(sub, color,
//             type.getProperty(G_PROPS.EMBLEM), type.getProperty(G_PROPS.IMAGE),
//             me ? ALLEGIENCE.ALLY : ALLEGIENCE.ENEMY);
//
//            Faction faction = new Faction(type, player);
//            game.addFaction(faction);
//            if (me)
//                game.setPlayerFaction(faction);
//        }
    }

    private static ObjType getMacroPartyType(Party party) {
        ObjType type = new ObjType(party.getType());
        type.initType();
        return type;
    }

    private static MacroParty createParty(MacroRef ref, String s) {
        Coordinates coordinates = Coordinates.get(true,
         Integer.parseInt(VariableManager.getVar(s, 0)),
         Integer.parseInt(VariableManager.getVar(s, 1)));
        String string = (VariableManager.getVar(s, 2));
        Faction faction;
        if (StringMaster.isEmpty(string)) {
            faction = game.getPlayerFaction();
        } else if (string.equalsIgnoreCase("player")) {
            Party party = DC_Game.game.getMetaMaster().getPartyManager().getParty();
            if (party == null) {
                return null;
            }
            MacroParty playerParty = new MacroParty(
             getMacroPartyType(party), game, ref,
             party);
            game.setPlayerParty(playerParty);
            playerParty.setFaction(game.getPlayerFaction());
            playerParty.setCoordinates(coordinates);

            if (NavigationMaster.isTestOn())
                playerParty.setCurrentPlace(game.getPlaces().get(0));

            return playerParty;
        } else
            faction = game.getFaction(string);

        String name = VariableManager.removeVarPart(s);

        MacroParty party = new MacroParty(DataManager.getType(name, DC_TYPE.PARTY), ref.getGame(), ref);
        party.setCoordinates(coordinates);

        party.setFaction(faction);
        party.setOriginalOwner(faction.getOwner());
        party.setOwner(faction.getOwner());

        return party;
    }

    private static void generateRoutes() {
        // TODO external routes
        String property = region.getProperty(MACRO_PROPS.INTERNAL_ROUTES);
        for (String routeTypeName : ContainerUtils.open(property)) {
            ObjType routeType = DataManager.getType(routeTypeName,
             MACRO_OBJ_TYPES.ROUTE);
            Route r;
            try {
                r = createRoute(routeType);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
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
            for (String p : ContainerUtils.open(r
             .getProperty(MACRO_PROPS.LINKED_PLACES))) {
                r.addLinkedPlace(region.getPlace(p));
            }
            for (String p : ContainerUtils.open(r
             .getProperty(MACRO_PROPS.LINKED_TOWNS))) {
                r.addLinkedTown(region.getTown(p));
            }
            for (String p : ContainerUtils.open(r
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
//        r.setParam(MACRO_PARAMS.ROUTE_LENGTH,
//         TravelMasterOld.calculateRouteLength(r), true);
        return r;
    }


    private static Place createPlace(MacroRef ref, String s) {
        s = formatPointVarString(s);
        String typeName = VariableManager.removeVarPart(s);
        ObjType t = DataManager.getType(typeName, MACRO_OBJ_TYPES.PLACE);
        if (t == null) {
            return null;
        }
        Place place = new Place(game, t, ref);
        if (VariableManager.getVarPart(s).contains("-")) {
            Coordinates c = Coordinates.get(true, VariableManager.getVarPart(s));
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
            Coordinates c = Coordinates.get(true, VariableManager.getVarPart(s));
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
