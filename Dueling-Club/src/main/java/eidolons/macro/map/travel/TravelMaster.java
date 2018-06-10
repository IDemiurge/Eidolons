package eidolons.macro.map.travel;

import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.map.Route;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 2/10/2018.
 */
public class TravelMaster {
    public static void travel(MacroParty party, float delta) {
        Route route = party.getCurrentRoute();
        int distance = route.getLength();
        //bend factor
        int speed = getTravelSpeed(party, route);
        float progress = party.getRouteProgress();
        float newProgress = speed * delta / distance;
        party.setRouteProgress(progress + newProgress);
        int x1 = party.getCurrentLocation().getX();
        int x2 = party.getCurrentDestination().getX();
        int y1 = party.getCurrentLocation().getY();
        int y2 = party.getCurrentDestination().getY();
        int xBase = (int) (x1 + (x2 - x1) * progress);
        int yBase = (int) (y1 + (y2 - y1) * progress);
//            float xOffset= route.getOffsetX(progress);
//            float yOffset;
        party.setX(xBase);
        party.setY(yBase);

    }

    private static int getTravelSpeed(MacroParty party, Route route) {
        int factor = route.getSpeedMod();
        return MathMaster.applyModIfNotZero(party.getTravelSpeed(), factor);
    }

}
