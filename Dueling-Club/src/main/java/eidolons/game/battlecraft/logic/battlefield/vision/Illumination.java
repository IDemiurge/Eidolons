package eidolons.game.battlecraft.logic.battlefield.vision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.ability.effects.common.LightEmittingEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMap;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
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

public class Illumination {
    private static final boolean BASE_ILLUMINATION = true;
    private static boolean applied;
    private final Map<Obj, LightEmittingEffect> effectCache = new HashMap<>();
    private ColorMap.Light heroLight;

    public Illumination() {
    }

    public static boolean isConcealed(Unit source, BattleFieldObject object) {
        return false;
    }

    public void applyLightEmission() {
        if (applied) {
            main.system.auxiliary.log.LogMaster.log(1, "IlluminationRule already applied!");
            return;
        }

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
        ObjectMap<Coordinates, Float> lerp = null;
        if (heroLight == null) {
            heroLight = new ColorMap.Light(GdxColorMaster.WHITE, lerp = new ObjectMap<>(), GenericEnums.ALPHA_TEMPLATE.GRID_LIGHT);
        } else {
            lerp = heroLight.lerp;
        }
        DIRECTION direction = Eidolons.getMainHero().getFacing().getDirection();
        Coordinates coordinates = Eidolons.getPlayerCoordinates();
        for (DIRECTION d : DIRECTION.clockwise) {
            Coordinates c = coordinates. getAdjacentCoordinate(d);
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

        DC_Game.game.getColorMap().setEmitters(set);
        applied = true;
    }

    private boolean isOutsideBoundaries(Obj obj) {
        if (CoreEngine.isLevelEditor()) {
            return false;
        }
        Coordinates c = obj.getCoordinates();
        // LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c);
        // LevelStruct struct2 = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(Eidolons.getPlayerCoordinates());

        // if (struct != struct2) {
        ////TODO Review !
        return c.dst(Eidolons.getPlayerCoordinates()) > 10;
        // }
        // return false;
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
            if (!ExplorationMaster.isExplorationOn() || isSpectrumResetRequired(obj)) {
                effectCache.get(obj).resetCache();
            }

        }
    }

    private boolean isSpectrumResetRequired(Obj obj) {
        return obj.getCoordinates().dst_(Eidolons.getPlayerCoordinates()) < 8;
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
            effect = new LightEmittingEffect(("" + value), circular, color, flicker);
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
