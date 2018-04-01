package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.GenericTurnManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.Ref;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.system.math.PositionMaster;
import eidolons.test.debug.DebugMaster;

/**
 * Created by JustMe on 2/22/2017.
 */
public class OutlineMaster {


    private static boolean outlinesOn = true;
    protected VisionMaster master;

    public OutlineMaster(VisionMaster visionMaster) {
        master = visionMaster;
    }

    public static boolean isOutlinesOn() {
//        if (outlinesOn==null )
//            outlinesOn = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OUTLINES);
        return outlinesOn;
    }

    public static void setOutlinesOn(boolean outlinesOn) {
        OutlineMaster.outlinesOn = outlinesOn;
    }

    public static boolean isAutoOutlinesOff() {
        return false;
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
        GenericTurnManager.setVisionInitialized(true);
        if (activeUnit == null) {
            return null;
        }
        if (unit == activeUnit) {
            return null;
        }
        return getOutlineType(unit, activeUnit);
    }

    public OUTLINE_TYPE getOutlineType(DC_Obj unit, Unit activeUnit) {
        if (DebugMaster.isOmnivisionOn()) {
            if (activeUnit.isMine()) return null;
        }
        if (unit.getGame().isSimulation()) {
            return null;
        }
        if (unit instanceof Entrance) {
            return null;
        }
        if (unit.isDetectedByPlayer()) {
//            if (unit instanceof DC_Cell) {
//                return null;
//            }
            if (unit instanceof BattleFieldObject) {
                BattleFieldObject battleFieldObject = (BattleFieldObject) unit;
                if (battleFieldObject.isWall() || battleFieldObject.isLandscape()) {
                    return null;
                }
            }
        }
        Ref ref = new Ref(activeUnit);
        ref.setMatch(unit.getId());
        // [quick fix] must be the distance on which nothing is visible anyway...
        if (PositionMaster.getExactDistance(unit.getCoordinates(), activeUnit.getCoordinates()) <
         ClearShotCondition.getMaxCheckDistance(activeUnit, unit)) {
            //it is assumed that if a unit is farther away than that, it cannot have anything but Concealed status for activeUnit
            if (!new ClearShotCondition().preCheck(ref)) {
                // vision type preCheck - x.ray or so TODO
                if (activeUnit.isMine()) // TODO fix this !!! some other way to darken blocked stuff!
                    unit.setGamma(0);
                return OUTLINE_TYPE.BLOCKED_OUTLINE;
            } else
                //TODO [visibility FIX]
                unit.setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);
        } else {
            if (activeUnit.isMine()) //TODO fix this
                unit.setGamma(0);
            return OUTLINE_TYPE.UNKNOWN;
        }
        int gamma = master.getGammaMaster().getGamma(true, activeUnit, unit);

        if (gamma == Integer.MIN_VALUE) {
            return OUTLINE_TYPE.VAGUE_LIGHT;
        } else if (gamma >= GammaMaster.getGammaForBlindingLight()) {
            if (!activeUnit.checkPassive(UnitEnums.STANDARD_PASSIVES.EYES_OF_LIGHT)) {
                return OUTLINE_TYPE.BLINDING_LIGHT;
            }
        }
        // TODO LIGHT_EMISSION !
        if (gamma <= GammaMaster.getGammaForThickDarkness()) {
            return OUTLINE_TYPE.DEEPER_DARKNESS;
        }
        if (gamma < GammaMaster.getGammaForDarkOutline()) {
            return OUTLINE_TYPE.DARK_OUTLINE;
        }
        return null;
    }

    protected OUTLINE_IMAGE getImageVague(Unit unit) {
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
