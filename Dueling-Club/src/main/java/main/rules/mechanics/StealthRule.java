package main.rules.mechanics;

import main.ability.effects.oneshot.special.AddStatusEffect;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.CONTENT_CONSTS.ROLL_TYPES;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.*;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.BuffMaster;
import main.game.battlefield.FacingMaster;
import main.game.battlefield.VisionManager;
import main.rules.action.ActionRule;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.swing.components.obj.drawing.VisibilityMaster;
import main.system.math.PositionMaster;
import main.system.math.roll.RollMaster;

public class StealthRule implements ActionRule {
    /*
	 * After each action by the stealthy unit or the spotter
	 */

    public static final String SPOTTED = "Spotted";
    private static final Integer DETECTION_FACTOR = 3;
    protected DC_Game game;

    public StealthRule(DC_Game g) {
        this.game = g;
    }

    public static boolean checkHidden(DC_HeroObj u) {
        // if (u.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.INVISIBLE)
        // return true;
        if (checkInvisible(u)) // SET STATUS
        {
            u.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.INVISIBLE);
            return true;
        }
        return false;
    }

    public static void applySpotted(DC_HeroObj target) {
        BuffMaster.applyBuff(SPOTTED, new AddStatusEffect(STATUS.SPOTTED), target, 1); // TODO
        // also negate concealment? // dispel
        // hidden?
        target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.DETECTED);
        // to be dispelled by renewed use of Invisiblity or special Hide
        // actions
        // or perhaps upon moving beyond vision range TODO
    }

    public static boolean checkInvisible(DC_Obj unit) {
        if (VisionManager.isVisionHacked()) {
            return false;
        }
        if (unit.checkStatus(STATUS.SPOTTED))
            // ***BY UNIT*** - if "spotter" is killed, can become invisible
            // again!!!
        {
            return false;
        }
        if (unit.checkStatus(STATUS.INVISIBLE))
            // TODO mind-affecting check?
        {
            return true;
        }
        if (unit.checkStatus(STATUS.HIDDEN)) {
            return true; // TODO sight-override?
        }

        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return false; // TODO ???

        if (unit.getIntParam(PARAMS.STEALTH) <= 0) {
            return false;
        } else {

            boolean result = true;
            int stealth = unit.getIntParam(PARAMS.STEALTH);

            for (Obj obj : unit.getGame().getPlayer(!unit.getOwner().isMe()).getControlledUnits()) {

                // if (checkInSight((DC_UnitObj) obj, unit))
                // continue; TODO Used to be like this? If within sight range,
                // no stealth possible?
                int detection = getDetection(unit, obj);
                if (detection >= stealth) { // detected
                    result = false;
                    break;
                }
            }

            return result;
        }

    }

    private static int getDetection(DC_Obj target, Obj source) {
        int distance = PositionMaster.getDistance(source, target);
        if (distance == 0) {
            distance = 1; // TODO ++ CONCEALMENT
        }
        Integer factor = source.getIntParam(PARAMS.SIGHT_RANGE);

        if (FacingMaster.getSingleFacing((DC_UnitObj) source, (BattlefieldObj) target) == FACING_SINGLE.BEHIND) {
            factor = source.getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        } else if (FacingMaster.getSingleFacing((DC_UnitObj) source, (BattlefieldObj) target) == FACING_SINGLE.TO_THE_SIDE) {
            factor -= source.getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
        }
        int detection = factor * source.getIntParam(PARAMS.DETECTION) / distance;
        return detection;
    }

    @Override
    public boolean unitBecomesActive(DC_HeroObj unit) {
        return true;
    }

    public void actionComplete(ActiveObj active) {
        DC_ActiveObj action = (DC_ActiveObj) active; // perhaps only moves?
        DC_HeroObj source = action.getOwnerObj();
        if (VisionManager.isVisionHacked()) {
            return;
        }
        for (DC_HeroObj u : game.getUnits()) {
            if (checkHidden(source)) {
                if (!VisibilityMaster.isZeroVisibility(source, true)) {
                    // if (ConcealmentRule.getVisibilityLevel(u, source,
                    // source.checkInSightForUnit(u)) !=
                    // VISIBILITY_LEVEL.CONCEALED) {
                    rollSpotted(u, source);
                    rollSpotted(u, source, true);
                }
            }

            if (checkHidden(u)) {
                if (ConcealmentRule.getVisibilityLevel(source, u) != VISIBILITY_LEVEL.CONCEALED) {
                    rollSpotted(source, u);
                }
            }
        }

    }

    // ++ SEARCH ACTION!
    public boolean rollSpotted(DC_HeroObj activeUnit, DC_HeroObj target) {
        return rollSpotted(activeUnit, target, false);
    }

    public boolean rollSpotted(DC_HeroObj activeUnit, DC_HeroObj target, boolean hearing) {
        if (activeUnit.isOwnedBy(target.getOwner())) {
            return false;
        }
        // TODO if (checkDistance()) return false;
        Ref ref = activeUnit.getRef().getCopy();
        ref.setTarget(target.getId());
        // TODO mind-affecting vs Spell Invisibility!

        int detection = getDetection(target, activeUnit);
        int base_detection = activeUnit.getIntParam(PARAMS.DETECTION);
        activeUnit.setParam(PARAMS.DETECTION, detection);

        boolean result = false;
        ref.setAnimationDisabled(true);
        try {
            result = RollMaster.roll(ROLL_TYPES.STEALTH, ref);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            activeUnit.setParam(PARAMS.DETECTION, base_detection);
        }
        if (result) {
            applySpotted(target);
        }

        return result;
    }

}
