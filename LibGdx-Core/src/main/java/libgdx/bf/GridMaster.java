package libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.module.cinematic.Cinematics;
import libgdx.GdxMaster;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import main.game.bf.Coordinates;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 1/29/2017.
 */
public class GridMaster {

    public static final int CELL_W = 128;
    public static final int CELL_H = 128;
    public static final String emptyCellPath = StrPathBuilder.build(
            "ui", "cells", "Empty Cell v3.png");
    public static final String emptyCellPathFloor = StrPathBuilder.build(
            "ui", "cells", "Floor.png");
    public static final String gridCornerElementPath = StrPathBuilder.build(
            "ui", "cells", "bf", "gridCorner.png");

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 = getCenteredPos(coordinates);
        Vector2 v2 = getCenteredPos(coordinates2);
        return v1.dst(v2);
    }

    public static Vector2 getCenteredPos(Coordinates sourceCoordinates) {
        return getVectorForCoordinate(sourceCoordinates, true, false);
    }


    public static Vector2 getVectorForCoordinate(Coordinates sourceCoordinates,
                                                 boolean center,
                                                 boolean camera
            , GridPanel gridPanel) {
        return getVectorForCoordinate(sourceCoordinates, center, camera, false, gridPanel);
    }

    public static Vector2 getVectorForCoordinate(Coordinates sourceCoordinates,
                                                 boolean center,
                                                 boolean camera) {
        return getVectorForCoordinate(sourceCoordinates, center, camera, false,
                ScreenMaster.getGrid());
    }

    public static float invertGdxY(float y) {
        return ScreenMaster.getGrid().getHeight() - y;
    }

    public static Coordinates invertGdxY(Coordinates c) {
        return new Coordinates(c.x, ScreenMaster.getGrid().getFullRows() - 1 - c.getY());
    }

    public static Vector2 getVectorForCoordinate(Coordinates sourceCoordinates,
                                                 boolean center,
                                                 boolean camera, boolean gdxY, GridPanel gridPanel) {

        //       TODO cache?
        //        InputController controller = DungeonScreen.getInstance().getController();
        float x = sourceCoordinates.getX() * CELL_W;
        float y = (gridPanel.getGdxY_ForModule((gdxY ? sourceCoordinates.getY() - 1 : sourceCoordinates.getY())) * CELL_H);

        if (camera) {
            //            x -= controller.getXCamPos();
            //            y -= controller.getYCamPos();
            x -= GdxMaster.getWidth() / 2;
            y -= GdxMaster.getHeight() / 2;
        }
        if (center) {
            x += CELL_W / 2;
            y += CELL_H / 2;
        } else {

        }
        return new Vector2(x, y);
    }

    public static Coordinates getCenter() {
        int x = Math.round((Coordinates.getFloorWidth() / 2));
        int y = Math.round((Coordinates.getFloorHeight() / 2));
        return (Coordinates.get(x, y));
    }

    public static Coordinates getCameraCenter() {
        int x = Math.round((ScreenMaster.getScreen().getController().getXCamPos()) / 128);
        int y = Math.round(invertGdxY(ScreenMaster.getScreen().getController().getYCamPos()) / 128);
        return (Coordinates.get(true, x, y));
    }

    public static Vector2 getMouseCoordinates() {
        return DungeonScreen.getInstance().getGridStage().screenToStageCoordinates(
                new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }

    public static void offset(Vector2 orig, Vector2 dest, int additionalDistance) {
        Vector2 v = new Vector2(dest.x - orig.x, dest.y - orig.y);

        // similar triangles solution!

        double hypotenuse = dest.dst(orig);

        double ratio = (hypotenuse + additionalDistance) / hypotenuse;
        float xDiff = (float) (ratio * v.x) - v.x;
        float yDiff = (float) (ratio * v.y) - v.y;
        v.add(xDiff, yDiff);
    }

    public static Vector2 getVectorForCoordinate(Coordinates coordinates) {
        return getVectorForCoordinate(coordinates, false, false);
    }

    public static boolean isHpBarsOnTop() {
        return true;
    }

    public static void validateVisibleUnitView(BaseView baseView) {
        if (Cinematics.ON)
            return;
        if (baseView instanceof UnitGridView) {
            UnitGridView view = ((UnitGridView) baseView);
            if (view.getActionsOfClass(AlphaAction.class).size == 0) {

                if (view.getUserObject() instanceof Unit)
                    if (view.getPortrait().getColor().a != 1)
                        if (view.getPortrait().getActions().size == 0) {
                            view.getPortrait().fadeIn();
                            main.system.auxiliary.log.LogMaster.warn("Validation was required for Portrait" + view +
                                    " - alpha==0");
                        }

                if (view.getColor().a == 0) {
                    main.system.auxiliary.log.LogMaster.warn("Validation was required for " + view +
                            " - alpha==0");
                    view.fadeIn();
                }
                if (view.getParent() instanceof GridCellContainer) {
                    GridCellContainer cell = ((GridCellContainer) view.getParent());
                    if (!cell.isStackView())
                        if (!cell.isWall())
                            if (view.getX() != cell.getViewX(view) ||
                                    view.getY() != cell.getViewY(view)) {
                                cell.recalcUnitViewBounds();
                            }

                } else {
                    //TODO detach bug
                    if (view.getParent()==null)
                        if (!view.getUserObject().isDead())
                            if (view.getX() <= 128)
                                if (view.getY() <= 128) {
                                    {
                                        GridPanel grid = ((GridPanel) view.getParent());
                                        grid.getCells()[view.getUserObject().getX()][(view.getUserObject().getY())].addActor(view);

                                        main.system.auxiliary.log.LogMaster.warn("Validation was required for " + view +
                                                " - re-attached to gridcell!");
                                    }
                                }
                }
            }
            if (view.getArrow() != null)
                if (view.getArrow().getActions().size == 0)
                    if (!AI_Manager.isRunning())
                     view.validateArrowRotation();

        }
    }

    private static String getCellImgSuffix(int cellVariant) {
        switch (cellVariant) {
            case 1:
                return "hl";
            case 2:
                return "lite";
            case 3:
                return "dark";
            case 4:
                return "rough";
        }
        return "";
    }

    public static Coordinates getClosestCoordinate(float x, float y) {
        GridPanel grid = ScreenMaster.getGrid();
        return
                Coordinates.getLimited((Math.round(x / CELL_W)),
                        grid.getGdxY_ForModule(Math.round(y / CELL_H)));
    }
}
