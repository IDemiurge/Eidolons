package macro.entity.action;

import macro.entity.action.MacroActionManager.MACRO_PARTY_ACTIONS;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.map.Place;
import eidolons.macro.map.Route;
import eidolons.macro.map.travel.RestMaster;
import main.content.CONTENT_CONSTS2.MACRO_STATUS;

import java.util.Set;

/**
 * Created by JustMe on 2/23/2018.
 */
public class MapActionHandler {

    private static Place selectMapObj(Set<Place> set) {
//        Integer id = MacroGame.getGame().getManager().select(set);
//        if (id == null) {
//            return null;
//        }
//        Place place = (Place) MacroGame.getGame().getObjectById(id);
//        return place;
        return null;
    }

    public static void partyAction(MACRO_PARTY_ACTIONS action, MacroParty party) {
        Set<Place> set;
        Route route;
        Place place;
        switch (action) {
            case AMBUSH:
                party.setStatus(MACRO_STATUS.IN_AMBUSH);
                // for 'encounters' to happen more likely...
                break;
            case CAMP:
                RestMaster.startCamping(party);

                break;
            case EXPLORE:
                // choose route? or choose current location to explore around...
                party.setStatus(MACRO_STATUS.EXPLORING);
//                set = TravelMasterOld.getAvailableRoutesAsPlaces(party, null);
//                if (party.getCurrentLocation() != null) {
//                    set.add(party.getCurrentLocation()); // else?
//                }
//                place = selectMapObj(set);
//
//                if (place == null) {
//                    return;
//                }
//                party.setCurrentExploration(place);

                break;
            case TRAVEL:
                // extract into effect???
                party.setStatus(MACRO_STATUS.TRAVELING);
//                set = TravelMasterOld.getAvailablePlaces(party);
//                // set.addAll(TravelMaster.getAvailableRoutes(party));
//                place = selectMapObj(set);
//                if (place == null) {
//                    return;
//                }
//                set = TravelMasterOld.getAvailableRoutesAsPlaces(party, place);
//                // MacroManager.getMapView().getMapComp().displayRoutes(set);
//
//                route = (Route) selectMapObj(set);
//                if (route == null) {
//                    return;
//                }
//                party.setCurrentDestination(place);
//                if (route.getOrigin() == place) {
//                    party.addProperty(G_PROPS.DYNAMIC_BOOLS,
//                     DYNAMIC_BOOLS.BACKWARDS_ROUTE_TRAVEL.toString());
//                } else {
//                    party.removeProperty(G_PROPS.DYNAMIC_BOOLS,
//                     DYNAMIC_BOOLS.BACKWARDS_ROUTE_TRAVEL.toString());
//                }
//
//                party.setCurrentRoute(route);

                break;
            default:
                break;

        }
    }
}
