package eidolons.game.battlecraft.logic.battlefield.vision.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionMaster;
import eidolons.game.exploration.dungeon.struct.Entrance;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_IMAGE;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.system.launch.Flags;

/**
 * Created by JustMe on 2/22/2017.
 */
public class OutlineMaster {


    private static final boolean outlinesOn = true;
    protected VisionMaster master;

    public OutlineMaster(VisionMaster visionMaster) {
        master = visionMaster;
    }

    public static boolean isOutlinesOn() {
        return outlinesOn;
    }

    public static boolean isAutoOutlinesOff() {
        return false;
    }

    public OUTLINE_TYPE getOutlineType(DC_Obj unit, Unit activeUnit) {
        if (Flags.isFootageMode()){
            return null;
        }
        if (unit.getGame().isSimulation()) {
            return null;
        }
        if (unit instanceof Entrance) {
            return null;
        }
        if (unit.isDetectedByPlayer()) {
            if (unit instanceof BattleFieldObject) {
                BattleFieldObject battleFieldObject = (BattleFieldObject) unit;
                if (battleFieldObject.isWall() || battleFieldObject.isLandscape()) {
                    return null;
                }
            }
        }
        return getOutline(unit, activeUnit);
    }

    public OUTLINE_TYPE getOutline(DC_Obj unit, Unit activeUnit) {
        int gamma = master.getGammaMaster().getGamma(activeUnit, unit);

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

    public OUTLINE_IMAGE getImageDark(BattleFieldObject obj) {
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
