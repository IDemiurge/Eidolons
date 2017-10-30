package main.game.battlecraft.logic.battlefield.vision;

import main.ability.conditions.special.ClearShotCondition;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.Ref;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class IlluminationMaster {

    public static final Integer DEFAULT_GLOBAL_ILLUMINATION = 10;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_NIGHT = 30;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_DAY = 80;
    private VisionMaster master;
    private Integer lightEmissionModifier = 100;
    private Integer globalIllumination = 0;
    private Integer globalConcealment = 10;
    private Map<DC_Obj, Integer> cache = new HashMap<>();

    public IlluminationMaster(VisionMaster visionManager) {
        master = visionManager;

    }

    public Integer getLightEmissionModifier() {
        Dungeon dungeon = master.getGame().getDungeon();
        if (dungeon != null) {
            if (dungeon.getIntParam(PARAMS.LIGHT_EMISSION_MODIFIER) != 0)
                return dungeon.getIntParam(PARAMS.LIGHT_EMISSION_MODIFIER);
        }
        return lightEmissionModifier;
    }

    public void setLightEmissionModifier(Integer lightEmissionModifier) {
        this.lightEmissionModifier = lightEmissionModifier;
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

    public Integer getIllumination(DC_Obj target) {
        return getIllumination(master.getSeeingUnit(), target);
    }

    public Integer getIllumination(Unit source, DC_Obj target) {
        Integer illumination = 0;
        if (source == master.getSeeingUnit()) {
            illumination = cache.get(target);
            if (illumination != null) {
                return illumination;
            }
        }
        Ref ref = new Ref(source);
        ref.setMatch(target.getId());
        if (
         target.getVisibilityLevel() == VISIBILITY_LEVEL.BLOCKED) {
            //master.getSightMaster().getClearShotCondition().preCheck(ref)
//            cache.put(target, -1);
//            return -1;
            illumination = 0;
        } else
            illumination = target.getIntParam(PARAMS.ILLUMINATION);
        Dungeon dungeon = source.getGame().getDungeon();
        illumination += target.getIntParam(PARAMS.LIGHT_EMISSION) / 2;
        if (dungeon != null) {
            illumination = Math.max(illumination, dungeon.getGlobalIllumination());
            globalIllumination = dungeon.getGlobalIllumination() / 5;
        }
//universal
        illumination += globalIllumination;
        if (illumination <= 0)
            return illumination;
        double ilMod;
        double distance = PositionMaster.getExactDistance(source.getCoordinates(), target.getCoordinates());
        // from 200 to 25 on diff of 8 to -5
        // def sight range of 5, I'd say
        Integer sight = source.getSightRangeTowards(target);
//        sight*=2; //TODO NEW
        double diff = sight - distance;

        if (diff < 0) {
            ilMod = 100 + diff * 100 / ClearShotCondition.SIGHT_RANGE_FACTOR;
//             - diff * diff * 2
        } else {
            ilMod = (100 + (diff * 12)); // + Math.sqrt(diff * 65)));
        }

        ilMod = Math.min(ilMod, 300);
        ilMod = Math.max(ilMod, 1);

        // TODO DISTANCE FACTOR?
        illumination = (int) Math.round(illumination * ilMod / 100);
        cache.put(target, illumination);
        return illumination;
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

    public void clearCache() {
        cache.clear();
    }
}
