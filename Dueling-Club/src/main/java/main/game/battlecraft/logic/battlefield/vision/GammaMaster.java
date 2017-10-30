package main.game.battlecraft.logic.battlefield.vision;

import main.content.PARAMS;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.mechanics.IlluminationRule;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.system.math.MathMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class GammaMaster {

    public static final boolean DEBUG_MODE = true;
    private static final Float CELL_GAMMA_MODIFIER = 0.01F;
    private static final Float UNIT_GAMMA_MODIFIER = 5F;
    private static final Float LIGHT_EMITTER_ALPHA_FACTOR = 0.02f;
    private static final Float CONCEALMENT_ALPHA_FACTOR = 0.02f;
    private VisionMaster master;
    private Map<DC_Obj, Integer> cache = new HashMap<>();
    private boolean dirty;

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
        return 500;
    }

    public static int getGammaForClearSight() {
        return 45;
    }

    public int getGamma(boolean minusForVagueLight, Unit source, DC_Obj target) {
        if (source == null) {
            source = target.getGame().getManager().getActiveObj();
        }
        if (!dirty)
            if (target.getGamma() != null)
                if (source == target.getGame().getManager().getActiveObj())
                    return target.getGamma();

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
        if (VisionManager.isVisionHacked()) {
            return 0;
        }
        Unit unit = Eidolons.game.getManager().getMainHero();
        if (unit == null) {
            unit = Eidolons.game.getManager().getActiveObj();
        }
        float alpha = 0;
        float gamma = getGammaForCell(x, y);
//        if (Eidolons.game.getCellByCoordinate(new Coordinates(x, y)).getVisibilityLevel() == VISIBILITY_LEVEL.BLOCKED) {
//            gamma = 0;
//        }

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
                for (Obj sub : IlluminationRule.getEffectCache().keySet()) {
                    if (sub instanceof Unit)
                        continue; //TODO illuminate some other way for units...
                    if (sub.getCoordinates().x==x)
                        if (sub.getCoordinates().y==y)
                            if (((DC_Obj)sub).getPlayerVisionStatus(false)==
                             UNIT_TO_PLAYER_VISION.DETECTED)
                        {
                            alpha += LIGHT_EMITTER_ALPHA_FACTOR * IlluminationRule
                             .getLightEmission((DC_Obj) sub);
                        }
                }

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
        dirty = true;
    }

    public float getGammaForCell(int x, int y) {
        DC_Cell cell = Eidolons.game.getCellByCoordinate(new Coordinates(x, y));
        if (cell==null ){
            return 0;
        }
        if ( cell.getGamma()==null ){
            return 0;
        }
        if (cell.getGame().isDebugMode())
        if ( cell.getGamma()==0 ){
//            if ( cell.getIntParam("illumination")!=0 )
            return getGamma(true,master.getSeeingUnit(), cell);
        }
//        Unit unit =  master.getSeeingUnit();
        return CELL_GAMMA_MODIFIER * (float)
        cell.getGamma();
//        return new Random().nextInt(50)/100 + 0.5f;
    }
}
