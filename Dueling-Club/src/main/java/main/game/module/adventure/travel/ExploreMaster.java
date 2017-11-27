package main.game.module.adventure.travel;

import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroObj;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Place.PLACE_VISIBILITY_STATUS;
import main.game.module.adventure.map.Route;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

public class ExploreMaster {

    public static void newTurn() {
        for (MacroParty party : MacroGame.getGame().getParties()) {
            if (party.getStatus() != MACRO_STATUS.TRAVELING) {
                return;
            }

            Place place = party.getCurrentExploration();
            if (place instanceof Route) {
                ExploreMaster.exploreRoute(party, (Route) place);
            } else {
                Boolean north_or_south = RandomWizard.random();
                Boolean west_or_east = RandomWizard.random();
                // getOrCreate available directions
                ExploreMaster.exploreLocation(party, north_or_south,
                        west_or_east);
            }

        }
    }

    public static void exploreLocation(MacroParty party,
                                       Boolean north_or_south, Boolean west_or_east) {
        Place place = party.getCurrentLocation();
        List<Place> potentialFinds = getPotentialFindsForLocationExplore(place,
                north_or_south, west_or_east, party);
        // special places - treasure coves, hidden dungeons, secret shops
//        if (checkGotLost(party, route)) {
//            return;
//        }

        // TODO hour at a time approach... choosing direction...

        for (Place p : potentialFinds) {
//            int hours = TravelMaster.getTravelTime(party, place, portrait, true);
//            checkDiscovered(party, portrait, route);
//            if (TimeMaster.hoursLeft() < hours)
//                break;
//            TimeMaster.hoursPassed(hours);
            // allow taking the route immediately? and proceed by exploring it
            // instead?

        }
    }

    private static List<Place> getPotentialFindsForLocationExplore(Place place,
                                                                   Boolean north_or_south, Boolean west_or_east, MacroParty party) {
        List<Place> list = new ArrayList<>();
        for (Route route : place.getRoutes()) {
            if (place.isLinkedToRoute(route)) {
                // either direction -
            }
            Place dest = route.getOtherEnd(place);

            PositionMaster.isAboveOr(dest, place);
            if (PositionMaster.checkDirection(dest, place, north_or_south,
                    west_or_east)) {
                if (checkCapacity(place, dest, party))
                // checkDIstance() instead - EXPLORE_SPEED
                {
                    list.add(dest);
                }
            }

        }

        return list;
    }

    private static Boolean checkCapacity(Place place, Place dest,
                                         MacroParty party) {
        // area mods
        int exploreCapacity = party.getExploreCapacity();
        int survivalCapacity = party.getSurvivalCapacity();
//        int speed = party.getTravelSpeed();
//        return PositionMaster.getDistance(place, dest)
//                * place.getRegion().getMilePerPixel();
        return null;
    }

    public static void exploreRoute(MacroParty party, Route route) {
        // TravelMaster.travel(true, party, route);

        int maxProgress = 0; // limits
        int progress = 0;
        if (checkGotLost(party, route)) {
//            e = EncounterMaster.checkEncounter(party.getArea(), route, party,
//                    true, progress);
            return;
        }

        List<Place> potentialFinds = getPotentialFindsForRouteExplore(route,
                maxProgress);
        Place destination = null;
        for (Place p : potentialFinds) {
            if (!checkDiscovered(party, p, route)) {
                continue;
            }
            // backward?
            progress = maxProgress
                    - p.getIntParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE);
            if (discovered(p)) {
                destination = p;
                break; // ++ choose to go there
            }
        }

        if (destination != null) {
            if (destination instanceof Route) {
                TravelMaster.enterRoute(party, (Route) destination);
            } else {
                TravelMaster.enterPlace(party, destination);
            }
            // TODO spend the "rest" of the progress there?
        }
    }

    private static boolean checkGotLost(MacroParty party, Route route) {
        // make random progress? negative progress?
        // basic version -
        // "You got lost and barely management to find your way back before sundown/rise"

        party.getExploreCapacity();
        party.getSurvivalCapacity();
        // preCheck encounters

        route.getBendFactor();
        MacroObj area = route.getRef().getRegion()
                .getArea(party.getProperty(MACRO_PROPS.AREA)); // getCurrentArea()
//        area.getIntParam(MACRO_PARAMS.PATHFINDING_MOD);

        // roll?

        return RandomWizard.chance(0);
    }

    private static List<Place> getPotentialFindsForRouteExplore(Route route,
                                                                int maxProgress) {
        List<Place> potentialFinds = new ArrayList<>();
        for (Route p : route.getLinkedRoutes()) {
            if (p.getVisibilityStatus() == PLACE_VISIBILITY_STATUS.UNKNOWN) {
                if (p.getIntParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE) < maxProgress) {
                    potentialFinds.add(p);
                }
            }
        }
        for (Place p : route.getLinkedPlaces()) {
            if (p.getVisibilityStatus() == PLACE_VISIBILITY_STATUS.UNKNOWN) {
                if (p.getIntParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE) < maxProgress) {
                    potentialFinds.add(p);
                }
            }
        }
        return potentialFinds;
    }

    private static Boolean discoveredRoute(Place p) {

        p.setVisibilityStatus(PLACE_VISIBILITY_STATUS.DISCOVERED);

//        return promptToTravel(portrait);
        return null;
    }


    private static Boolean discovered(Place p) {
        // if chooses to go into the place...

        p.setVisibilityStatus(PLACE_VISIBILITY_STATUS.DISCOVERED);

//        return promptToStay(portrait);
        return null;
    }


    // ++ from place explore

    private static boolean checkDiscovered(MacroParty party, Place p,
                                           Route route) { // linked place
        int difficulty = route.getBendFactor();
        int exploreCapacity = party.getExploreCapacity();

//        RollMaster.roll(roll_type, success, fail, ref);

        return false;
    }
}
