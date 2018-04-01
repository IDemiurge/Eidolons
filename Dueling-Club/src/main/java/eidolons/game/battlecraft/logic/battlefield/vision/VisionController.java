package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.Structure;
import eidolons.game.core.game.DC_Game;

import java.util.List;

/**
 * Created by JustMe on 3/28/2018.
 * <p>
 * Algorithm
 * <p>
 * game-reset
 * outlines reset
 * visibility reset
 * <p>
 * wallmap reset
 *
 * 1.  STATUS FOR UNIT- (CLEAR) IN SIGHT/ BEYOND SIGHT ...
 * 2.  OUTLINE TYPE - based on gamma or clearshot, determines next
 * 3.  VISIBILITY LEVEL - based on outline+status,
 * CLEAR_SIGHT(no outline)
 * OUTLINE(outline with hints)
 * CONCEALED(just something, no hints, except "last seen here" )
 * BLOCKED(no clearshot) //must be unseen?
 * UNSEEN;
 *
 * 4.  * STATUS FOR PLAYER - DETECTED, KNOWN, UNKNOWN,
 * INVISIBLE - if was seen at least once, but not now (stealth/block...)
 *
 *
 * RULES
 * 1) If a unit is beyond the range of [sight], it is always
 * 2)
 *
 */
public class VisionController {
    DC_Game game;
    VisionMaster master;

    public VisionController(DC_Game game, VisionMaster master) {
        this.game = game;
        this.master = master;
    }

    public void afterCheck() {

        checkOutlines();
        checkVisibilityLevels();
        checkGridVisible();
        checkPlayerVisionStatus();

        //for debug/logging, which is best practice here?
        List<Structure> list = game.getMaster().getWalls();

    }

    private void checkPlayerVisionStatus() {
    }

    private void checkGridVisible() {
    }

    private void checkVisibilityLevels() {
    }

    private void checkOutlines() {
    }

    public enum VISIBILITY_CHECK_OBJ_CASE {
        WALL, UNIT, STRUCTURE
    }

}
