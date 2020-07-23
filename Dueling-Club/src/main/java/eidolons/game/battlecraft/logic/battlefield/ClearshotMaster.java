package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.core.game.DC_Game;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClearshotMaster {

    static Map<DC_Obj, Map<Obj, Boolean>> cache = new HashMap<>();

    public static void clearCache() {
        cache.values().forEach(map -> map.clear());
    }

    public static Map<DC_Obj, Map<Obj, Boolean>> getCache() {
        return cache;
    }

    public static final boolean isOverlayingWithinSightAngle(DC_Obj target,
                                                             DC_Obj source) {
        return isOverlayingWithinSightAngle(target.getCoordinates(), target.getDirection(), source.getCoordinates());
    }

    //    ###
    //    ##D
    //    ###U
    //== 45 == OK
    //    D##
    //    ###
    //    ###U
    //== 45 == NO TODO could customize 90 angle!
    public static final boolean isOverlayingWithinSightAngle(Coordinates c, DIRECTION d, Coordinates c1) {
        DIRECTION d1 = DirectionMaster.getRelativeDirection(c, c1);
        if (d != null) {
            if ((Math.abs(d.getDegrees() - d1.getDegrees()) + 360) % 360 <= 90)
                return true;
            return Math.abs(d.getDegrees() % 360 - d1.getDegrees() % 360) <= 90;
        }
        return false;
    }

    public static void filterWallObstructed(Coordinates coordinates, Collection<Coordinates> full) {
        //let's try to quickly cut some coords
/*
diagonal adjacent walls will remove whole directions..
 */

        for (DIRECTION direction : DIRECTION.clockwise) {
            Coordinates adj = coordinates.getAdjacentCoordinate(direction);
            if (adj == null) {
                continue;
            }
            if (direction.isDiagonal()) {
                // if (DC_Game.game.isWall(adj)){
                //     full.removeIf(coord -> PositionMaster.inLineDiagonally(coord, coordinates)
                //             &&  //TODO 
                // }
                Coordinates c = coordinates.getAdjacentCoordinate(direction.rotate45(true));
                Coordinates c2 = coordinates.getAdjacentCoordinate(direction.rotate45(false));
                if (c2 == null || c == null) {
                    continue;
                }
                if (DC_Game.game.isWall(c) && DC_Game.game.isWall(c2)) {
                    full.removeIf(coord -> checkRemove(coord, coordinates, direction));
                }
            } else {
                if (DC_Game.game.isWall(adj))
                    if (direction.isVertical()) {
                        full.removeIf(coord -> (!direction.growY
                                ? coord.y + 1 < coordinates.y
                                : coord.y - 1 > coordinates.y) &&
                                coord.x == coordinates.x);
                    } else {
                        full.removeIf(coord -> (!direction.growX
                                ? coord.x + 1 < coordinates.x
                                : coord.x - 1 > coordinates.x) &&
                                coord.y == coordinates.y);
                    }
            }
        }
    }

    private static boolean checkRemove(Coordinates coord, Coordinates c, DIRECTION direction) {
        switch (direction) {
            case UP_LEFT:
                return coord.x < c.x && coord.y < c.y;
            case UP_RIGHT:
                return coord.x > c.x && coord.y < c.y;
            case DOWN_RIGHT:
                return coord.x > c.x && coord.y > c.y;
            case DOWN_LEFT:
                return coord.x < c.x && coord.y > c.y;
        }
        return false;
    }

    /**
     * Checks if there is a diagonal wall block that obstructs clearshot between source and target
     *
     * @param source
     * @param target
     * @param coordinates
     * @return
     */
    public static boolean checkWallObstruction(DC_Obj source, DC_Obj target, Coordinates coordinates) {
        boolean result = checkWallObstruction(source.getCoordinates(), target.getCoordinates(), coordinates);
        DC_Game.game.getVisionMaster().getVisionController().
                getWallObstructionMapper().set(source.getCoordinates(), target, result);
        return result;
    }

    /**
     * Checks if there is a diagonal wall block that obstructs clearshot between source and target
     *
     * @param source
     * @param target
     * @param coordinates
     * @return
     */
    public static boolean checkWallObstruction(Coordinates source, Coordinates target, Coordinates coordinates) {
        Boolean result = DC_Game.game.getVisionMaster().getVisionController().
                getWallObstructionMapper().get(source,
                DC_Game.game.getCellByCoordinate(target));
        if (result != null) {
            return result;
        }
        DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);

        // difference from <?> no greater than... on both/either axis?

        for (Coordinates c : coordinates.getAdjacent()) {
            //            isCoordinateWallObstructing(c, source, target, coordinates, direction, angle);

            DIRECTION relativeDirection = c.isAdjacent(source) ? DirectionMaster
                    .getRelativeDirection(c, coordinates) : DirectionMaster.getRelativeDirection(
                    coordinates, c);
            if (Bools.areOpposite(relativeDirection.growX, direction.growX)) {
                continue;
            }
            if (Bools.areOpposite(relativeDirection.growY, direction.growY)) {
                continue;
            }
            double distance = PositionMaster.getDistanceToLine(
                    c.x, c.y, source.getX(), source.getY(),
                    target.getX(), target.getY());
            if (distance > 1) {
                continue;
            }
            if (coordinates.equals(target)
                    || !target.isAdjacent(source, false)
            ) {
                double d = PositionMaster
                        .getExactDistance(source, target)
                        - PositionMaster.getExactDistance(c, source);
                if ((d) <= 0.0) {
                    continue; //must not be beyond target
                }
                d = PositionMaster
                        .getExactDistance(source, target)
                        - PositionMaster.getExactDistance(coordinates, target);
                if ((d) <= 0.0) {
                    continue; //must not be behind source
                }

            }
            boolean left = false;
            if (source.getY() != c.y) {
                //                PositionMaster.isToTheLeft(Coordinates.getVar())
                left = (float) Math.abs(source.getX() - c.x) / Math.abs(source.getY() - c.y) <
                        getAngle(source, target);

            }

            List<DIRECTION> list = DC_Game.game.getBattleFieldManager().getWallMap().get(c);
            if (list == null) {
                continue;
            }

            for (DIRECTION d : list) { // TODO does direction matter???
                if (d != null) {
                    if (!d.isDiagonal()) {
                        continue;
                    }
                }

                if (left) {
                    if (Bools.areOpposite(d.growX, direction.growX)) {
                        continue;
                    }
                    if (!Bools.areOpposite(d.growY, direction.growY)) {
                        continue;
                    }
                }
                if (!left) {
                    if (Bools.areOpposite(d.growY, direction.growY)) {
                        continue; // TODO does X/Y interchange?
                    }
                    if (!Bools.areOpposite(d.growX, direction.growX)) {
                        continue; // TODO does X/Y interchange?
                    }
                }
                return true;


            }
        }

        return false;
    }

    private static float getAngle(Coordinates source, Coordinates target) {
        return (float) Math.abs(source.getX() - target.getX())
                / Math.abs(source.getY() - target.getY());
    }

    //
    public static boolean checkClearShot(int dX, int dY, boolean[][] obstructionArray) {

        double slope = (double) dY / dX;
        double k = 0.5 - slope / 2;
        double x = 1;
        double a, b;
        while (x < dX) {
            a = Math.floor(lineY(slope, k, x));
            b = Math.floor(lineY(slope, k, x + 1));
            if (obstructionArray[(int) x - 1][(int) a]
                    && obstructionArray[(int) x - 1][(int) b]) {
                return false;
            }
            x++;
            // TODO mystery solutions: 1) transform the Array 2) run the same
        }

        return true;

    }

    private static double lineY(double slope, double k, double x) {
        double y = slope * x + k;
        return y;
    }
}
