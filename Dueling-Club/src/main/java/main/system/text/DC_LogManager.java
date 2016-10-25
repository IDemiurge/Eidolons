package main.system.text;

import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.entity.Ref;
import main.entity.obj.DC_Obj;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.StringMaster;

public class DC_LogManager extends LogManager {

    public DC_LogManager(Game game) {
        super(game);
    }

    /*
     * Do I need to have information levels? What is the best way to access the
     * log and control its filtering?
     */
    public void logMovement(DC_Obj obj, Coordinates c) {
        if (obj.getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.INVISIBLE)
            return;
        String name = obj.getName();
        // if (obj.getActivePlayerVisionStatus() ==
        // UNIT_TO_PLAYER_VISION.UNKNOWN)
        // name = "A unit";

        String entry = name + " has moved to a new position at " + c.toString();

        entry = StringMaster.MESSAGE_PREFIX_PROCEEDING + entry;
        main.system.auxiliary.LogMaster.log(1, entry);
    }

    public boolean logMovement(Ref ref) {
        logMovement((DC_Obj) ref.getSourceObj(), ref.getTargetObj().getCoordinates());
        return true;
    }

}
