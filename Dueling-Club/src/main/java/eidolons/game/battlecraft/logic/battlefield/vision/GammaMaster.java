package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
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
        return 10;
    }

    public static int getGammaForDarkOutline() {
        return 25;
    }

    public static int getGammaForBlindingLight() {
        return 500;
    }

    public static int getGammaForClearSight() {
        return 45;
    }

    public int getGamma(Unit source, DC_Obj target) {
        if (target instanceof Entrance)
            return 200;
        Integer gamma = target.getGamma(source);
        if (gamma !=null )
            return gamma;
        return master.getIlluminationMaster().getIllumination(source, target)
         -master.getIlluminationMaster().getConcealment(source, target);
    }

    public float getAlphaForShadowMapCell(int x, int y, SHADE_LIGHT type) {

        if (type == SHADE_LIGHT.BLACKOUT) {
            return getBlackoutAlpha(x, y);
        }
        if (type == SHADE_LIGHT.HIGLIGHT) {
            return getHiglightAlpha(x, y);
        }
        Unit unit = Eidolons.game.getManager().getMainHero();
        if (unit == null) {
            unit = Eidolons.game.getManager().getActiveObj();
        }
        if (unit == null) {
            switch (type) {
                case GAMMA_SHADOW:
                    return 1;
            }
            return 0;
        }
        float alpha = 0;
        float gamma = getGammaForCell(x, y);
//        if (Eidolons.game.getCellByCoordinate(new Coordinates(x, y)).getVisibilityLevel() == VISIBILITY_LEVEL.BLOCKED) {
//            gamma = 0;
//        }

        switch (type) {

            case GAMMA_SHADOW:
                if (VisionManager.isVisionHacked()) {
                    return 0;
                }
                if (gamma >= 1)
                    return 0;
                if (gamma < 0)
                    alpha = 1;
                else
                    alpha = 1 - gamma;
                if (unit.getX()==x && unit.getY()==y){
                    alpha = alpha/2;
                }
                break;
            case GAMMA_LIGHT:
                if (gamma < 0)
                    return 0;
                alpha = (float) Math.min(Math.sqrt(gamma * 2), gamma / 3);
                alpha = Math.min(alpha, 0.5f);
                break;
            case LIGHT_EMITTER:
                for (Obj sub : DC_Game.game.getRules().getIlluminationRule().getEffectCache().keySet()) {
                    if (sub instanceof Unit)
                        continue; //TODO illuminate some other way for units...
                    if (sub.getCoordinates().x == x)
                        if (sub.getCoordinates().y == y)
                            if (((DC_Obj) sub).getPlayerVisionStatus(false) ==
                             PLAYER_VISION.DETECTED) {
                                alpha += LIGHT_EMITTER_ALPHA_FACTOR *
                                 master.getGame().getRules().getIlluminationRule()
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

    private float getHiglightAlpha(int x, int y) {
        //tutorial info
        if (master.getGame().getDungeonMaster().getDungeonWrapper() instanceof Location) {
            Entrance exit = ((Location) master.getGame().getDungeonMaster().
             getDungeonWrapper()).getMainExit();
            if (exit != null) {
                if (exit.getX() == x)
                    if (exit.getY() == y)
                        return 1;
            }
        }
        return 0;
    }

    private float getBlackoutAlpha(int x, int y) {
        return 0;
    }



    public float getGammaForCell(int x, int y) {
        DC_Cell cell = Eidolons.game.getCellByCoordinate(new Coordinates(x, y));
        if (cell == null) {
            return 0;
        }
        if (cell.getGamma() == null) {
            return 0;
        }
        if (cell.getGame().isDebugMode())
            if (cell.getGamma() == 0) {
//            if ( cell.getIntParam("illumination")!=0 )
                try {
                    return getGamma(master.getSeeingUnit(), cell);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }

            }
        if (cell.getUnitVisionStatus() == UNIT_VISION.BLOCKED)
            return getBlockedGamma(cell);


//        Unit unit =  master.getSeeingUnit();
        return CELL_GAMMA_MODIFIER * (float)
         cell.getGamma();
//        return new Random().nextInt(50)/100 + 0.5f;
    }

    private float getBlockedGamma(DC_Cell cell) {
        if (cell.isPlayerHasSeen()) {
            return 0.25f;
        }
        return 0;
    }

}
