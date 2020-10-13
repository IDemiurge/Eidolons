package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.bf.grid.cell.OverlayView;
import eidolons.libgdx.bf.overlays.map.WallMap;
import main.entity.EntityCheckMaster;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.MigMaster;

import java.awt.*;

public class OverlayingMaster {
    public static Vector2 getOffset(DIRECTION direction, DIRECTION direction2) {

        Dimension dim1 = getOffsetsForOverlaying(direction, 64, 64);
        Dimension dim2 = getOffsetsForOverlaying(direction2, 64, 64);

        //    a graceful mathematical solution was coming...
        //    int diff = direction.getDegrees() - direction1.getDegrees();
        //        int middle = (direction.getDegrees() + direction1.getDegrees()) / 2;
        int x = dim2.width - dim1.width;
        int y = dim2.height - dim1.height;

        //        if (Math.abs(diff) == 45) {
        ////        int dist = Math
        //            if (middle>180){
        //        }
        //        }

        return new Vector2(x, y);
    }

    public static void moveOverlaying(BattleFieldObject target, Unit source, boolean push) {
        Boolean clockwise = true;
        DIRECTION d = target.getDirection();
        DIRECTION relative = DirectionMaster.getRelativeDirection(target, source);
        FACING_DIRECTION facing = source.getFacing();
        if (d == null) {
            clockwise = null;
        } else
            switch (relative) {
                case UP:
                    if (d.isDiagonal()) {
                        clockwise = d.isGrowX();
                    } else
                        clockwise = null;
                    break;
                case UP_RIGHT:
                case DOWN_LEFT:
                    if (!facing.isVertical()) {
                        clockwise = false;
                    }
                    break;
                case UP_LEFT:
                case DOWN_RIGHT:
                    if (facing.isVertical()) {
                        clockwise = false;
                    }
                    break;

                case DOWN:
                    if (d.isDiagonal()) {
                        clockwise = !d.isGrowX();
                    } else
                        clockwise = null;
                    break;

                case LEFT:
                    if (d.isDiagonal()) {
                        clockwise = !d.isGrowY();
                    } else
                        clockwise = null;
                    break;
                case RIGHT:
                    if (d.isDiagonal()) {
                        clockwise = d.isGrowY();
                    } else
                        clockwise = null;
                    break;
            }
        if (clockwise == null) {
            if (d == null) {
                d = facing.getDirection();
                if (!push) {
                    d = d.flip();
                }
            } else {
                if (push) {
                    d = null;
                } else {
                    //try to take/break?
                }
            }
        } else {
            if (!push) {
                clockwise = !clockwise;
            }
            d = d.rotate45(clockwise);
        }

        target.setDirection(d);

        GuiEventManager.trigger(GuiEventType.MOVE_OVERLAYING, target);
    }

    public static Dimension getOffsetsForOverlaying(DIRECTION direction,
                                                    int width, int height) {
        return getOffsetsForOverlaying(direction, width, height, null);
    }

    public static Dimension getOffsetsForOverlaying(DIRECTION direction,
                                                    int width, int height, OverlayView view) {
        Coordinates c = view.getUserObject().getCoordinates();
        Obj wall = view.getUserObject().getGame().getObjectByCoordinate(c, false);
        boolean isWall = false;
        if (wall != null) {
            isWall = EntityCheckMaster.isWall(wall);
        }
        return getOffsetsForOverlaying(direction, width, height, view, isWall);
    }

    public static Dimension getOffsetsForOverlaying(DIRECTION direction,
                                                    int width, int height, OverlayView view, boolean wall) {

        float scale = view == null ? 0.5f : view.getScale();
        int w = (int) (width / scale);
        int h = (int) (height / scale);
        int calcXOffset = 0;
        int calcYOffset = 0;
        if (direction == null) {
            calcXOffset += (w - width) * (view == null ? OverlayView.SCALE : scale);
            calcYOffset += (h - height) * (view == null ? OverlayView.SCALE : scale);
        } else {
            int x = MigMaster.getCenteredPosition(w, width);

            if (direction.growX != null)
                x = (direction.growX) ? w - width : 0;


            int y = MigMaster.getCenteredPosition(h, width);

            if (direction.growY != null)
                y = (!direction.growY) ? h - width : 0;


            calcXOffset += x;
            calcYOffset += y;
        }
        if (!wall) {
            return new Dimension(calcXOffset, calcYOffset);
        }
        if (direction != null)
        if (direction.growY != null) {
            return new Dimension(calcXOffset + WallMap.getOffsetX(), calcYOffset);
        } else if (direction.growX != null) {
            return new Dimension(calcXOffset, calcYOffset + WallMap.getOffsetY());
        }
        return new Dimension(calcXOffset + WallMap.getOffsetX(), calcYOffset + WallMap.getOffsetY());
    }

}
