package main.game.battlecraft.logic.battlefield.vision;

import main.ability.effects.common.AddStatusEffect;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.BfObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.FEATURE;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.battlecraft.rules.action.ActionRule;
import main.game.core.game.DC_Game;
import main.game.core.master.BuffMaster;
import main.system.math.PositionMaster;
import main.system.math.roll.RollMaster;

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
        return u.getPlayerVisionStatus(true) == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE;
    }

    public static void applySpotted(Unit target) {
        BuffMaster.applyBuff(SPOTTED, new AddStatusEffect(UnitEnums.STATUS.SPOTTED), target, 1); // TODO
        // also negate concealment? // dispel
        // hidden?
        target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED);
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
    public boolean unitBecomesActive(Unit unit) {
        return true;
    }

    public void actionComplete(ActiveObj active) {
        if (!isOn())
            return;
        DC_ActiveObj action = (DC_ActiveObj) active; // perhaps only moves?
        Unit source = action.getOwnerObj();
//        if (VisionManager.isVisionHacked()) {
//            return;
//        }
//        if (source.getPlayerVisionStatus(true) != UNIT_TO_PLAYER_VISION.INVISIBLE)
//            return ;
        List<? extends DC_Obj> list = Analyzer.getEnemies(source, false, false, false);
        for (DC_Obj sub : list) {
            Unit u = (Unit) sub;
            if (checkHidden(source)) {
//                if (!game.getVisionMaster().getVisibilityMaster().isZeroVisibility(source, true)) {
                // if (ConcealmentRule.getVisibilityLevel(u, source,
                // source.checkInSightForUnit(u)) !=
                // VISIBILITY_LEVEL.CONCEALED) {
                if (u.isUnconscious())
                    continue;
                if (source.getOutlineType()== OUTLINE_TYPE.BLOCKED_OUTLINE)
                    continue;
                checkSpotRoll(u, source);
//                    rollSpotted(u, source, true);
//                }
            }

            if (checkHidden(u)) {
                if (u.getOutlineType()== OUTLINE_TYPE.BLOCKED_OUTLINE)
                    continue;
                checkSpotRoll(source, u);
            }
        }

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
