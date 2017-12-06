package main.game.battlecraft.rules.mechanics;

import main.ability.effects.common.LightEmittingEffect;
import main.content.C_OBJ_TYPE;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.math.Formula;

import java.util.HashMap;
import java.util.Map;

public class IlluminationRule {
    private static final boolean BASE_ILLUMINATION =true ;
    static Map<Obj, LightEmittingEffect> effectCache = new HashMap<>();
    private final DC_Game game;


    public IlluminationRule(DC_Game game) {
        this.game = game;
    }

    public   void resetIllumination( ) {
        game.getCells().forEach(cell -> {
            cell.setParam(PARAMS.ILLUMINATION, 0);
        });
        game.getBfObjects().forEach(unit -> {
            unit.setParam(PARAMS.ILLUMINATION, 0);
        });
    }

    public   Map<Obj, LightEmittingEffect> getEffectCache() {
        return effectCache;
    }

    public   void initLightEmission( ) {
//        List<Effect> effects = new ArrayList<>();
        for (Obj obj : game.getObjects(C_OBJ_TYPE.LIGHT_EMITTERS)) {
            LightEmittingEffect effect = getLightEmissionEffect((DC_Obj) obj);
            if (effect != null) {
//                effect.setFormula(new Formula(getLightEmission((DC_Obj) obj) + ""));
                effect.apply();
            }
        }
//        if (effects.isEmpty()) {
//            return;
//        }
//        for (Effect effect : effects) {
//            effect.apply();
//
//        }
    }

    public static LightEmittingEffect getLightEmissionEffect(DC_Obj source) {
        // if (!source.getVisionMode()==VISION_MODE.NORMAL_VISION)
        // whether to be added to the objects IN SIGHT; but there ought to be
        // other levels... e.g. detection from afar without revealing the real
        // obj-type... as with walls

        // Twilight Rule!

        LightEmittingEffect effect = effectCache.get(source);
        if (effect == null) {
            int value = getLightEmission(source);
            if (value <= 0) {
                return null;
            }
            Boolean circular = true;
            if (source instanceof Unit)
                circular = false;
            else if (source.checkBool(GenericEnums.STD_BOOLS.SPECTRUM_LIGHT)) {
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
        } else
            effect.getEffects().setFormula(new Formula("" +
             getLightEmission(source)));
        return effect;

    }

    public static int getLightEmission(DC_Obj source) {

//        Integer concealment = source.getIntParam(PARAMS.CONCEALMENT)
//                + source.getGame().getCellByCoordinate(source.getCoordinates()).getIntParam(
//                PARAMS.CONCEALMENT);
        int value =
//         source.getGame().getVisionMaster().
//          getIlluminationMaster().getIllumination(source);
         source.getIntParam(PARAMS.LIGHT_EMISSION,  BASE_ILLUMINATION );
        if (source instanceof Unit) {
            if (((Unit) source).isHero())
//                if (source.getGame().getVisionMaster().
//                 getIlluminationMaster().getIllumination(source) < 50)
                    value += 40;
        }
        Integer mod = source.getGame().getVisionMaster().getIlluminationMaster().
         getLightEmissionModifier();
        if (mod != null)
            value = value * mod / 100;

        return value;
    }

    public void clearCache() {
        effectCache.clear();
    }
}
