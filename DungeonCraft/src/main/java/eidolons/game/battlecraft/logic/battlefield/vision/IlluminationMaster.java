package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.content.PARAMS;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import eidolons.game.core.Core;
import eidolons.game.module.dungeoncrawl.struct.LevelStruct;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class IlluminationMaster {


    public static final float SIGHT_RANGE_FACTOR = 2.5f;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_NIGHT = 60;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_DAY = 90;
    public static final Integer DEFAULT_GLOBAL_ILLUMINATION_UNDERGROUND = 50;
    private final VisionMaster master;
    private final Integer globalIllumination = 0;
    private final Map<DC_Obj, Integer> cache = new HashMap<>();

    public IlluminationMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public Integer getLightEmissionModifier() {
        Floor floor = master.getGame().getDungeon();
        if (floor != null) {
            if (floor.getIntParam(PARAMS.LIGHT_EMISSION_MODIFIER) != 0)
                return floor.getIntParam(PARAMS.LIGHT_EMISSION_MODIFIER);
        }
        Integer lightEmissionModifier = 100;
        return lightEmissionModifier;
    }

    //Light revamp
    public Integer getVisibleIllumination(Unit source, DC_Obj target) {
        Integer illumination;
        if (source == master.getSeeingUnit()) {
            illumination = cache.get(target);
            if (illumination != null) {
                return illumination;
            }
        }
        if (
         target.getVisibilityLevel() == VISIBILITY_LEVEL.BLOCKED) {
            illumination = 0;
        } else
            illumination = target.getIntParam(PARAMS.ILLUMINATION);

        //emitters are bright enough already
        // illumination += target.getIntParam(PARAMS.LIGHT_EMISSION) / 2;

        LevelStruct struct = master.game.getDungeonMaster().getStructMaster().getLowestStruct(target.getCoordinates());

        Integer ambient = struct.getIlluminationValue();
        illumination += ambient;

        if (illumination <= 0)
        {
            if (target == Core.getMainHero()) {
                return 50;
            }
            return illumination;
        }
        double ilMod;

        double distance = PositionMaster.getExactDistance(source.getCoordinates(), target.getCoordinates());
        Integer sight = source.getSightRangeTowards(target);
        double diff = 1+sight*sight - distance*distance;

        if (diff < 0) {
            ilMod = 1  / -diff   ;
        } else {
            ilMod = (1 + (diff/ 2)); // + Math.sqrt(diff * 65)));
        }

        ilMod = Math.min(ilMod, 5);
        ilMod = Math.max(ilMod, 0.01f);

        // TODO DISTANCE FACTOR?
        illumination = (int) Math.round(illumination * ilMod  );
        if (target == Core.getMainHero()) {
            illumination*=2;
        }
        if (source == master.getSeeingUnit())
            cache.put(target, illumination);
        return illumination;
    }



    //TODO
    public Integer getConcealment(Unit source, DC_Obj target) {
        Integer concealment = target.getIntParam(PARAMS.CONCEALMENT);
        concealment += source.getIntParam(PARAMS.CONCEALMENT) / 2; // getOrCreate from
        Floor floor = source.getGame().getDungeon();
        if (floor != null) {
            concealment += floor.getIntParam(PARAMS.GLOBAL_CONCEALMENT);
        }

        Integer globalConcealment = 10;
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
