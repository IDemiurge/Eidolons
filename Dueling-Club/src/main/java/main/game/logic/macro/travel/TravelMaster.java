package main.game.logic.macro.travel;

import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.PARAMS;
import main.content.parameters.MACRO_PARAMS;
import main.content.properties.G_PROPS;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.game.logic.dungeon.Location;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.global.Journal;
import main.game.logic.macro.global.TimeMaster;
import main.game.logic.macro.map.Place;
import main.game.logic.macro.map.Route;
import main.system.auxiliary.RandomWizard;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.HashSet;
import java.util.Set;

public class TravelMaster {

    private static final int DEFAULT_TRAVEL_SPEED = 4;
    private static boolean testMode = true;

    public static int calculateRouteLength(Route route) {
        int distance = PositionMaster.getDistance(route.getOrigin().getCoordinates(), route
                .getDestination().getCoordinates());
        float scale = route.getRef().getRegion().getMilePerPixel();

        return Math.round(distance * scale);

    }

    public static Set<Place> getAvailablePlaces(MacroParty party) {
        Set<Place> list = new HashSet<>();
        // via available routes! across Areas...
        Set<Route> routes = getAvailableRoutes(party);
        for (Route r : routes) {
            // if (r.getDestination())
            list.add(r.getDestination());
            list.add(r.getOrigin());
        }

        return list;
    }

    public static Set<Route> getAvailableRoutes(MacroParty party) {
        return getAvailableRoutes(party, null);
    }

    public static Set<Route> getAvailableRoutes(MacroParty party, Place destination) {
        Set<Route> list = new HashSet<>();
        for (Route route : party.getRegion().getRoutes()) {
            boolean result = false;
            if (destination == null) {
                result = checkAvailableRoute(party, route);
            } else {
                result = checkRouteForDestination(route, destination);
            }
            if (result) {
                list.add(route);
            }
        }
        return list;
    }

    private static boolean checkRouteForDestination(Route route, Place destination) {
        // TODO via linked?
        return route.getOrigin() == destination || route.getDestination() == destination;
    }

    private static boolean checkAvailableRoute(MacroParty party, Route route) {
        if (route.getOrigin() == party.getCurrentLocation()
                || route.getDestination() == party.getCurrentLocation()) {
            return true;
        }
        for (Route r : route.getLinkedRoutes()) {
            if (party.getCurrentRoute() == r) {
                return true;
            }
        }
        for (Place p : route.getLinkedPlaces()) {
            if (party.getCurrentLocation() == p) {
                return true;
            }
        }
        return false;
    }

    public static void newTurn() {
        for (MacroParty party : MacroGame.getGame().getParties()) {
            if (party.getStatus() != MACRO_STATUS.TRAVELING) {
                return;
            }
            // party.newTurn(); already done
            Route route = party.getCurrentRoute();
            if (route == null) {
                continue;
            }
            // TODO perhaps travel PER HOUR with a mini-message with progress
            // updates... although if 'nothing happens', it certainly should be
            // skippable
            travel(party, route);
            MacroManager.refreshGui();
            // while (TimeMaster.hoursLeft() > 1) {
            // if (!travel(party, route, 1))
            // break;
            // // TODO reduce Vigor gradually... notify and ask - linked stuff,
            // // possible discoveries (less likely than explore mode but
            // // still... maybe it should be *the same* just with different
            // // stats!
            // MacroManager.refreshGui();
            // }
        }
    }

    private static void travel(MacroParty party, Route route) {
        travel(party, route, TimeMaster.getHoursPerTurn());
    }

    private static boolean travel(MacroParty party, Route route, int hoursToTravel) {
        int length = route.getLength();

        int mod = route.getSpeedMod();
        mod = mod - mod * route.getBendFactor() / 100;
        Boolean back = party // TODO
                .checkBool(DYNAMIC_BOOLS.BACKWARDS_ROUTE_TRAVEL);
        int leaguesTraveled = party.getIntParam(MACRO_PARAMS.TRAVEL_SPEED) * hoursToTravel * mod
                / 100;
        int progress = party.getIntParam(MACRO_PARAMS.ROUTE_PROGRESS);
        int progressPercentageMade = 100 * leaguesTraveled / length;
        TimeMaster.hoursPassed(1);
        Encounter e = null;
        if (!testMode) {
            try {
                e = EncounterMaster.checkEncounter(party, progressPercentageMade);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if (e != null) {
            progress = e.getProgressMadeBeforeBattle();// time instead?
            hoursToTravel = TimeMaster.getHoursPerTurn() - e.getHoursIntoTheTurn();
            boolean result = EncounterMaster.resolveEncounter(e);
            if (result) {
                progress += party.getIntParam(MACRO_PARAMS.TRAVEL_SPEED) * mod / 100
                        * hoursToTravel;
            } else {
                return false;
            }
        }

        progress += leaguesTraveled;
        // Boolean north_or_south = PositionMaster.isAboveOr(
        // party.getCurrentDestination(), party.getLastLocation());
        // Boolean west_or_east = PositionMaster.isToTheLeftOr(
        // party.getCurrentDestination(), party.getLastLocation());
        if (progress < length) {
            int progressPerc = 100 * route.getLength() / progress;
            route.setParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE, progressPerc);
            Journal.logTravelProgress(RandomWizard.random(), RandomWizard.random(),
                    leaguesTraveled, progress, route, party);
            party.setParam(MACRO_PARAMS.ROUTE_PROGRESS, progress);
            // backwards? undo progress if necessary... negative progress
            // for
            // getting
            return true;
        } else {
            leaguesTraveled = length - progress;
            progress = length;
            int progressPerc = 100 * route.getLength() / progress;
            route.setParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE, progressPerc);
            Place place = back ? route.getOrigin() : route.getDestination();
            int hoursLeft = (progress - length) * TimeMaster.getHoursPerTurn()
                    / party.getIntParam(MACRO_PARAMS.TRAVEL_SPEED);
            Journal.logTravelComplete(RandomWizard.random(), RandomWizard.random(), back ? route
                    .getOrigin() : route.getDestination(), route, party, hoursLeft);
            // TimeMaster.setTimeRemaining(hoursLeft);
            enterPlace(party, place);
            // party.useTime(hoursLeft);
            return false;
        }
    }

    public static void enterRoute(MacroParty party, Route destination) {
        party.setCurrentRoute(destination);

    }

    public static void enterPlace(MacroParty party, Place destination) {
        party.setCurrentPlace(destination);
        party.setCurrentDestination(null);
        party.setCurrentRoute(null);
        /*
		 * init dialogue 
		 * 1) Entering
		 * 2) Info 
		 * 3) Choices 
		 */
        // int outcome = 0;
        String typeName = "";
        // destination.getTopDungeonName();
        // DC_Game.game.getDungeonMaster().initDungeon(typeName, destination);
        if (destination.getTopDungeon() == null) {
            destination.setTopDungeon(new Location(destination).construct());
        }
        DC_Game.game.getDungeonMaster().setDungeon(destination.getTopDungeon());
        // other setups? battlemanager...
        Launcher.launchDC(party.getName());

    }

    public static Set<Place> getAvailableRoutesAsPlaces(MacroParty party, Place place) {
        Set<Route> routes = getAvailableRoutes(party, place);
        return new HashSet<Place>(routes);
    }

    public static float getTravelSpeedDynamic(DC_HeroObj unit) {
        // reset unit? for HC ... maybe that's why those valueIcons are base[]
        // sometimes
        int mod = 100 * unit.getIntParam(PARAMS.CARRYING_CAPACITY) / 2
                / Math.max(1, unit.initCarryingWeight());
        int dex_mod = Math.min(mod / 2, unit.getIntParam(PARAMS.DEXTERITY));
        return getTravelSpeed(unit, mod, dex_mod);
    }

    public static float getTravelSpeed(Entity entity) {
        int dex_mod = Math.min(50, entity.getIntParam(PARAMS.BASE_DEXTERITY));
        return getTravelSpeed(entity, 100, dex_mod);
    }

    public static float getTravelSpeed(Entity entity, int mod, int dex_mod) {
        // what about ObjTypes?
        // survival/mobility/special param?
        mod = Math.min(mod, 200);

        boolean flyer = entity.checkProperty(G_PROPS.STANDARD_PASSIVES, ""
                + STANDARD_PASSIVES.FLYING);
        float speed = new Float(entity.getIntParam(MACRO_PARAMS.TRAVEL_SPEED, true)); //
        if (speed == 0) {
            speed = DEFAULT_TRAVEL_SPEED;
        }

        speed += MathMaster.getFractionValueCentimal(((int) speed), mod);
        speed += MathMaster.getFractionValueCentimal(((int) speed), dex_mod);
        if (flyer) {
            speed = speed * 3 / 2;
        }
        return speed;

    }
}
