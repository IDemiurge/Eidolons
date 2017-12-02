package main.ability.conditions.special;

import main.content.DC_TYPE;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.swing.XLine;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.DC_PositionMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClearShotCondition extends MicroCondition {

    public static final float SIGHT_RANGE_FACTOR = 2.5f;
    static Map<BattleFieldObject, Map<Obj, Boolean>> cache = new HashMap<>();
    boolean showVisuals;
    private boolean vision;
    private String str1;
    private String str2;
    private int log_priority = 1;
    private static boolean unitTestBreakMode;

    public ClearShotCondition() {
        this(KEYS.SOURCE.toString(), KEYS.MATCH.toString());
    }

    public ClearShotCondition(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;

    }

    public static void clearCache() {
        cache.values().forEach(map -> map.clear());
    }

    public static Map<BattleFieldObject, Map<Obj, Boolean>> getCache() {
        return cache;
    }

    public static double getMaxCheckDistance(Unit activeUnit, DC_Obj unit) {
        return getMaxCheckDistance(activeUnit, unit.getCoordinates());
    }
        public static double getMaxCheckDistance(Unit activeUnit, Coordinates coordinates) {
        return SIGHT_RANGE_FACTOR * activeUnit.getSightRangeTowards(coordinates);
    }

    private boolean isBlocking(DC_Obj source, DC_Obj target,
                               int x_, int y_) {
        boolean result = isBlocking(source, target, x_, y_, target.getGame().getMaster()
         .getStructuresArray());
        if (result)
            return true;
        result = isBlocking(source, target, x_, y_, target.getGame().getMaster().getUnitsArray());
        if (result)
            return true;
        return checkWallObstruction(source, target, new Coordinates(x_, y_));
    }

    private boolean isBlocking(DC_Obj source, DC_Obj target,
                               int x_, int y_,
                               BattleFieldObject[] objects) {

        boolean obstructing = false;

        for (BattleFieldObject obj : objects) {
            if (obj.getX() != x_)
                continue;
            if (obj.getY() != y_)
                continue;
            if (
                //!isVision() ||
             !obj.isTransparent()) {
                obstructing = obj.isObstructing(source, target);
            }
            if (obstructing) {
//                log(obstructing + " by " + obj);
                break;
            }
        }
        if (obstructing) {
            return true;
        }

        return false;
    }

	/*
     *
	 * I draw a line between centers of "Shooter"(caster, the one watching,
	 * whatever) and Target.
	 * 
	 * The line might be straight or diagonal, this are trivial cases where any
	 * unit on the line blocks the attack.
	 * 
	 * Alternatively the line is drawn as a diagonal to a rectangle: it has long
	 * side and short side.
	 * 
	 * I take Shooter and Target to form this rectangle. According to shooter
	 * and target sizes I determine which tiles contain something blocking, and
	 * which do not.
	 * 
	 * Now I will rotate and mirror my rectangle to make shooter have
	 * coordinates 0,0 and target dX,dY, where dX > dY(if not, mirror!).
	 * 
	 * I draw a line between centers of Shooter and Target. if a line goes close
	 * to a border of two cells, I will require that they both be occupied to
	 * block the target. If the line goes close to the center of the cell, only
	 * this cell must be checked on this step. Now all cells on the line will be
	 * checked.
	 * 
	 * I implemented the preCheck based on dX, dY and obstructionArray, which
	 * should containt True if cell contains something blocking, and False if
	 * not.
	 * 
	 * Return meaning: True for "is in clear shot".
	 *                                   <><><><><> 
	 * 1) get slope factor (rectangle)
	 * 2) get base y (intersect vector with y axis)
	 * 3) run for each X cell : project intersection with  verticals , IFF 2 integers - big circle (2 blocks requires) 
	 * 
	 * get rectangle -> transform it ->
	 * 
	 * 
	 */

    @Override
    public boolean check(Ref ref) {
        // consider flying/non-obstructing!
        Obj obj = game.getObjectById(ref.getId(str2));
if (!(obj instanceof DC_Obj)){
    return false;
}
        DC_Obj target = (DC_Obj) game.getObjectById(ref.getId(str2));
        if (target == null) {
            return false;
        }
        Coordinates c2 = target.getCoordinates();
        if (c2 == null)
            return false;
        BattleFieldObject source = (BattleFieldObject) game.getObjectById(ref.getId(str1));
        if (source == null)
            return false;
        if (c2.equals(source.getCoordinates()))
            return true;
        // if (c2.isAdjacent(source.getCoordinates()))
        // toc
//        if (source.getGame().getDungeon().isSurface() )
//        if (source.checkProperty(G_PROPS.STANDARD_PASSIVES, STANDARD_PASSIVES.FLYING.getName()))
//            return true; //TODO use lift height instead!!

        if (target.isOverlaying()) {
            if (target instanceof BattleFieldObject) {
                DIRECTION d = ((BattleFieldObject) target).getDirection();
                DIRECTION d1 = DirectionMaster.getRelativeDirection(target, source);
                if (d != null) {
                    if (d1 != d) {
                        if (Math.abs( d.getDegrees()   - d1.getDegrees())   > 90)
                            return false;
                    }

                }
            }
        }

        Coordinates c1 = source.getCoordinates();
        Map<Obj, Boolean> map = cache.get(source);
        if (map == null) {
            map = new HashMap<>();
            cache.put((BattleFieldObject) source, map);
        }
        Boolean result = map.get(target);
        if (result == null)
            result = true;
        else
            return result;
        boolean toCheck = true;
        if (target.isInfoSelected()) {
            toCheck = true;
        }
        if (PositionMaster.inLine(c1, c2)) {
            result = PositionMaster.noObstaclesInLine(source, target, game.getBattleField()
             .getGrid());
            toCheck = false;
            if (!result)
                return cacheResult(map, target, result);
        } else { // TODO TRANSPARENT FOR VISION!
            if (PositionMaster.inLineDiagonally(c1, c2)) {
                result = PositionMaster.noObstaclesInDiagonal(c1, c2, game.getBattleField()
                 .getGrid(), source);
                if (!result)
                    return cacheResult(map, target, result);

                List<Coordinates> list = new ArrayList<>();
                if (!c2.isAdjacent(source.getCoordinates())) {
                    DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);
                    list = (DC_PositionMaster.getLine(false, direction, source.getCoordinates(),
                     Math.abs(source.getX() - target.getX())));// PositionMaster.getDistance(source,
                } else {
                    list.add(target.getCoordinates());
                }
                for (Coordinates c : list) {
                    if (checkWallObstruction(source, target, c))
                        return cacheResult(map, target, false);
                }
                return cacheResult(map, target, true);
            }
        }

        if (!result)
            return cacheResult(map, target, result);
        if (!toCheck)
            return cacheResult(map, target, result);


        result = checkClearShot(source, target);
        return cacheResult(map, target, result);
    }

    private boolean cacheResult(Map<Obj, Boolean> map, DC_Obj target, Boolean result) {
        map.put(target, result);
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

        Boolean[][] array = new Boolean[x - 1][y + 1];
//        log("Checking Clear Shot for " + source + " on " + target + "; mirrored = "
//         + mirrorRectangle + "; flippedX = " + flippedX + "; flippedY = " + flippedY);
        boolean toCheck = false;
        for (int i = 0; i + 1 < x; i++)            // don't preCheck source
        {
            for (int j = 0; j <= y; j++) { // don't preCheck target
                int x_ = source.getX(); // greater mirrorRectangle ?
                // source.getY() :
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

                if (isBlocking(source, target,
                 x_, y_)) {
                    toCheck = true;
                    array[i][j] = true;
                } else {
                    array[i][j] = false;
                }
            }
        }
        if (!toCheck) {
            return true;
        }
//        log("Checking Clear Shot for " + source + " on " + target + "; obstruction array = "
//         + new ArrayMaster<Boolean>().get2dList(array));
        return checkClearShot(x, y, array);
    }

    private boolean checkWallObstruction(DC_Obj source, DC_Obj target, Coordinates coordinates) {
        if (isUnitTestBreakMode()) {
            return false;
        }
        DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);
        target.setBlockingCoordinate(coordinates);
        float angle = getAngle(source.getCoordinates(), target.getCoordinates());

        // and distance!

        // diagonal adjacent
        // difference from <?> no greater than... on both/either axis?

        for (Coordinates c : coordinates.getAdjacent()) { // c


            DIRECTION relativeDirection = c.isAdjacent(source.getCoordinates()) ? DirectionMaster
             .getRelativeDirection(c, coordinates) : DirectionMaster.getRelativeDirection(
             coordinates, c);
            if (BooleanMaster.areOpposite(relativeDirection.isGrowX(), direction.isGrowX())) {
                continue;
            }
            if (BooleanMaster.areOpposite(relativeDirection.isGrowY(), direction.isGrowY())) {
                continue;
            }
            double distance = PositionMaster.getDistanceToLine(new XLine(source.getCoordinates(),
             target.getCoordinates()), c);
            if (distance > 1) {
                continue;
            }
            if (coordinates.equals(target.getCoordinates())
             || !target.getCoordinates().isAdjacent(source.getCoordinates(), false)
                // && !coordinates.equals(target.getCoordinates())
                // && target.getCoordinates().isAdjacent(source.getCoordinates())
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
//            DIRECTION d1 = DirectionMaster.getRelativeDirection(source.getCoordinates(), c);
//            DIRECTION d2 = DirectionMaster.getRelativeDirection(target.getCoordinates(), c);
            boolean left = false;
            if (source.getY() != c.y) {
                left = (float) Math.abs(source.getX() - c.x) / Math.abs(source.getY() - c.y) < angle;

            } else {
                // by direction
                // if (BooleanMaster.areOpposite(d1.isGrowX(),
                // direction.isGrowX()))
                // if (!BooleanMaster.areOpposite(d2.isGrowY(),
                // direction.isGrowY())) {
                // left = true;
                // if (target.isInfoSelected())
                // main.system.auxiliary.LogMaster.log(1, d1 + " 1vs " +
                // direction);
                // }
                // if (BooleanMaster.areOpposite(d2.isGrowY(),
                // direction.isGrowY()))
                // if (!BooleanMaster.areOpposite(d1.isGrowX(),
                // direction.isGrowX())) {
                // left = true;
                // if (target.isInfoSelected())
                // main.system.auxiliary.LogMaster.log(1, d2 + " 2vs " +
                // direction);
                // }
            }
            // positive/negative?

            List<DIRECTION> list = source.getGame().getBattleFieldManager().getWallMap().get(c);
            // boolean hasDiagonal = false;
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
                    if (BooleanMaster.areOpposite(d.isGrowX(), direction.isGrowX())) {
                        continue;
                    }
                    if (!BooleanMaster.areOpposite(d.isGrowY(), direction.isGrowY())) {
                        continue;
                    }
                }
                if (!left) {
                    if (BooleanMaster.areOpposite(d.isGrowY(), direction.isGrowY())) {
                        continue; // TODO does X/Y interchange?
                    }
                    if (!BooleanMaster.areOpposite(d.isGrowX(), direction.isGrowX())) {
                        continue; // TODO does X/Y interchange?
                    }
                }
                if (target.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                    if (target.isInfoSelected()) {
                        LogMaster.log(1, target + " vs " + coordinates
                         + " distance: " + distance);
                    }
                    if (target.isInfoSelected()) {
                        LogMaster.log(1, angle + " vs "
                         + (float) Math.abs(source.getX() - c.x)
                         / Math.abs(source.getX() - c.y));
                    }

                } // if (d.isGrowX() == !left)
                // continue;
                target.setBlockingWallDirection(d);
                target.setBlockingWallCoordinate(c);
                target.setBlockingDiagonalSide(left);

                if (showVisuals) {
//                    GuiEventManager.trigger(GuiEventType.SHOW_CLEARSHOT, new ClearShotData(target, d, c, left));
                }
                // TODO WALL HEIGHT!
                return true;


            }
        }

        // getRelevantCoordinates(source, target);

        // for each uninterrupted wall segment...
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
            return checkClearShot(source, target, !mirrorRectangle);
        }
        return true;

    }

    //
    public boolean checkClearShot(int dX, int dY, Boolean obstructionArray[][]) {

        double slope = (double) dY / dX;
        double k = 0.5 - slope / 2;
        double x = 1;
        double a, b;
//        log("slope= " + slope + "; k= " + k);
        while (x < dX) {
            a = Math.floor(LineY(slope, k, x));
            b = Math.floor(LineY(slope, k, x + 1));

//            log("a= " + a + "; b= " + b);
            if (obstructionArray[(int) x - 1][(int) a]
             && obstructionArray[(int) x - 1][(int) b]) {
                // target.setBlockingCoordinate(new Coordinates(a, b));
//                GuiEventManager.trigger(GuiEventType.SHOW_CLEARSHOT, new ClearShotData(ref , target, d, c, left));
                return false;
            }
            x++;

            // TODO mystery solutions: 1) transform the Array 2) run the same
            // preCheck without 'rotation'
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

    public static boolean isUnitTestBreakMode() {
        return unitTestBreakMode;
    }

    public static void setUnitTestBreakMode(boolean unitTestBreakMode) {
        ClearShotCondition.unitTestBreakMode = unitTestBreakMode;
    }
    // if there are 2 cells adjacent with diagonal
    // walls, we're blocked

    // TODO just preCheck that it goes the right way!
    // depending on whether the C lies above or below the
    // diagonal!

    // for (Coordinates c2 : c.getAdjacentCoordinates(null)) {
    // // TODO same as getting just 1 coordinate?
    // if (!c2.isAdjacent(coordinates))
    // continue;
    // if
    // (!source.getGame().getBattleFieldManager().hasDiagonalWall(c2))
    // continue;
    //
    // // if
    // //
    // (BooleanMaster.areOpposite(DirectionMaster.getRelativeDirection(
    // // c, c2).isGrowX(), direction.isGrowX()))
    // // if (BooleanMaster.areOpposite(DirectionMaster
    // // .getRelativeDirection(c, c2).isGrowY(),
    // // direction
    // // .isGrowY()))
    // // continue;
    //
    // target.setBlockingCoordinate(coordinates);
    // return true;
    //
    // }
    // run another loop here? preCheck all again now with
    // adjacent
    // condition
    // }
    // firstCoordinate = c;
}
