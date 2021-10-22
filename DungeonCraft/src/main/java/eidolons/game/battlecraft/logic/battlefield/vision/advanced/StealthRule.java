package eidolons.game.battlecraft.logic.battlefield.vision.advanced;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.DC_UnitModel;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.BuffMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.math.roll.Roll;
import eidolons.system.math.roll.RollMaster;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;
import main.system.sound.AudioEnums;

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


    public static void applySpotted(Unit target) {
        if (target.isPlayerCharacter()) {
            EUtils.showInfoText("You have been spotted!");
        } else {
            if (!target.isMine()) {
                EUtils.showInfoText("Spotted " + target);
            }
        }
        BuffMaster.applyBuff(SPOTTED, new AddStatusEffect(UnitEnums.STATUS.SPOTTED), target, 1); // TODO
        // also negate concealment? // dispel
        // hidden?
        target.setPlayerVisionStatus(PLAYER_VISION.DETECTED);

        DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.SPOT, target);
        target.setSneaking(false);
        // to be dispelled by renewed use of Invisiblity or special Hide
        // actions
        // or perhaps upon moving beyond vision range TODO
    }

    public boolean checkInvisible(DC_Player player, BattleFieldObject unit) {
        if (VisionHelper.isVisionHacked()) {
            if (!unit.isMine())
                return false;
        }
        if (unit.isSpotted())
        // ***BY UNIT*** - if "spotter" is killed, can become invisible
        // again!!!
        {
            return false;
        }
        if (unit.checkStatus(STATUS.UNCONSCIOUS))
            return false;
//        if (unit.checkStatus(UnitEnums.STATUS.INVISIBLE))
//        // TODO mind-affecting preCheck?
//        {
//            return true;
//        }
//        if (unit.checkStatus(UnitEnums.STATUS.HIDDEN)) {
//            return true; // TODO sight-override?
//        }

        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return false; // TODO ???

        boolean result = game.getVisionMaster().getVisionController()
                .getDetectionMapper().get(player, unit);
        if (result) {
            game.getVisionMaster().getVisionController()
                    .getStealthMapper().set(player, unit, false);
            return false;
        }
        Boolean stealth = game.getVisionMaster().getVisionController()
                .getStealthMapper().get(player, unit);
        if (stealth != null)
            result = stealth;
        else
            result = checkStealth(unit);

        game.getVisionMaster().getVisionController()
                .getStealthMapper().set(player, unit, result);
        return result;
    }

    private boolean checkStealth(BattleFieldObject unit) {
        boolean result = false;
        if (unit.getIntParam(PARAMS.STEALTH) > 0) {
            result = true;
            int stealth = unit.getIntParam(PARAMS.STEALTH);

            for (Unit obj : unit.getGame().getPlayer(!unit.getOwner().isMe()).collectControlledUnits_()) {
                // if (checkInSight((DC_UnitObj) obj, unit))
                // continue; TODO Used to be like this? If within sight range,
                // no stealth possible?
                int detection = getDetection(unit, obj, null);
                if (obj.isPlayerCharacter())
                    detection += DETECTION_BONUS_MAIN_HERO;

                if (detection >= stealth) { // detected
                    result = false;
                    break;
                }
            }

        }
        if (result) {
            unit.setSneaking(true);
        }
        return result;
    }

    private static int getDetection(DC_Obj target, Obj source, DC_ActiveObj action) {
        int distance = PositionMaster.getDistance(source, target);
        if (distance == 0) {
            distance = 1; // TODO ++ CONCEALMENT
        }
        Integer factor = 2 * source.getIntParam(PARAMS.SIGHT_RANGE);

        if (FacingMaster.getSingleFacing((DC_UnitModel) source, (BfObj) target) == UnitEnums.FACING_SINGLE.BEHIND) {
            factor = source.getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        } else if (FacingMaster.getSingleFacing((DC_UnitModel) source, (BfObj) target) == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
            factor -= source.getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
        }
        if (action != null) {
            if (action.isAttackAny()) {
                factor = factor * 2;
            }
            if (action.isSpell()) {
                factor = factor * 3 / 2;
            }
        }

        return factor * source.getIntParam(PARAMS.DETECTION) / distance;
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
        Unit source = action.getOwnerUnit();
        List<? extends DC_Obj> list = Analyzer.getEnemies(source, false, false, false);

        list.removeIf(unit -> {
            double d = PositionMaster.getExactDistance(source, unit);
            if ((d > getMaxDistance(source, unit))) {
                return true;
            }
            return (d > getMaxDistance((Unit) unit, source));
        });
        for (DC_Obj sub : list) {
            Unit unit = (Unit) sub;
            double d = PositionMaster.getExactDistance(source, unit);

            if ((d <= getMaxDistance(unit, source)) || action.getChecker().isPotentiallyHostile()) {
                if (isSpotRollAllowed(action, unit, source)) {
                    rollSpotted(unit, source, action);
                }
            }

            if ((d <= getMaxDistance(source, unit))) {
                if (isSpotRollAllowed(action, source, unit)) {
                    rollSpotted(source, unit, action);
                }
            }
        }

    }

    private boolean isSpotRollAllowed(DC_ActiveObj action, Unit source, Unit unit) {
        if (source.isUnconscious())
            return false;
        if (!unit.isSneaking()) {
            return false;
        }
        if (action.getChecker().isPotentiallyHostile())
            return true;
        UNIT_VISION status = unit.getUnitVisionMapper().get(source, unit);
        if (status == UNIT_VISION.BEYOND_SIGHT)
            return false;
        if (status == UNIT_VISION.BLOCKED)
            return false;
        if (PositionMaster.getExactDistance(source, unit) > source.getMaxVisionDistance()) {
            return false;
        }
        return unit.getPlayerVisionMapper().get(source.getOwner(), unit)
                == PLAYER_VISION.INVISIBLE;
    }

    private double getMaxDistance(Unit source, DC_Obj unit) {
        return (source.getSightRangeTowards(unit) + 1) * 2;
    }

//    private void checkSpotRoll(Unit spotter, Unit unit) {
//        VISIBILITY_LEVEL vl = game.getVisionMaster().getVisibilityMaster().
//         getUnitVisibilityLevel(spotter, unit);
//        if (vl != VISIBILITY_LEVEL.BLOCKED)
//            if (vl != VISIBILITY_LEVEL.UNSEEN)
////                if (vl != VISIBILITY_LEVEL.CONCEALED)
//            {
//                rollSpotted(spotter, unit);
//            }
//    }

    private boolean isOn() {
        return RuleKeeper.isRuleOn(RuleEnums.RULE.STEALTH);
    }

    // ++ SEARCH ACTION!
    public boolean rollSpotted(Unit activeUnit, Unit target, DC_ActiveObj action) {
        return rollSpotted(activeUnit, target, false, action);
    }

    public boolean rollSpotted(Unit activeUnit, Unit target, boolean hearing, DC_ActiveObj action) {
        if (activeUnit.isOwnedBy(target.getOwner())) {
            return false;
        }
        // TODO if (checkDistance()) return false;
        Ref ref = activeUnit.getRef().getCopy();
        ref.setTarget(target.getId());
        // TODO mind-affecting vs Spell Invisibility!

        int detection = getDetection(target, activeUnit, action);
        int base_detection = activeUnit.getIntParam(PARAMS.DETECTION);
        activeUnit.setParam(PARAMS.DETECTION, detection);

        boolean result = false;
        ref.setAnimationDisabled(true);
        try {
            //TODO rpg Review
            // result = RollMaster.roll(GenericEnums.RollType.stealth, ref);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            //TODO better way to do it?
            activeUnit.setParam(PARAMS.DETECTION, base_detection);
        }
        Roll roll = RollMaster.getLastRoll();

        if (result) {
            applySpotted(target);
        } else {
            if (RandomWizard.chance(10)) //TODO depends on n of units?
                DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.ALERT, target);
        }

        return result;
    }

}
