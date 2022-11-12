package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.content.consts.VisualEnums.SHADE_CELL;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.GridCell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.core.Core;
import eidolons.game.exploration.dungeon.struct.Entrance;
import eidolons.game.exploration.story.quest.advanced.Quest;
import main.content.CONTENT_CONSTS;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.game.bf.Coordinates;
import main.system.math.MathMaster;

/**
 * Gamma is the value used to determine the alpha of SHADOW on each given cell
 */
public class GammaMaster {

    public static final boolean DEBUG_MODE = false;
    private static final int GAMMA_MAXIMUM = 200;
    private static final Float CELL_GAMMA_MODIFIER = 0.006F;
    private static final Float CONCEALMENT_ALPHA_FACTOR = 0.02f;
    private final VisionMaster master;
    private Coordinates mainExitCoordingates;

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

    public static void resetCaches(int w, int h) {
    }

    public int getGamma(Unit source, DC_Obj target) {
        if (target instanceof Entrance)
            return GAMMA_MAXIMUM;
        Integer gamma = target.getGamma(source);
        if (gamma != null)
            return gamma;
        return master.getIlluminationMaster().getVisibleIllumination(source, target)
                - master.getIlluminationMaster().getConcealment(source, target);
    }

    public float getAlphaForShadowMapCell(int x, int y, SHADE_CELL type) {
        if (type == SHADE_CELL.VOID) return getVoidAlpha(x, y);
        if (type == SHADE_CELL.BLACKOUT) return getBlackoutAlpha(x, y);
        if (type == SHADE_CELL.HIGHLIGHT) return getHiglightAlpha(x, y);

        Coordinates c = Core.getPlayerCoordinates();
        float alpha = 0;
        GridCell cell = Core.game.getCell(
                master.getGame().getGrid().getModuleCoordinates(x, y));
        float gamma = getGammaForCell(cell);

        switch (type) {

            case GAMMA_SHADOW:
                if (VisionHelper.isVisionHacked()) {
                    return 0;
                }
                //Light revamp - should we use colormap for Shadow?

                // Color color = master.game.getColorMap().getOutput().get(c);
                // gamma+=color.a;
                // gamma=gamma/2;
                if (gamma >= 1)
                    return 0;
                if (gamma <= 0)
                    alpha = 1;
                else {
                    alpha = 1 - gamma;
                    //                    alpha =(float) Math.min(Math.sqrt(alpha * 2), alpha / 3); // 1 - gamma;
                }
                if (c.getX() == x && c.getY() == y) {
                    alpha = alpha / 3;
                }
                if (cell.isPlayerHasSeen()) {
                    alpha = alpha * 2 / 3;
                }
                break;
            case GAMMA_LIGHT:
                alpha = getLightAlpha(gamma, cell, c);
                break;
            case CONCEALMENT:
                alpha =
                        master.getIlluminationMaster().getConcealment(Core.getMainHero(), cell) * CONCEALMENT_ALPHA_FACTOR;
                if (alpha > 0)
                    alpha += getAlphaForShadowMapCell(x, (y), SHADE_CELL.LIGHT_EMITTER) / 3;
                break;
        }

        return MathMaster.minMax(alpha, 0, 1);
    }

    protected float getLightAlpha(float gamma, GridCell cell, Coordinates observer) {
        if (gamma < 0)
            return 0;
        if (!(cell.getUnitVisionStatus() == UNIT_VISION.IN_PLAIN_SIGHT
                || cell.getUnitVisionStatus() == UNIT_VISION.IN_SIGHT)) {
            return 0;
        }
        float alpha = (float) Math.min(Math.sqrt(gamma), gamma / 4);
        alpha = Math.min(alpha, 0.5f);
        if (observer.getX() == cell.getX() && observer.getY() == cell.getY()) {
            alpha = alpha / 3 * 2;
        }
        return alpha;
    }


    private float getVoidAlpha(int x, int y) {
        if (master.getGame().getCell(Coordinates.get(x, y)).hasMark(CONTENT_CONSTS.MARK.undecorated)) {
            return 0;
        }
        //TODO Light revamp - those cobwebs need a new idea
        return 0.4f;
    }

    protected float getHiglightAlpha(int x, int y) {
        if (mainExitCoordingates == null) {
            if (master.getGame().getDungeonMaster().getFloorWrapper() instanceof Location) {
                Entrance exit = master.getGame().getDungeonMaster().
                        getFloorWrapper().getMainExit();
                if (exit == null) return 0;
                mainExitCoordingates = exit.getCoordinates();
            }
        }

        BattleFieldObject obj = master.getGame().getManager().getHighlightedObj();
        if (obj != null) {
            if (obj.getCoordinates().x == x)
                if (obj.getCoordinates().y == y)
                    return 1;
        }
        if (mainExitCoordingates != null) {
            if (mainExitCoordingates.getX() == x)
                if (mainExitCoordingates.getY() == y)
                    return 1;
        }

        //       TODO   questMaster .getQuestCoordinates()
        if (master.getGame().getMetaMaster().getQuestMaster() != null) {
            for (Quest quest : master.getGame().getMetaMaster().getQuestMaster().getRunningQuests()) {
                if (quest.isComplete()) {
                    continue;
                }
                if (quest.getCoordinate() != null && quest.getCoordinate().getX() == x && quest.getCoordinate().getY() == y)
                    return 1;
            }
        }

        return 0;
    }

    private float getBlackoutAlpha(int x, int y) {
        return 0;
    }


    public float getGammaForCell(int x, int y) {
        return getGammaForCell(Core.game.getCell(Coordinates.get(x, y)));
    }

    public float getGammaForCell(GridCell cell) {

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
        return getModifier() * (float)
                cell.getGamma();
        //        return new Random().nextInt(50)/100 + 0.5f;
    }

    private float getModifier() {
        return CELL_GAMMA_MODIFIER;
    }

    private boolean isBlockedGammaOn() {
        return true;
    }

    private float getBlockedGamma(GridCell cell) {
        if (cell.isPlayerHasSeen()) {
            return 0.25f;
        }
        return 0;
    }

}
