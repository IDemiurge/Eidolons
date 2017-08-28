package main.game.battlecraft.logic.battlefield.vision;

import main.content.PARAMS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.mechanics.IlluminationRule;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.system.math.MathMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 2/22/2017.
 */
public class GammaMaster {

    public static final boolean DEBUG_MODE = false;
    private static final Float CELL_GAMMA_MODIFIER = 0.01F;
    private static final Float UNIT_GAMMA_MODIFIER = 5F;
    private static final Float LIGHT_EMITTER_ALPHA_FACTOR = 0.01f;
    private static final Float CONCEALMENT_ALPHA_FACTOR = 0.02f;
    private VisionMaster master;
    private Map<DC_Obj, Integer> cache = new HashMap<>();

    public GammaMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public static int getGammaForThickDarkness() {
        return 15;
    }

    public static int getGammaForDarkOutline() {
        return 30;
    }

    public static int getGammaForBlindingLight() {
        return 300;
    }

    public static int getGammaForClearSight() {
        return 45;
    }

    public int getGamma(boolean minusForVagueLight, Unit source, DC_Obj target) {
        if (source == null) {
            source = target.getGame().getManager().getActiveObj();
        }
        Integer illumination = master.getIlluminationMaster().getIllumination(source, target);
        Integer concealment = master.getIlluminationMaster().getConcealment(source, target);


        Integer gamma = illumination - concealment;
//        cache.put(target, gamma);

        if (source == target.getGame().getManager().getActiveObj()) {
            target.setGamma(gamma);
        }
        if (target.getIntParam(PARAMS.LIGHT_EMISSION) > 0) {

        }
//        if (i > 50 && c > 50) {
//            return Integer.MIN_VALUE; TODO twilight
//        }
        return gamma;
    }

    public float getAlphaForShadowMapCell(int x, int y, SHADE_LIGHT type) {
if (VisionManager.isVisionHacked()){
    return 0;
}
        Unit unit = Eidolons.game.getManager().getMainHero();
        if (unit == null) {
            unit = Eidolons.game.getManager().getActiveObj();
        }
        float alpha = 0;
        float gamma = getGammaForCell(x, y);
        switch (type) {

            case GAMMA_SHADOW:
                if (gamma >= 1)
                    return 0;
                if (gamma < 0)
                    alpha = 1;
                else
                    alpha = 1 - gamma;
                break;
            case GAMMA_LIGHT:
                if (gamma < 2)
                    return 0;
                alpha = gamma - 2;
                break;
            case LIGHT_EMITTER:
                List<BattleFieldObject> list = Eidolons.game.getOverlayingObjects(
                 new Coordinates(x, y));
                list.addAll(Eidolons.game.getObjectsOnCoordinate(
                 new Coordinates(x, y)));
                list.removeIf(u -> IlluminationRule.getLightEmission(
                 u) == 0);
                if (!list.isEmpty())
                    alpha = Math.round(LIGHT_EMITTER_ALPHA_FACTOR *
                     list.stream().
                      collect(
                       Collectors.summingInt(obj ->
                        IlluminationRule.getLightEmission(
                         obj))));
                if (alpha > 0) {
                    break;
                }
                break;
            case CONCEALMENT:
                alpha =
//             Eidolons.game.getCellByCoordinate(
//              new Coordinates(x, y)).getIntParam(
//               PARAMS.CONCEALMENT)*CONCEALMENT_ALPHA_FACTOR;
                 master.getIlluminationMaster().getConcealment(unit, Eidolons.game.getCellByCoordinate(
                  new Coordinates(x, y))) * CONCEALMENT_ALPHA_FACTOR;
                if (alpha > 0)
                    alpha += getAlphaForShadowMapCell(x, y, SHADE_LIGHT.LIGHT_EMITTER) / 3;
                break;
        }

        return MathMaster.minMax(alpha, 0, 1);
    }


    public void clearCache() {
        cache.clear();
    }

    public float getGammaForCell(int x, int y) {
        Unit unit =  master.getSeeingUnit();
        return CELL_GAMMA_MODIFIER * (float)
         getGamma(false, unit,
          Eidolons.game.getCellByCoordinate(new Coordinates(x, y)));
//        return new Random().nextInt(50)/100 + 0.5f;
    }
}
