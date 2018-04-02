package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.rules.RuleMaster;
import eidolons.game.battlecraft.rules.RuleMaster.FEATURE;
import eidolons.game.battlecraft.rules.RuleMaster.RULE;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.BuffMaster;
import eidolons.system.math.roll.RollMaster;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.system.math.PositionMaster;

import java.util.List;

public class StealthRule implements ActionRule {
    /*
     * After each action by the stealthy unit or the spotter
	 */

    public static final String SPOTTED = "Spotted";
    private static final Integer DETECTION_FACTOR = 3;
    private static final int DETECTION_BONUS_MAIN_HERO = 12;
    protected DC_Game game;

    public StealthRule(DC_Game g) {
        this.game = g;
    }

    public static boolean checkHidden(Unit u) {
        return u.getPlayerVisionStatus(true) == PLAYER_VISION.INVISIBLE;
    }

    public static void applySpotted(Unit target) {
        BuffMaster.applyBuff(SPOTTED, new AddStatusEffect(UnitEnums.STATUS.SPOTTED), target, 1); // TODO
        // also negate concealment? // dispel
        // hidden?
        target.setPlayerVisionStatus(PLAYER_VISION.DETECTED);
        // to be dispelled by renewed use of Invisiblity or special Hide
        // actions
        // or perhaps upon moving beyond vision range TODO
    }

    public static boolean checkInvisible(DC_Obj unit) {
        if (!RuleMaster.checkFeature(FEATURE.VISIBILITY))
            return false;
        if (VisionManager.isVisionHacked()) {
            return false;
        }
        if (unit.checkStatus(UnitEnums.STATUS.SPOTTED))
        // ***BY UNIT*** - if "spotter" is killed, can become invisible
        // again!!!
        {
            return false;
        }
        if (unit.checkStatus(STATUS.UNCONSCIOUS))
            return false;
        if (unit.checkStatus(UnitEnums.STATUS.INVISIBLE))
        // TODO mind-affecting preCheck?
        {
            return true;
        }
        if (unit.checkStatus(UnitEnums.STATUS.HIDDEN)) {
            return true; // TODO sight-override?
        }

        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return false; // TODO ???

        if (unit.getIntParam(PARAMS.STEALTH) <= 0) {
            return false;
        } else {

            boolean result = true;
            int stealth = unit.getIntParam(PARAMS.STEALTH);

            for (Unit obj : unit.getGame().getPlayer(!unit.getOwner().isMe()).getControlledUnits_()) {

                // if (checkInSight((DC_UnitObj) obj, unit))
                // continue; TODO Used to be like this? If within sight range,
                // no stealth possible?
                int detection = getDetection(unit, obj);
                if (obj.isMine()) {
                    if (obj.isMainHero())
                        detection += DETECTION_BONUS_MAIN_HERO;
                }
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

        if (FacingMaster.getSingleFacing((DC_UnitModel) source, (BfObj) target) == UnitEnums.FACING_SINGLE.BEHIND) {
            factor = source.getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        } else if (FacingMaster.getSingleFacing((DC_UnitModel) source, (BfObj) target) == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
            factor -= source.getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
        }
        int detection = factor * source.getIntParam(PARAMS.DETECTION) / distance;
        return detection;
    }

    @Override
    public boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return true;
    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        return true;
    }

    public void actionComplete(ActiveObj active) {
        if (!isOn())
            return;
        DC_ActiveObj action = (DC_ActiveObj) active; // perhaps only moves?
        Unit source = action.getOwnerObj();
        List<? extends DC_Obj> list = Analyzer.getEnemies(source, false, false, false);

        list.removeIf(unit -> {
            double d = PositionMaster.getExactDistance(source, unit);
            if ((d > getMaxDistance(source, unit))) {
                return true;
            }
            if ((d > getMaxDistance((Unit) unit, source))) {
                return true;
            }
            return false;
        });
        for (DC_Obj sub : list) {
            Unit u = (Unit) sub;
            if (checkHidden(source)) {
                if (u.isUnconscious())
                    continue;
                if (source.getOutlineType() == OUTLINE_TYPE.BLOCKED_OUTLINE)
                    continue;
                checkSpotRoll(u, source);
            }

            if (checkHidden(u)) {
                if (u.getOutlineType() == OUTLINE_TYPE.BLOCKED_OUTLINE)
                    continue;
                checkSpotRoll(source, u);
            }
        }

    }

    private double getMaxDistance(Unit source, DC_Obj unit) {
        return (source.getSightRangeTowards(unit) + 1) * 2;
    }

    private void checkSpotRoll(Unit spotter, Unit unit) {
        VISIBILITY_LEVEL vl = game.getVisionMaster().getVisibilityMaster().
         getUnitVisibilityLevel(spotter, unit);
        if (vl != VISIBILITY_LEVEL.BLOCKED)
            if (vl != VISIBILITY_LEVEL.UNSEEN)
//                if (vl != VISIBILITY_LEVEL.CONCEALED)
            {
                rollSpotted(spotter, unit);
            }
    }

    private boolean isOn() {
        return RuleMaster.isRuleOn(RULE.STEALTH);
    }

    // ++ SEARCH ACTION!
    public boolean rollSpotted(Unit activeUnit, Unit target) {
        return rollSpotted(activeUnit, target, false);
    }

    public boolean rollSpotted(Unit activeUnit, Unit target, boolean hearing) {
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
            result = RollMaster.roll(GenericEnums.ROLL_TYPES.STEALTH, ref);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            activeUnit.setParam(PARAMS.DETECTION, base_detection);
        }
        if (result) {
            applySpotted(target);
        }

        return result;
    }

}
