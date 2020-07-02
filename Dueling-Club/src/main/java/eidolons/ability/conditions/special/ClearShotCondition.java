package eidolons.ability.conditions.special;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.core.Eidolons;
import eidolons.system.math.DC_PositionMaster;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.math.PositionMaster;

import java.util.*;


public class ClearShotCondition extends MicroCondition {

    static Map<DC_Obj, Map<Obj, Boolean>> cache = new HashMap<>();
    boolean showVisuals;
    private boolean vision;
    private final String str1;
    private final String str2;
    private final int log_priority = 1;
    private boolean wallObstruction;

    public ClearShotCondition() {
        this(KEYS.SOURCE.toString(), KEYS.MATCH.toString());
    }

    public ClearShotCondition(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
        setGame(Eidolons.getGame());
    }

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

    private boolean isBlocking(DC_Obj source, DC_Obj target,
                               int x_, int y_) {
        for (DC_Obj obj :
                target.getGame().getObjMaster().getObjects (
                         x_, y_ , false))
        //         target.getGame().getMaster().getObjects(x_, y_))
        {
            if (!obj.isTransparent()) {
                if (obj.isObstructing(source, target))
                    return true;
            }
        }
        return checkWallObstruction(source, target, Coordinates.get(x_, y_));
    }

    public boolean check(Coordinates c, Coordinates c2) {
        Obj ce = game.getCellByCoordinate(c);
        if (ce == null) {
            return false;
        }
        Obj ce2 = game.getCellByCoordinate(c2);
        if (ce2 == null) {
            return false;
        }
        return check(ce, ce2);

    }

    @Override
    public boolean check(Ref ref) {
        Obj obj2 = game.getObjectById(ref.getId(str2));
        if (!(obj2 instanceof DC_Obj)) {
            return false;
        }
        DC_Obj target = (DC_Obj) obj2;
        Coordinates c2 = target.getCoordinates();
        if (c2 == null)
            return false;
//        Obj obj1 = game.getObjectById(ref.getId(str1));
//        if (obj1 instanceof DC_Cell){
//            return getForCell(((DC_Cell) obj1), target);
//        }
        DC_Obj source = (DC_Obj) game.getObjectById(ref.getId(str1));
        if (source == null)
            return false;
        if (c2.equals(source.getCoordinates()))
            return true;

        if (target.isOverlaying()) {
            if (target instanceof DC_Obj) {
                if (!isOverlayingWithinSightAngle(target, source))
                    return false;

            }
        }
        wallObstruction = false;
        Coordinates c1 = source.getCoordinates();
        boolean toCheck = true;
        boolean result = true;
        if (PositionMaster.inLine(c1, c2)) {
            //            result = source.getGame().getVisionMaster().getVisionController().getDiagObstructMapper().
            //             getVar(source, target);

            result = PositionMaster.noObstaclesInLine(source, target, game
                    .getGrid());
            toCheck = false;
            if (!result)
                return cacheResult(source, target, result);
        } else { // TODO TRANSPARENT FOR VISION!
            if (PositionMaster.inLineDiagonally(c1, c2)) {
                result = PositionMaster.noObstaclesInDiagonal(c1, c2, game
                        .getGrid(), source);
                if (!result)
                    return cacheResult(source, target, result);

                List<Coordinates> list = new ArrayList<>();
                if (!c2.isAdjacent(source.getCoordinates())) {
                    DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);
                    list = (DC_PositionMaster.getLine(false, direction, source.getCoordinates(),
                            Math.abs(source.getX() - target.getX())));// PositionMaster.getDistance(source,
                } else {
                    list.add(target.getCoordinates());
                }
                for (int i = 0, listSize = list.size(); i < listSize; i++) {
                    Coordinates c = list.get(i);
                    if (checkWallObstruction(source, target, c))
                        return cacheResult(source, target, false);
                }
                return cacheResult(source, target, true);
            }
        }

        if (!result)
            return cacheResult(source, target, result);
        if (!toCheck)
            return cacheResult(source, target, result);


        result = checkClearShot(source, target);
        return cacheResult(source, target, result);
    }


    private boolean cacheResult(DC_Obj source, DC_Obj target, boolean result) {
        if (wallObstruction)
            source.getGame().getVisionMaster().getVisionController().
                    getWallObstructionMapper().set(source.getCoordinates(),
                    source.getGame().getCellByCoordinate(target.getCoordinates()), !result);
        return result;
    }


    public boolean checkClearShot(DC_Obj source, DC_Obj target,
                                  boolean mirrorRectangle) {
        int x = Math.abs(mirrorRectangle ? source.getX() - target.getX() : source.getY()
                - target.getY()); // greater dimension of final
        // rectangle
        int y = Math.abs(!mirrorRectangle ? source.getX() - target.getX() : source.getY()
                - target.getY()); // lesser dimension of final
        // rectangle
        boolean flippedX = source.getX() - target.getX() > 0;
        boolean flippedY = source.getY() - target.getY() > 0;
        if (!mirrorRectangle) {
            boolean equal = flippedX == flippedY;
            boolean cache = flippedY;
            flippedY = flippedX;
            flippedX = cache;
            if (!equal) {
                flippedY = !flippedY;
                flippedX = !flippedX;
            }
        }

//        boolean cached =  ArrayMaster.isClearshotCached(source, target);
        boolean[][] array =
//                ArrayMaster.getBooleanFalseArray(x-1,y+1 );
                new boolean[x - 1][y + 1];
//        if ( cached){
//            for (int i = 0; i + 1 < x; i++)            // don't preCheck source
//            {
//                for (int j = 0; j <= y; j++) {
//                if (array[i][j])
//                    if (!checkClearShot(x, y, array)) {
//                        return false;
//                    }
//                }
//
//        } else
//        {
        boolean toCheck = false;
        for (int i = 0; i + 1 < x; i++)            // don't preCheck source
        {
            for (int j = 0; j <= y; j++) { // don't preCheck target
                int x_ = source.getX(); // greater mirrorRectangle ?
                if (mirrorRectangle) {
                    x_ = x_ + (flippedX ? -(i + 1) : i + 1);
                } else {
                    x_ = x_ + (flippedX ? -(j) : j);
                }
                int y_ = source.getY();// lesser mirrorRectangle ? source.getX()
                if (mirrorRectangle) {
                    y_ = y_ + (flippedY ? -(j) : j);
                } else {
                    y_ = y_ + (flippedY ? -(i + 1) : i + 1);
                }
                if (i + j >= 1)
                    toCheck = true;
                if (isBlocking(source, target,
                        x_, y_)) {
                    array[i][j] = true;
                    if (toCheck)
                        //check if this much blocking is sufficient already...
                        if (!checkClearShot(x, y, array)) {
                            return false;
                        }
                }
            }
        }
        return true;
    }

    private Set<Coordinates> checkWallObstructionAlt(Coordinates coordinates, Set<Coordinates> full) {
        //let's try to quickly cut some coords
/*
diagonal adjacent walls will remove whole directions..

 */

        for (DIRECTION direction : DIRECTION.DIAGONAL) {
            Coordinates c = coordinates.getAdjacentCoordinate(direction.rotate45(true));
            Coordinates c2 = coordinates.getAdjacentCoordinate(direction.rotate45(false));
            if (game.isWall(c) && game.isWall(c2)){
                full.removeIf(coord-> checkRemove(coord, coordinates, direction));
            }
        }
        return full;
    }

    private boolean checkRemove(Coordinates coord, Coordinates c, DIRECTION direction) {
        switch (direction) {
            case UP_LEFT:
                return coord.x<c.x && coord.y<c.y;
            case UP_RIGHT:
                return coord.x>c.x && coord.y<c.y;
            case DOWN_RIGHT:
                return coord.x>c.x && coord.y>c.y;
            case DOWN_LEFT:
                return coord.x<c.x && coord.y>c.y;
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
    private boolean checkWallObstruction(DC_Obj source, DC_Obj target, Coordinates coordinates) {
        Boolean result = source.getGame().getVisionMaster().getVisionController().
                getWallObstructionMapper().get(source.getCoordinates(),
                source.getGame().getCellByCoordinate(target.getCoordinates()));
        if (result != null) {
            return result;
        }
        DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);

        // difference from <?> no greater than... on both/either axis?

        for (Coordinates c : coordinates.getAdjacent()) {
            //            isCoordinateWallObstructing(c, source, target, coordinates, direction, angle);

            DIRECTION relativeDirection = c.isAdjacent(source.getCoordinates()) ? DirectionMaster
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
            if (coordinates.equals(target.getCoordinates())
                    || !target.getCoordinates().isAdjacent(source.getCoordinates(), false)
            ) {
                double d = PositionMaster
                        .getExactDistance(source.getCoordinates(), target.getCoordinates())
                        - PositionMaster.getExactDistance(c, source.getCoordinates());
                if ((d) <= 0.0) {
                    continue; //must not be beyond target
                }
                d = PositionMaster
                        .getExactDistance(source.getCoordinates(), target.getCoordinates())
                        - PositionMaster.getExactDistance(coordinates, target.getCoordinates());
                if ((d) <= 0.0) {
                    continue; //must not be behind source
                }

            }
            boolean left = false;
            if (source.getY() != c.y) {
                //                PositionMaster.isToTheLeft(Coordinates.getVar())
                left = (float) Math.abs(source.getX() - c.x) / Math.abs(source.getY() - c.y) <
                        getAngle(source.getCoordinates(), target.getCoordinates());

            }

            List<DIRECTION> list = source.getGame().getBattleFieldManager().getWallMap().get(c);
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
                wallObstruction = true;
                return true;


            }
        }

        return false;
    }

    private float getAngle(Coordinates source, Coordinates target) {
        return (float) Math.abs(source.getX() - target.getX())
                / Math.abs(source.getY() - target.getY());
    }

    public boolean checkClearShot(DC_Obj source, DC_Obj target) {
        boolean mirrorRectangle = Math.abs(source.getX() - target.getX()) > Math // mirror
                .abs(source.getY() - target.getY()); // is dX or dY
        int secondDimension = Math.abs(!mirrorRectangle ? source.getX() - target.getX() : source
                .getY()
                - target.getY()); // dimensions
        // of final
        // rectangle
        if (!checkClearShot(source, target, mirrorRectangle)) {
            return false;
        }
        if (secondDimension > 0) {
            wallObstruction = false;
            return checkClearShot(source, target, !mirrorRectangle);
        }
        return true;

    }

    //
    public boolean checkClearShot(int dX, int dY, boolean[][] obstructionArray) {

        double slope = (double) dY / dX;
        double k = 0.5 - slope / 2;
        double x = 1;
        double a, b;
        while (x < dX) {
            a = Math.floor(LineY(slope, k, x));
            b = Math.floor(LineY(slope, k, x + 1));
            if (obstructionArray[(int) x - 1][(int) a]
                    && obstructionArray[(int) x - 1][(int) b]) {
                return false;
            }
            x++;
            // TODO mystery solutions: 1) transform the Array 2) run the same
        }

        return true;

    }

    private double LineY(double slope, double k, double x) {
        double y = slope * x + k;
        //        log("y= " + y);
        return y;
    }

    private void log(String str) {
        if (showVisuals || game.isDebugMode())
            if (!isVision()) {
                LogMaster.log(log_priority, str);
            }
    }

    public void setShowVisuals(boolean showVisuals) {
        this.showVisuals = showVisuals;
    }

    public boolean isVision() {
        return vision;
    }

    public void setVision(boolean vision) {
        this.vision = vision;
    }

}