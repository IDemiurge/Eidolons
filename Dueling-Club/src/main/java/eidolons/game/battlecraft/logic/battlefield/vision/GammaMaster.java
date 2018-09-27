package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class GammaMaster {

    public static final boolean DEBUG_MODE = true;
    private static final Float CELL_GAMMA_MODIFIER = 0.004F;
    private static final Float UNIT_GAMMA_MODIFIER = 5F;
    private static final Float LIGHT_EMITTER_ALPHA_FACTOR = 0.02f;
    private static final Float CONCEALMENT_ALPHA_FACTOR = 0.02f;
    private static Float[][] voidAlphaCache;
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

    public static void resetCaches() {
        voidAlphaCache = new Float[GuiManager.getBattleFieldWidth()][GuiManager.getBattleFieldHeight()];
    }

    public int getGamma(Unit source, DC_Obj target) {
        if (target instanceof Entrance)
            return 200;
        Integer gamma = target.getGamma(source);
        if (gamma != null)
            return gamma;
        return master.getIlluminationMaster().getIllumination(source, target)
                - master.getIlluminationMaster().getConcealment(source, target);
    }

    public float getAlphaForShadowMapCell(int x, int y, SHADE_CELL type) {
        if (type == SHADE_CELL.VOID)
            return getVoidAlpha(x, y);
        if (type == SHADE_CELL.BLACKOUT) {
            return getBlackoutAlpha(x, y);
        }
        if (type == SHADE_CELL.HIGLIGHT) {
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
        DC_Cell cell = Eidolons.game.getCellByCoordinate(
                Coordinates.get(x, y));
        if (cell == null) {
            if (type == SHADE_CELL.GAMMA_SHADOW)
                return 1;
            return 0;
        }
        float gamma = getGammaForCell(cell);
        //        if (Eidolons.game.getCellByCoordinate(Coordinates.get(x, y)).getVisibilityLevel() == VISIBILITY_LEVEL.BLOCKED) {
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
                else {
                    alpha = 1 - gamma;
                    //                    alpha =(float) Math.min(Math.sqrt(alpha * 2), alpha / 3); // 1 - gamma;
                }
                if (unit.getX() == x && unit.getY() == y) {
                    alpha = alpha / 3;
                }
                if (cell.isPlayerHasSeen()) {
                    alpha = alpha * 2 / 3;
                }
                break;
            case GAMMA_LIGHT:
                if (gamma < 0)
                    return 0;
                if (!(cell.getUnitVisionStatus() == UNIT_VISION.IN_PLAIN_SIGHT || cell.getUnitVisionStatus() == UNIT_VISION.IN_SIGHT)) {
                    return 0;
                }
                alpha = (float) Math.min(Math.sqrt(gamma), gamma / 4);
                alpha = Math.min(alpha, 0.5f);
                if (unit.getX() == x && unit.getY() == y) {
                    alpha = alpha / 3 * 2;
                }
                break;
            case LIGHT_EMITTER:
                alpha = getLightEmitterAlpha(x, y);
                break;
            case CONCEALMENT:
                alpha =
                        //             Eidolons.game.getCellByCoordinate(
                        //              Coordinates.get(x, y)).getIntParam(
                        //               PARAMS.CONCEALMENT)*CONCEALMENT_ALPHA_FACTOR;
                        master.getIlluminationMaster().getConcealment(unit, cell) * CONCEALMENT_ALPHA_FACTOR;
                if (alpha > 0)
                    alpha += getAlphaForShadowMapCell(x, PositionMaster.getLogicalY(y), SHADE_CELL.LIGHT_EMITTER) / 3;
                break;
        }

        return MathMaster.minMax(alpha, 0, 1);
    }

    public float getLightEmitterAlpha(int x, int y) {
        return getLightEmitterAlpha(x, y, false);
    }
        public float getLightEmitterAlpha(int x, int y, boolean unit) {
        float alpha = 0;

        for (Obj sub : DC_Game.game.getRules().getIlluminationRule().getEffectCache().keySet()) {
            if ((sub instanceof Unit)!=unit) {
                continue; //TODO illuminate some other way for units...
            }
            if (sub.getCoordinates().x == x)
                if (sub.getCoordinates().y == y) {
                    float value =getLightEmitterAlpha(sub, alpha);
                        alpha += value;
                        //                        log(1, x + " " + y + " has " + alpha + " light alpha with " + sub);
                }
        }

        return alpha;
    }

    public float getLightEmitterAlpha(Obj sub, float alpha) {
        UNIT_VISION visionStatus = ((DC_Obj) sub).getUnitVisionStatus(Eidolons.getMainHero());

        if (Eidolons.getMainHero()!=sub){
            if (visionStatus == UNIT_VISION.BLOCKED)
                return 0;
        }

        float dst = 1 + (float) Coordinates.get(
               sub.getX(), sub.getY()).dst_(Eidolons.getMainHero().getCoordinates());
        if (dst > Eidolons.getMainHero().
                getMaxVisionDistanceTowards(sub.getCoordinates()))
            return 0;

        float value = (float) (LIGHT_EMITTER_ALPHA_FACTOR / Math.sqrt(dst) *
                        master.getGame().getRules().getIlluminationRule()
                                .getLightEmission((DC_Obj) sub));

        boolean overlaying = ((BattleFieldObject) sub).isOverlaying();

        if (visionStatus == UNIT_VISION.IN_PLAIN_SIGHT) {
            value *= 1.5f;
            if (alpha == 0)
                if (overlaying)
                    value *= 1.3f;
        } else if (visionStatus == UNIT_VISION.IN_SIGHT) {
            value *= 1.25f;
            if (alpha == 0)
                if (overlaying)
                    value *= 1.5f;
        } else if (alpha == 0)
            if (overlaying)
                value *= 1.8f;

        return value;
    }


    private float getVoidAlpha(int x, int y) {
        Float alpha = voidAlphaCache[x][y];
        if (alpha != null)
            return alpha;
        alpha = 1f;
        Coordinates c = Coordinates.get(x, y);
        DC_Cell cell = null;
        //no guarantee, but high chance to find one of the closest non void cells
        loop:
        for (int i = 1; i < master.getGame().getDungeon().getWidth(); i++) {
            for (int j = 0; j < master.getGame().getDungeon().getHeight(); j++) {
                cell = master.getGame().getGrid().getCell(x + i, y + i);
                if (cell != null) {
                    break loop;
                }
                cell = master.getGame().getGrid().getCell(x - i, y + i);
                if (cell != null) {
                    break loop;
                }
                cell = master.getGame().getGrid().getCell(x + i, y - i);
                if (cell != null) {
                    break loop;
                }
                cell = master.getGame().getGrid().getCell(x - i, y - i);
                if (cell != null) {
                    break loop;
                }
            }
        }
        float dst = (float) c.dst_(cell.getCoordinates());
//         (float) master.getGame().getCells().stream().sorted(new SortMaster<Obj>().getSorterByExpression_(
//          cell -> (int) (-1000 * cell.getCoordinates().dst_(Coordinates.get(x, y))
//          ))).findFirst().get().getCoordinates().dst_(c); too performance heavy!
        dst = (float) Math.sqrt(dst * 2);
        alpha = alpha / (Math.max(1, dst));
        voidAlphaCache[x][y] = alpha;
        return alpha;
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
        return getGammaForCell(Eidolons.game.getCellByCoordinate(Coordinates.get(x, y)));
    }

    public float getGammaForCell(DC_Cell cell) {

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
        if (isBlockedGammaOn())
            if (cell.getUnitVisionStatus() == UNIT_VISION.BLOCKED)
                return getBlockedGamma(cell);


        //        Unit unit =  master.getSeeingUnit();
        return CELL_GAMMA_MODIFIER * (float)
                cell.getGamma();
        //        return new Random().nextInt(50)/100 + 0.5f;
    }

    private boolean isBlockedGammaOn() {
        return true;
    }

    private float getBlockedGamma(DC_Cell cell) {
        if (cell.isPlayerHasSeen()) {
            return 0.25f;
        }
        return 0;
    }

}
