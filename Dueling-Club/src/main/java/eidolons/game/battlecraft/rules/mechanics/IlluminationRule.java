package eidolons.game.battlecraft.rules.mechanics;

import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.C_OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.math.Formula;

import java.util.HashMap;
import java.util.Map;

public class IlluminationRule {
    private static final boolean BASE_ILLUMINATION = true;
    private static boolean applied;
    private Map<Obj, LightEmittingEffect> effectCache = new HashMap<>();


    public IlluminationRule() {
    }

    public static boolean isConcealed(Unit source, BattleFieldObject object) {
        return false;
    }

    public void applyLightEmission() {
        if (applied) {
            main.system.auxiliary.log.LogMaster.log(1, "IlluminationRule already applied!");
            return;
        }

        for (Obj obj : DC_Game.game.getObjects(C_OBJ_TYPE.LIGHT_EMITTERS)) {
               if (isOutsideBoundaries(obj))
                    continue;
            LightEmittingEffect effect = getLightEmissionEffect((DC_Obj) obj);
            if (effect != null) {
                //                effect.setFormula(new Formula(getLightEmission((DC_Obj) obj) + ""));
                effect.apply();
            }
        }
        applied = true;
    }

    private boolean isOutsideBoundaries(Obj obj) {
        Coordinates c = obj.getCoordinates();
        DungeonLevel level =
         Eidolons.getGame().getMetaMaster().getDungeonMaster().getDungeonLevel();
        if (level != null) {
            if (level.getBlockForCoordinate(c) !=
             level.getBlockForCoordinate(
              Eidolons.getMainHero().getCoordinates())) {
                    if (c.dst(Eidolons.getMainHero().getCoordinates())>10)
                        return true;
            }

        }
        return false;
    }

    public void resetIllumination() {
        DC_Game.game.getCells().forEach(cell -> {
            cell.setParam(PARAMS.ILLUMINATION, 0);
        });
        DC_Game.game.getBfObjects().forEach(unit -> {
            unit.setParam(PARAMS.ILLUMINATION, 0);
        });
        applied = false;

        for (Obj obj : effectCache.keySet()) {
            if (!ExplorationMaster.isExplorationOn()|| isSpectrumResetRequired(obj)){
                effectCache.get(obj).resetCache();
            }

        }
    }

    private boolean isSpectrumResetRequired(Obj obj) {
        return obj.getCoordinates().dst_(Eidolons.getMainHero().getCoordinates())<8;
    }

    public Map<Obj, LightEmittingEffect> getEffectCache() {
        return effectCache;
    }

    public void clearCache() {
        effectCache.clear();
    }

    public LightEmittingEffect getLightEmissionEffect(DC_Obj source) {
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
        int value =
         source.getIntParam(PARAMS.LIGHT_EMISSION, BASE_ILLUMINATION);
        if (source instanceof Unit) {
            if (((Unit) source).isPlayerCharacter())
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
}
