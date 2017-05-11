package main.game.battlecraft.logic.battlefield.vision;

import main.ability.conditions.special.ClearShotCondition;
import main.content.PARAMS;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.core.DC_TurnManager;
import main.game.core.game.DC_Game;
import main.game.core.DC_TurnManager;
import main.system.math.PositionMaster;
import main.test.debug.DebugMaster;

/**
 * Created by JustMe on 2/22/2017.
 */
public class OutlineMaster {


    protected VisionMaster master;

    public OutlineMaster(VisionMaster visionMaster) {
        master = visionMaster;
    }

    public OUTLINE_TYPE getOutlineType(DC_Obj unit) {
        if (unit.getGame().isDebugMode()) {
            if (unit.isMine()) {
                return null;
            }
        }
        Unit activeUnit = DC_Game.game.getTurnManager().getActiveUnit(true);
        DC_TurnManager.setVisionInitialized(true);
        if (activeUnit == null) {
            return null;
        }
        if (unit == activeUnit) {
            return null;
        }
        return getOutlineType(activeUnit, unit);
    }

    public OUTLINE_TYPE getOutlineType(Unit activeUnit, DC_Obj unit) {
        // if (unit.getVisibilityLevel() == VISIBILITY_LEVEL.CLEAR_SIGHT)
        // return null;
        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return null;
        return getOutlineType(unit, activeUnit);
    }

    public OUTLINE_TYPE getOutlineType(DC_Obj unit, Unit activeUnit) {
        if (DebugMaster.isOmnivisionOn()) {
            return null;
        }
        if (unit.getGame().isSimulation()) {
            return null;
        }
        if (unit.isDetectedByPlayer()) {
            if (unit instanceof DC_Cell) {
                return null;
            }
            if (unit instanceof Unit) {
                Unit heroObj = (Unit) unit;
                if (heroObj.isWall() || heroObj.isLandscape()) {

                    return null;
                }
            }
        }
        Ref ref = new Ref(activeUnit);
        ref.setMatch(unit.getId());
        // [quick fix]

        int gamma = master.getGammaMaster().getGamma(true, activeUnit, unit);
        if (gamma == Integer.MIN_VALUE) {
            return OUTLINE_TYPE.VAGUE_LIGHT;
        } else if (gamma >= master.getGammaMaster().getGammaForBlindingLight()) {
            if (!activeUnit.checkPassive(UnitEnums.STANDARD_PASSIVES.EYES_OF_LIGHT)) {
                return OUTLINE_TYPE.BLINDING_LIGHT;
            }
        }
        // TODO LIGHT_EMISSION !
        if (gamma <= master.getGammaMaster().getGammaForThickDarkness()) {

            return OUTLINE_TYPE.THICK_DARKNESS;
        }
        // LIT_HAZE ?

        // if (unit instanceof DC_Cell)
        // return null;

        // int effectiveVisibility = (int) (gamma / Math.max(1, 2 *
        // Math.sqrt(diff)));
        // first preCheck if there is enough for either... then preCheck which is
        // relatively greater! Or "Dark Vague Outline?" :)
        if (unit instanceof DC_Cell) {
            if (gamma > 50) {

                // [quick fix]
                if (!new ClearShotCondition().preCheck(ref)) {
                    // vision type preCheck - x.ray or so TODO
                    return OUTLINE_TYPE.BLOCKED_OUTLINE;
                }
                return null;
            }
        }
        if (gamma > 50) {// ++ dark vision!
            // flat/blocked?

            // [quick fix]
            if (!new ClearShotCondition().preCheck(ref)) {
                // vision type preCheck - x.ray or so TODO
                return OUTLINE_TYPE.BLOCKED_OUTLINE;
            }
            return null;
        }
        int distance = PositionMaster.getDistance(activeUnit, unit);
        int diff = distance - activeUnit.getIntParam(PARAMS.SIGHT_RANGE);
        // if adjacent, gamma must be
        if (gamma < 40 - diff * 10) {
            return OUTLINE_TYPE.DARK_OUTLINE;
        }
        return OUTLINE_TYPE.VAGUE_OUTLINE;
    }

    protected OUTLINE_IMAGE getImageVague(Unit unit) {
        // if (unit.isHuge()) {
        //
        // }
        // if (unit.isShort()) {
        //
        // }
        // if (unit.isSmall()) {
        //
        // }
        // if (unit.isTall()) {
        //
        // }
        if (unit.isWall()) {
            return OUTLINE_IMAGE.WALL;
        }
        return OUTLINE_IMAGE.UNKNOWN;
    }

    protected OUTLINE_IMAGE getImageDark(Unit unit) {
        // TODO identify!
        if (unit.isWall()) {
            return OUTLINE_IMAGE.WALL;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.ANIMAL)) {
            return OUTLINE_IMAGE.BEAST;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.HUMANOID)) {
            if (unit.getRace() == HeroEnums.RACE.HUMAN || unit.isHero()) {
                return OUTLINE_IMAGE.HUMAN;
            } else if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.MONSTER)) {
                return OUTLINE_IMAGE.MONSTROUS_HUMANOID;
            } else {
                return OUTLINE_IMAGE.HUMANLIKE;
            }
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.INSECT)) {
            return OUTLINE_IMAGE.INSECT;
        }

        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.DEMON)) {
            return OUTLINE_IMAGE.HORROR;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.UNDEAD)) {
            return OUTLINE_IMAGE.HORROR;
        }

        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.MONSTER)) {
            return OUTLINE_IMAGE.MONSTROUS;
        }

        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.ANIMAL)) {
            return OUTLINE_IMAGE.MULTIPLE;
        }
        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.ANIMAL)) {
            return OUTLINE_IMAGE.INSECT;
        }

        return OUTLINE_IMAGE.UNKNOWN;
    }

}
