package eidolons.system.hotkey;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Core;
import main.game.bf.directions.FACING_DIRECTION;

public class Accessibility {

    public static char checkReplaceWasd(char aChar) {
        FACING_DIRECTION moveDirection = getAbsoluteDirectionForWasd(aChar);

        if (moveDirection == null)
            return aChar;

        Unit unit = null;
        try {
            unit = Core.getGame().getLoop().getActiveUnit();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (unit == null)
            unit = Core.getMainHero();
        FACING_DIRECTION facing = unit.getFacing();
        return getCorrectedWsad(facing, moveDirection);
    }

    public static FACING_DIRECTION getAbsoluteDirectionForWasd(char aChar) {
        switch (aChar) {
            case 'w':
                return FACING_DIRECTION.NORTH;
            case 'a':
                return FACING_DIRECTION.WEST;
            case 's':
                return FACING_DIRECTION.SOUTH;
            case 'd':
                return FACING_DIRECTION.EAST;
        }
        return null;
    }

    public static char getCorrectedWsad(FACING_DIRECTION facing,
                                        FACING_DIRECTION moveDirection) {
        int degrees = (360 + moveDirection.getDirection().getDegrees()
                - facing.getDirection().getDegrees() + 90) % 360;
        switch (degrees) {
            case 0:
                return 'd';
            case 90:
                return 'w';
            case 180:
                return 'a';
            case 270:
                return 's';
        }
        return 0;
    }

    public static boolean isActionNotBlocked(DC_ActiveObj activeObj, boolean exploreMode) {
        if (activeObj.getOwnerUnit() == Core.getMainHero())
            if (EidolonsGame.TUTORIAL) {
                if (EidolonsGame.TURNS_DISABLED)
                    if (activeObj.isTurn())
                        return false;
                if (activeObj.isMove())
                    if (EidolonsGame.MOVES_DISABLED) {
                        return false;
                    }
                if (activeObj.isAttackAny())
                    return !EidolonsGame.ATTACKS_DISABLED;

                if (activeObj.isMode()) {
                    return EidolonsGame.getActionSwitch(activeObj.getName());
                }
                if (!activeObj.isSpell()) {
                    return EidolonsGame.getActionSwitch(activeObj.getName());
                }
            }
        return true;
    }
}
