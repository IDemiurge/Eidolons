package eidolons.libgdx.bf;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.gui.CursorPosVector2;
import eidolons.libgdx.screens.DungeonScreen;
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
            "ui", "cells","bf", "gridCorner.png");

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 = getCenteredPos(coordinates);
        Vector2 v2 = getCenteredPos(coordinates2);
        return v1.dst(v2);
    }

    public static Vector2 getPosWithOffset(Coordinates sourceCoordinates) {
        return getVectorForCoordinate(sourceCoordinates, false, true);
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
                DungeonScreen.getInstance().getGridPanel());
    }

    public static Coordinates invertGdxY(Coordinates c) {
        return new Coordinates(c.x,  DungeonScreen.getInstance().getGridPanel().getRows()-1-c.getY());
    }
    public static Vector2 getVectorForCoordinate(Coordinates sourceCoordinates,
                                                 boolean center,
                                                 boolean camera, boolean gdxY, GridPanel gridPanel) {

//        InputController controller = DungeonScreen.getInstance().getController();
        float x = sourceCoordinates.getX() * CELL_W;
        float y = (gridPanel.getRows()
                - (gdxY ? sourceCoordinates.getY() + 1 : sourceCoordinates.getY())) * CELL_H;

        if (camera) {
//            x -= controller.getXCamPos();
//            y -= controller.getYCamPos();
            x -= GdxMaster.getWidth() / 2;
            y -= GdxMaster.getHeight() / 2;
        }
        if (center) {
            x += CELL_W / 2;
            y -= CELL_H / 2;
        } else {

        }
        return new Vector2(x, y);
    }

    public static Vector2 getMouseCoordinates() {
        return DungeonScreen.getInstance().getGridStage().screenToStageCoordinates(new CursorPosVector2());
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
        if (baseView instanceof GridUnitView) {
            GridUnitView view = ((GridUnitView) baseView);
            if (view.getActions().size == 0) {
                if (view.getColor().a == 0) {
                    main.system.auxiliary.log.LogMaster.warn(  "Validation was required for " + view +
                            " - alpha==0");
                    view.fadeIn();
                }
                if (view.getParent() instanceof GridCellContainer) {
                    GridCellContainer cell = ((GridCellContainer) view.getParent());
                    if (!cell.isStackView())
                        if (view.getX() != cell.getViewX(view) ||
                                view.getY() != cell.getViewY(view)) {
                            cell.recalcUnitViewBounds();
                        }

                } else {
                    //TODO detach bug
                    if (view.getParent() instanceof GridPanel)
                        if (view.getX() <= 50)
                        if (view.getY() <= 0) {{
                                GridPanel grid = ((GridPanel) view.getParent());
                                grid.getCells()[view.getUserObject().getX()][grid.getGdxY(view.getUserObject().getY())].addActor(view);

                                main.system.auxiliary.log.LogMaster.warn(  "Validation was required for " + view +
                                        " - re-attached to gridcell!");
                            }
                        }
                }
            }
            if (view.getArrow() != null)
                if (view.getArrow().getActions().size == 0)
                    view.validateArrowRotation();

        }
    }
}
