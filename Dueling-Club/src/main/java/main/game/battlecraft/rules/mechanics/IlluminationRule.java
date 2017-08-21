package main.game.battlecraft.rules.mechanics;

import main.ability.effects.Effect;
import main.ability.effects.common.LightEmittingEffect;
import main.content.C_OBJ_TYPE;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.game.core.game.DC_Game;
import main.system.math.Formula;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IlluminationRule {
    static Map<Obj, LightEmittingEffect> effectCache = new HashMap<>();

    public static void initLightEmission(DC_Game game) {
        List<Effect> effects = new LinkedList<>();
        for (Obj obj : game.getObjects(C_OBJ_TYPE.LIGHT_EMITTERS)) {
            LightEmittingEffect effect = getLightEmissionEffect(obj);
            if (effect != null) {
                effect.setFormula(new Formula(getLightEmission(obj) + ""));
                effects.add(effect);
            }
        }
        if (effects.isEmpty()) {
            return;
        }
        for (Effect effect : effects) {
            effect.apply();

        }
    }

    public static LightEmittingEffect getLightEmissionEffect(Obj source) {
        // if (!source.getVisionMode()==VISION_MODE.NORMAL_VISION)
        // whether to be added to the objects IN SIGHT; but there ought to be
        // other levels... e.g. detection from afar without revealing the real
        // obj-type... as with walls

        // Twilight Rule!
        if (source.getIntParam(PARAMS.LIGHT_EMISSION) <= 0) {
            return null;
        }
        int value = getLightEmission(source);
        if (value <= 0) {
            return null;
        }
        LightEmittingEffect effect = effectCache.get(source);
        // if (effect == null) {
        Boolean circular = true;
        if (source.checkBool(GenericEnums.STD_BOOLS.SPECTRUM_LIGHT)) {
            circular = false;
        } else if (EntityCheckMaster.isOverlaying(source)) {
            BattleFieldObject dc_Obj = (BattleFieldObject) source;
            if (dc_Obj.getDirection() != null) {
                circular = false;
            }
        }
        effect = new LightEmittingEffect(("" + value), circular);
        effect.setRef(new Ref(source));
        effectCache.put(source, effect);
        // } else
        // effect.getEffect().getEffectsStage().getOrCreate(0).setFormula(new Formula("" +
        // value));
        return effect;

    }

    public static int getLightEmission(Obj source) {
//        Integer concealment = source.getIntParam(PARAMS.CONCEALMENT)
//                + source.getGame().getCellByCoordinate(source.getCoordinates()).getIntParam(
//                PARAMS.CONCEALMENT);
        int value = source.getIntParam(PARAMS.LIGHT_EMISSION)  ;
        return value;
    }

}
