package main.game.battlecraft.logic.battlefield.vision;

import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.entity.obj.BfObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.system.math.PositionMaster;

/**
 * Created by JustMe on 2/22/2017.
 */
public class IlluminationMaster {

    public static final Integer DEFAULT_GLOBAL_ILLUMINATION = 10;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_NIGHT = 30;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_DAY = 80;
    private Integer globalIllumination = 0;
    private Integer globalConcealment = 10;

    public IlluminationMaster(VisionMaster visionManager) {

    }

    public Integer getGlobalIllumination() {
        return globalIllumination;
    }

    public void setGlobalIllumination(Integer globalIllumination) {
        this.globalIllumination = globalIllumination;
    }

    public Integer getGlobalConcealment() {
        return globalConcealment;
    }

    public void setGlobalConcealment(Integer globalConcealment) {
        this.globalConcealment = globalConcealment;
    }

    public Integer getIllumination(Unit source, DC_Obj target) {
        Dungeon dungeon = source.getGame().getDungeon();

        Integer illumination =
         target.getIntParam(PARAMS.ILLUMINATION);
        illumination += target.getIntParam(PARAMS.LIGHT_EMISSION) / 2;
        if (dungeon != null) {
            illumination = Math.max(illumination, dungeon.getGlobalIllumination());
            globalIllumination = dungeon.getGlobalIllumination() / 5;
        }
//universal
        illumination += globalIllumination;

        Integer ilMod = 100;
        int distance = PositionMaster.getDistance(source, target);
        // from 200 to 25 on diff of 8 to -5
        // def sight range of 5, I'd say
        Integer sight = source.getIntParam(PARAMS.SIGHT_RANGE);
        FACING_SINGLE singleFacing = FacingMaster.getSingleFacing(source, (BfObj) target);
        if (singleFacing == UnitEnums.FACING_SINGLE.BEHIND) {
            sight =1+ source.getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        } else if (singleFacing == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
            sight -= source.getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
        }
//        sight*=2; //TODO NEW
        int diff = sight - distance;

        if (diff < 0) {
            ilMod = 100 + (diff * 20
//             - diff * diff * 5
            );
        } else {
            ilMod = (100 + (int) (diff * 3 + Math.sqrt(diff * 50)));
        }

        ilMod = Math.min(ilMod, 200);
        ilMod = Math.max(ilMod, 1);

        // TODO DISTANCE FACTOR?
        return illumination * ilMod / 100;
    }

    public Integer getConcealment(Unit source, DC_Obj target) {
        Integer concealment = target.getIntParam(PARAMS.CONCEALMENT);
        concealment += source.getIntParam(PARAMS.CONCEALMENT) / 2; // getOrCreate from
        Dungeon dungeon = source.getGame().getDungeon();
        if (dungeon != null) {
            concealment += dungeon.getIntParam(PARAMS.GLOBAL_CONCEALMENT);
        }

        concealment += globalConcealment;

        Integer cMod = 100;

        if (source.checkPassive(UnitEnums.STANDARD_PASSIVES.DARKVISION)) { // maybe it
            cMod -= cMod * 50 / 100;
        }

        return concealment * cMod / 100;
    }
}
