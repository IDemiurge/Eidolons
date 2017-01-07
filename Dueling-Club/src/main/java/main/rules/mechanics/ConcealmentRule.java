package main.rules.mechanics;

import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.CONTENT_CONSTS.VISION_MODE;
import main.content.PARAMS;
import main.entity.obj.BattlefieldObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.FacingMaster;
import main.game.logic.dungeon.Dungeon;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.Map;

public class ConcealmentRule {
    /*
	 * add buff with a passive dodge ability addPassive effect?
	 * trigger/continuous effect to be removed...
	 * 
	 * or maybe I could hard-code it somewhere almost like resistance?
	 * 
	 * Only for *ranged touch* actions, which could be a STD spell/action
	 * passive or tag or classif.
	 */

    private static Map<DC_Obj, Integer> cache = new HashMap<>();
    private static Integer GLOBAL_CONCEALMENT = 0;
    private static Integer GLOBAL_ILLUMINATION = 70;

    public static VISIBILITY_LEVEL getVisibilityLevel(DC_HeroObj source, DC_Obj target) {
        return getVisibilityLevel(source, target, null);
    }

    // public static String getOutlinePicture(VISIBILITY_LEVEL vl, DC_HeroObj
    // obj) {
    // }
    public static void clearCache() {
        cache.clear();
    }

    public static int getGamma(DC_HeroObj source, DC_Obj target, Boolean status) {
        return getGamma(false, source, target);
    }

    public static int getGamma(boolean minusForVagueLight, DC_HeroObj source, DC_Obj target) {
        Integer gamma = null;
        // = cache.getOrCreate(target);
        // if (gamma != null)
        // return gamma;
        if (source == null) {
            source = target.getGame().getManager().getActiveObj();
        }
        Dungeon dungeon = source.getGame().getDungeon();
        Integer illumination = target.getIntParam(PARAMS.ILLUMINATION);
        illumination += target.getIntParam(PARAMS.LIGHT_EMISSION) / 2;
        if (dungeon != null)
            illumination += dungeon.getGlobalIllumination();

        illumination += GLOBAL_ILLUMINATION;
        Integer concealment = target.getIntParam(PARAMS.CONCEALMENT);
        concealment += source.getIntParam(PARAMS.CONCEALMENT) / 2; // getOrCreate from
        // cell in
        // case?
        if (dungeon != null)
            concealment += dungeon.getIntParam(PARAMS.GLOBAL_CONCEALMENT);

        concealment += GLOBAL_CONCEALMENT;

        // main.system.auxiliary.LogMaster.log(0, "***" + target +
        // "'s illumination= " + illumination
        // + " ; concealment=" + concealment);
        Integer ilMod = 100;
        Integer cMod = 100;
        int distance = PositionMaster.getDistance(source, target);
        // from 200 to 25 on diff of 8 to -5
        // def sight range of 5, I'd say
        Integer sight = source.getIntParam(PARAMS.SIGHT_RANGE);
        FACING_SINGLE singleFacing = FacingMaster.getSingleFacing(source, (BattlefieldObj) target);
        if (singleFacing == FACING_SINGLE.BEHIND)
            sight = source.getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        else if (singleFacing == FACING_SINGLE.TO_THE_SIDE) {
            sight -= source.getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
        }
        // else if (singleFacing == FACING_SINGLE.BEHIND)
        // sight = 0; // ???
        int diff = distance - sight;

        if (diff > 0)
            ilMod = 100 - (diff * 10 + diff * diff * 5);
        else
            ilMod = (100 - (int) (diff * 5 + Math.sqrt(diff * 100)));

        ilMod = Math.min(ilMod, 200);
        ilMod = Math.max(ilMod, 25);
        // main.system.auxiliary.LogMaster.log(0, "**" + target + "'s ilMod= " +
        // ilMod + " ; diff="
        // + diff);

        // idea - accumulate C thru all the cells in the line of sight to obj!
        // source.getVisionMode() - alter rules! :)
        if (source.checkPassive(STANDARD_PASSIVES.DARKVISION)) { // maybe it
            // should
            // inverse
            // formula?
            // can be custom param instead of 50!
            cMod -= cMod * 50 / 100;
        }
        // if (status == null)
        // status = target.getUnitVisionStatus() ==
        // UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
        // if (status)
        // distance/sight range?
        // if (illumination < 75)
        // ilMod += ilMod * 25 / 100;

        // TODO DISTANCE FACTOR?

        int i = illumination * ilMod / 100;
        int c = concealment * cMod / 100;
        gamma = i - c;
        cache.put(target, gamma);

        // main.system.auxiliary.LogMaster.log(1, gamma + " gamma on " +
        // target.getNameAndCoordinate()
        // + " for " + source.getNameAndCoordinate() + "; ilMod = " + ilMod +
        // " cMod=" + cMod);

        if (source == target.getGame().getManager().getActiveObj())
            target.setGamma(gamma);

        if (i > 50 && c > 50)
            return Integer.MIN_VALUE;
        return gamma;
    }

    public static VISIBILITY_LEVEL getIdentificationLevel(DC_Obj target) {
        return null;

    }

    public static VISIBILITY_LEVEL getVisibilityLevel(DC_HeroObj source, DC_Obj target, Boolean status) {
        int value = getGamma(source, target, status);
        // if (!new ClearShotCondition(str1, str2).check(ref))
        // return VISIBILITY_LEVEL.CONCEALED;
        // detection
        // distance

        VISIBILITY_LEVEL vLevel = null;
        for (VISIBILITY_LEVEL vLvl : VISIBILITY_LEVEL.values()) {
            vLevel = vLvl;
            // if (value > vLvl.getIlluminationBarrier())
            // break;
        }
        return vLevel;
    }

    public static boolean checkMissed(DC_ActiveObj action) {
        DC_HeroObj source = action.getOwnerObj();
        Obj target = action.getRef().getTargetObj();
        if (source == null || target == null)
            return false;

        if (source.getVisionMode() == VISION_MODE.INFRARED_VISION) {

        }
        int chance = getMissChance(action);

        if (chance <= 0)
            return false;
        // add cell's concealment value, but not the unit's!

        return RandomWizard.chance(chance);
    }

    // DEPENDING ON VISIBILITY_LEVEL?
    public static int getMissChance(DC_ActiveObj action) {
        DC_Obj source = action.getOwnerObj();
        Obj target = action.getRef().getTargetObj();
        Obj cell = source.getGame().getCellByCoordinate(source.getCoordinates());
        // if (source.checkPassive(STANDARD_PASSIVES.DARKVISION))
        // return false;
        // if (source.checkPassive(STANDARD_PASSIVES.LIGHTVISION))
        // return false;
        int chance = target.getIntParam(PARAMS.CONCEALMENT) - source.getIntParam(PARAMS.DETECTION)
                - source.getIntParam(PARAMS.PERCEPTION) / 2 - target.getIntParam(PARAMS.NOISE) / 2
                - source.getIntParam(PARAMS.ACCURACY) - source.getIntParam(PARAMS.ILLUMINATION) // if
                // normal
                // vision...
                + cell.getIntParam(PARAMS.CONCEALMENT);
        if (chance < 0)
            chance = 0;

        chance -= source.getIntParam(PARAMS.ILLUMINATION);
        return Math.abs(chance);
    }

    public enum IDENTIFICATION_LEVEL {

        SHAPE, // size and 'shape' (monster, humanoid, strange shape, animal,
        // avian...)
        TYPE, // classifications
        GROUP, // lesser demon, ++ race
        UNIT, DETAILED, // special for rangers? All value pages available... Can
        // be a
        // spell too!
        ;
    }

    public enum VISIBILITY_LEVEL {
        // Distance based - Outline?
        CLEAR_SIGHT(), OUTLINE(), VAGUE_OUTLINE(), CONCEALED(), BLOCKED;
        // for info-panel, objComp... as an addition to UNIT_VISIBILITY?
        // what is the default? is this linked to Perception?
        // DETECTED vs KNOWN
        // IN_SIGHT vs BEYOND_SIGHT
        private int illuminationBarrier;

        VISIBILITY_LEVEL() {

        }
        // VISIBILITY_LEVEL(int illuminationBarrier) {
        // this.setIlluminationBarrier(illuminationBarrier);
        // }
        //
        // public int getIlluminationBarrier() {
        // return illuminationBarrier;
        // }
        //
        // public void setIlluminationBarrier(int illuminationBarrier) {
        // this.illuminationBarrier = illuminationBarrier;
        // }
    }

}
