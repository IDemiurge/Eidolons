package eidolons.macro.global;

import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.map.Place;
import eidolons.macro.map.Route;
import main.entity.DataModel;
import main.entity.Ref;
import main.game.core.game.Game;
import main.system.text.LogManager;

public class Journal extends LogManager {
    static Journal instance; // multiparty isn't really supported

    public Journal(Game game) {
        super(game);
        instance = this;
    }

    public static void logTravelComplete(Boolean north_or_south,
                                         Boolean west_or_east, Place place, Route route, MacroParty party,
                                         int hoursLeft) {

    }

	/*
     * a "log" that can be filtered
	 */

    public static void logTravelProgress(Boolean north_or_south,
                                         Boolean west_or_east, int distance, Integer progress, Route route,
                                         MacroParty party) {

        String direction = "";
        if (north_or_south != null) {
            direction += north_or_south ? "North" : "South";
            if (west_or_east != null) {
                direction += "-";
                direction += north_or_south ? "West" : "East";
            }
        } else {
            direction += north_or_south ? "West" : "East";
        }
        // "Your party" ?
        String string = party.getDisplayedName() + " has traveled " + distance
         + " leagues " + direction + " along the "
         + route.getDisplayedName();
        getInstance().log(string);

    }

    public static Journal getInstance() {
        return instance;
    }

    @Override
    public boolean logMovement(Ref ref) {
        return false;
    }

    @Override
    public boolean log(LOGGING_DETAIL_LEVEL log, String entry) {
        return false;
    }

    @Override
    public void logCounterModified(DataModel entity, String name, int modValue) {

    }


}
