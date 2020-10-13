package eidolons.game.battlecraft.logic.battlefield.vision;

import com.badlogic.gdx.graphics.Color;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMap;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxColorMaster;
import main.content.enums.GenericEnums;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.launch.CoreEngine;
import main.system.math.Formula;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static main.system.auxiliary.log.LogMaster.log;

public class Illumination {
    private static final boolean BASE_ILLUMINATION = true;
    private static boolean applied;
    private final Map<Obj, LightEmittingEffect> effectCache = new HashMap<>();
    private ColorMap.Light heroLight;
    private boolean resetRequired;
    private final Set<Obj> illuminatedObjs=new LinkedHashSet<>();

    public Illumination() {
    }

    public static boolean isConcealed(Unit source, BattleFieldObject object) {
        return false;
    }

    public void applyLightEmission() {
        if (applied) {
            log(1, "IlluminationRule already applied!");
            return;
        }
        illuminatedObjs.clear();
        Set<ColorMap.Light> set = new LinkedHashSet<>();
        for (Obj obj : DC_Game.game.getStructures()) {
            if (isOutsideBoundaries(obj))
                continue;
            LightEmittingEffect effect = getLightEmissionEffect((DC_Obj) obj);
            if (effect != null) {
                //                effect.setFormula(new Formula(getLightEmission((DC_Obj) obj) + ""));
                set.add(effect.createAndApplyLight());
            }
        }
        //Light revamp - HERO LIGHT
        if (isHeroLightOn()){
        Map<Coordinates, Float> lerp;
        if (heroLight == null) {
            heroLight = new ColorMap.Light(GdxColorMaster.PALE_GOLD, lerp = new ConcurrentHashMap<>(), GenericEnums.ALPHA_TEMPLATE.GRID_LIGHT);
        } else {
            lerp = heroLight.lerp;
        }
        DIRECTION direction = Eidolons.getMainHero().getFacing().getDirection();
        Coordinates coordinates = Eidolons.getPlayerCoordinates();
        for (DIRECTION d : DIRECTION.clockwise) {
            Coordinates c = coordinates.getAdjacentCoordinate(d);
            if (c == null) {
                continue;
            }
            Float value = lerp.get(c);
            float def = 0.55f;
            if (d == direction) {
                def = 0.88f;
            }
            if (value == null) {
                value = def;
            } else {
                value = def * 1 / (1 + c.dst(coordinates)); //TODO facing
            }
            lerp.put(c, value);
        }
        lerp.put(coordinates, 0.9f);
        set.add(heroLight);
        }
        DC_Game.game.getColorMap().setEmitters(set);
        applied = true;
    }

    private boolean isHeroLightOn() {
        return true;
    }

    private boolean isOutsideBoundaries(Obj obj) {
        if (CoreEngine.isLevelEditor()) {
            return false;
        }
        Coordinates c = obj.getCoordinates();
        //TODO DC Review
        return c.dst(Eidolons.getPlayerCoordinates()) > 10;
    }

    public void resetIllumination(boolean full) {
        // if (!DC_GameState.gridChanged) {
        //     if (!resetRequired)
        //     {
        //         log(1,"*** Skipped illumination reset!" );
        //         return;
        //     }
        // }
        // if (full) {
        // } else {
        //     log(1,"** Partial illumination reset!" );
        // }
        removeIllumination();
        applyLightEmission();
        resetRequired = false;
    }

    public void setResetRequired(boolean resetRequired) {
        this.resetRequired = resetRequired;
    }

    public void lightAdded(Obj targetObj) {
        illuminatedObjs.add(targetObj);
    }
    public void removeIllumination() {
        for (Obj obj : illuminatedObjs) {
            obj.setParam(PARAMS.ILLUMINATION, 0,true );
        }
        // DC_Game.game.getCells().forEach(cell -> {
        //     cell.setParam(PARAMS.ILLUMINATION, 0);
        // });
        // DC_Game.game.getBfObjects().forEach(unit -> {
        //     unit.setParam(PARAMS.ILLUMINATION, 0);
        // });

        applied = false;

        for (Obj obj : effectCache.keySet()) {
            if (!ExplorationMaster.isExplorationOn() || isSpectrumResetRequired(obj)) {
                effectCache.get(obj).resetCache();
            }

        }
    }

    private boolean isSpectrumResetRequired(Obj obj) {
        return obj.getCoordinates().dst_(Eidolons.getPlayerCoordinates()) < 6 && DC_GameState.gridChanged;
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
            Color color = GdxColorMaster.getColorForTheme(source.getColorTheme());

            GenericEnums.ALPHA_TEMPLATE flicker = GenericEnums.ALPHA_TEMPLATE.GRID_LIGHT;
            effect = new LightEmittingEffect(("" + value), circular, color, flicker, this);
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
        } else {
            if (source.isDead())
                return 0;
        }
        Integer mod = source.getGame().getVisionMaster().getIlluminationMaster().
                getLightEmissionModifier();
        if (mod != null)
            value = value * mod / 100;

        return value;
    }

}
