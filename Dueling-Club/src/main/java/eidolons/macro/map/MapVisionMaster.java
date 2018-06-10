package eidolons.macro.map;

import eidolons.macro.MacroGame;
import eidolons.macro.map.travel.RouteMaster;
import eidolons.macro.entity.party.MacroParty;

/**
 * Created by JustMe on 2/22/2018.
 */
public class MapVisionMaster {
    MacroGame game;

    public void update(float delta) {
        MacroParty party = game.getPlayerParty();
        Place location = game.getPlayerParty().getCurrentLocation();

        for (Place sub : game.getPlaces()) {
            int distance = RouteMaster.getDistance(location, sub);
            int scoutRange = party.getExploreCapacity();
            MAP_OBJ_INFO_LEVEL info_level = MAP_OBJ_INFO_LEVEL.INVISIBLE;
            if (distance <= scoutRange) {
                if (sub.isHidden()) {
                    //check
                } else
                    info_level = MAP_OBJ_INFO_LEVEL.VISIBLE;
                sub.setDetected(true);
            } else {
                if (sub.isDetected())
                    info_level = MAP_OBJ_INFO_LEVEL.KNOWN;
                else {
                    if (sub.isHidden())
                        info_level = MAP_OBJ_INFO_LEVEL.INVISIBLE;
                    else
                        info_level = MAP_OBJ_INFO_LEVEL.UNKNOWN;
                }
            }

//sight range? route length?
            //check concealed!


            sub.setInfoLevel(info_level);
        }


    }

    public void act(float delta) {

    }

    public enum MAP_OBJ_INFO_LEVEL {
        VISIBLE, KNOWN, CONCEALED, UNKNOWN, INVISIBLE
    }
}
