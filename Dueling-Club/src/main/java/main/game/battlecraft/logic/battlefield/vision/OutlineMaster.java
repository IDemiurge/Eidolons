package main.game.battlecraft.logic.battlefield.vision;

import main.ability.conditions.special.ClearShotCondition;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.core.DC_TurnManager;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.math.PositionMaster;
import main.test.debug.DebugMaster;

/**
 * Created by JustMe on 2/22/2017.
 */
public class OutlineMaster {


    protected VisionMaster master;
    private static boolean outlinesOn;

    public OutlineMaster(VisionMaster visionMaster) {
        master = visionMaster;
    }

    public static void setOutlinesOn(boolean outlinesOn) {
        OutlineMaster.outlinesOn = outlinesOn;
    }

    public static boolean isOutlinesOn() {
        return outlinesOn;
    }

    public OUTLINE_TYPE getOutlineType(DC_Obj unit) {
        if (unit.getGame().isDebugMode()) {
            if (unit.isMine()) {
                return null;
            }
        }
        Unit activeUnit = ExplorationMaster.isExplorationOn()
        ? master.getSeeingUnit() :
         DC_Game.game.getTurnManager().getActiveUnit(true);
        DC_TurnManager.setVisionInitialized(true);
        if (activeUnit == null) {
            return null;
        }
        if (unit == activeUnit) {
            return null;
        }
        return getOutlineType(unit, activeUnit  );
    }



    public OUTLINE_TYPE getOutlineType(DC_Obj unit, Unit activeUnit) {
        if (DebugMaster.isOmnivisionOn()) {
          if (activeUnit.isMine())  return null;
        }
        if (unit.getGame().isSimulation()) {
            return null;
        }
        if (unit.isDetectedByPlayer()) {
//            if (unit instanceof DC_Cell) {
//                return null;
//            }
            if (unit instanceof Unit) {
                Unit heroObj = (Unit) unit;
                if (heroObj.isWall() || heroObj.isLandscape()) {

                    return null;
                }
            }
        }
        Ref ref = new Ref(activeUnit);
        ref.setMatch(unit.getId());
        // [quick fix] must be the distance on which nothing is visible anyway...
        if (PositionMaster.getExactDistance(         unit.getCoordinates(), activeUnit.getCoordinates())<
       ClearShotCondition.getMaxCheckDistance(activeUnit, unit)        ) {
            //it is assumed that if a unit is farther away than that, it cannot have anything but Concealed status for activeUnit
            if (!new ClearShotCondition().preCheck(ref)) {
                // vision type preCheck - x.ray or so TODO
                if (activeUnit.isMine()) // TODO fix this !!! some other way to darken blocked stuff!
                    unit.setGamma(0);
                return OUTLINE_TYPE.BLOCKED_OUTLINE;
            } else
                unit.setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);
        } else {
            if (activeUnit.isMine()) //TODO fix this
                unit.setGamma(0);
            return OUTLINE_TYPE.THICK_DARKNESS;
        }
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
        if (gamma < master.getGammaMaster().getGammaForDarkOutline()  ) {
            return OUTLINE_TYPE.DARK_OUTLINE;
        }
        return null ;
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

    protected OUTLINE_IMAGE getImageDark(BattleFieldObject obj) {
        // TODO identify!

        if (!(obj instanceof Unit)) { //.isWall()
            return OUTLINE_IMAGE.WALL;
        }
        Unit unit = (Unit) obj;
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
