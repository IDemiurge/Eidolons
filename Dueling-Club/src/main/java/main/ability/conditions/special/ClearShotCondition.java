package main.ability.conditions.special;

import main.content.DC_TYPE;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.core.Eidolons;
import main.swing.XLine;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.PositionMaster;

import java.util.List;

public class ClearShotCondition extends MicroCondition {

    private boolean vision;
    private String str1;
    private String str2;
    private int log_priority = 0;

    public ClearShotCondition() {
        this(KEYS.SOURCE.toString(), KEYS.MATCH.toString());
    }

    public ClearShotCondition(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;

    }

    public boolean checkClearShotNew(DC_Obj source, DC_Obj target) {
        Coordinates[] toCheck = pavelsAlg(source.getX(), source.getY(),
                target.getX(), target.getY());
        for (Coordinates c : toCheck) {
            if (isBlocking(source, target, c.x, c.y)) {
                return false;
            }
        }
        return true;
    }

    private Coordinates[] pavelsAlg(int x, int y, int x1, int y1) {
        Coordinates[] arrayToCheck = new Coordinates[10];
        new Coordinates(x, y);

        return arrayToCheck;

    }


    private boolean isBlocking(DC_Obj source, DC_Obj target,
                               int x_, int y_) {

        Coordinates coordinates = new Coordinates(x_, y_);
        List<Unit> units = Eidolons.gameMaster.getObjectsOnCoordinate(coordinates);
        if (units.isEmpty()) {

            if (!isVision()) {
                log("No unit at " + coordinates);
            }
            return false;
        } else {
            boolean obstructing = false;
            for (Unit unit : units) {
                if (!isVision() || !unit.isTransparent()) {
                    obstructing = unit.isObstructing(source, target);
                }
                if (obstructing) {
                    log(obstructing + " by " + unit);
                    break;
                }
            }
            if (obstructing) {
                return true;
            }
        }

        return checkWallObstruction(source, target, coordinates);
    }

    @Override
    public boolean check(Ref ref) {
       /* // consider flying/non-obstructing!

        DC_Obj target = (DC_Obj) game.getObjectById(ref.getId(str2));
        if (target == null) {
            return false;
        }
        Coordinates c2 = target.getCoordinates();
        if (c2 == null)
            return false;
        DC_Obj source = (DC_Obj) game.getObjectById(ref.getId(str1));
        if (source == null)
            return false;
        if (c2.equals(source.getCoordinates()))
            return true;
        // if (c2.isAdjacent(source.getCoordinates()))
        // toc
        if (source.checkProperty(G_PROPS.STANDARD_PASSIVES, STANDARD_PASSIVES.FLYING.getName()))
            return true;
        Coordinates c1 = source.getCoordinates();
        boolean result = true;
        boolean toCheck = true;
        if (target.isInfoSelected()) {
            toCheck = true;
        }
        if (PositionMaster.inLine(c1, c2)) {
            result = PositionMaster.noObstaclesInLine(source, target, game.getBattleField()
                    .getGrid());
            toCheck = false;
            if (!result)
                return false;
        } else { // TODO TRANSPARENT FOR VISION!
            if (PositionMaster.inLineDiagonally(c1, c2)) {
                result = PositionMaster.noObstaclesInDiagonal(c1, c2, game.getBattleField()
                        .getGrid(), source);
                if (!result)
                    return false;

                List<Coordinates> list = new ListMaster<Coordinates>().getList(target
                        .getCoordinates());
                if (!c2.isAdjacent(source.getCoordinates())) {
                    DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);
                    list = (DC_PositionMaster.getLine(false, direction, source.getCoordinates(),
                            Math.abs(source.getX() - target.getX())));// PositionMaster.getDistance(source,
                    // target)
                    // ));
                }
                for (Coordinates c : list) {
                    if (checkWallObstruction(source, target, c))
                        return false;
                }
                return true;
            }
        }

        if (!result)
            return false;
        if (!toCheck)
            return true;

        return checkClearShot(source, target);*/
        return true;
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
	 * 1) getOrCreate slope factor (rectangle)
	 * 2) getOrCreate base y (intersect vector with y axis)
	 * 3) run for each X cell : project intersection with  verticals , IFF 2 integers - big circle (2 blocks requires) 
	 * 
	 * getOrCreate rectangle -> transform it ->
	 * 
	 * 
	 */

    public boolean checkClearShot(DC_Obj source, DC_Obj target,
                                  boolean mirrorRectangle) {
        int x = Math.abs(!mirrorRectangle ? source.getX() - target.getX() : source.getY()
                - target.getY()); // greater dimension of final
        // rectangle
        int y = Math.abs(mirrorRectangle ? source.getX() - target.getX() : source.getY()
                - target.getY()); // lesser dimension of final
        // rectangle
        boolean flippedX = source.getX() - target.getX() > 0;
        boolean flippedY = source.getY() - target.getY() > 0;
        if (mirrorRectangle) {
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
        log("Checking Clear Shot for " + source + " on " + target + "; mirrored = "
                + mirrorRectangle + "; flippedX = " + flippedX + "; flippedY = " + flippedY);
        boolean toCheck = false;
        for (int i = 1; i < x; i++)            // don't preCheck source
        {
            for (int j = 0; j <= y; j++) { // don't preCheck target
                int x_ = source.getX(); // greater mirrorRectangle ?
                // source.getY() :
                if (!mirrorRectangle) {
                    x_ = x_ + (flippedX ? -i : i);
                } else {
                    x_ = x_ + (flippedX ? -j : j);
                }
                int y_ = source.getY();// lesser mirrorRectangle ? source.getX()
                if (!mirrorRectangle) {
                    y_ = y_ + (flippedY ? -j : j);
                } else {
                    y_ = y_ + (flippedY ? -i : i);
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
        log("Checking Clear Shot for " + source + " on " + target + "; obstruction array = "
                + new ArrayMaster<Boolean>().get2dList(array));
        return checkClearShot(x, y, array);
    }


    private boolean checkWallObstruction(DC_Obj source, DC_Obj target, Coordinates coordinates) {

        DIRECTION direction = DirectionMaster.getRelativeDirection(source, target);
        target.setBlockingCoordinate(coordinates);
        float angle = getAngle(source.getCoordinates(), target.getCoordinates());

        // and distance!

        // diagonal adjacent
        // difference from <?> no greater than... on both/either axis?

        for (Coordinates c : coordinates.getAdjacentCoordinates()) { // c

            if (coordinates.equals(target.getCoordinates())
                    || !target.getCoordinates().isAdjacent(source.getCoordinates(), false)
                // && !coordinates.equals(target.getCoordinates())
                // && target.getCoordinates().isAdjacent(source.getCoordinates())
                    ) {
                if (PositionMaster.getDistance(c, target.getCoordinates()) >= PositionMaster
                        .getDistance(source.getCoordinates(), target.getCoordinates())) {
                    continue;
                }
                if (PositionMaster.getDistance(c, source.getCoordinates()) > PositionMaster
                        .getDistance(source.getCoordinates(), target.getCoordinates())) {
                    continue;
                }
                if (PositionMaster.getDistance(coordinates, target.getCoordinates()) > PositionMaster
                        .getDistance(source.getCoordinates(), target.getCoordinates())) {
                    continue;
                }

            }
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
            DIRECTION d1 = DirectionMaster.getRelativeDirection(source.getCoordinates(), c);
            DIRECTION d2 = DirectionMaster.getRelativeDirection(target.getCoordinates(), c);
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
                // TODO WALL HEIGHT!
                return true;

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
        boolean mirrorRectangle = Math.abs(source.getX() - target.getX()) < Math // mirror
                .abs(source.getY() - target.getY()); // is dX or dY
        int secondDimension = Math.abs(mirrorRectangle ? source.getX() - target.getX() : source
                .getY()
                - target.getY()); // dimensions
        // of final
        // rectangle
        if (!checkClearShot(source, target, mirrorRectangle)) {
            return false;
        }
        if (secondDimension > 1) {
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
        log("slope= " + slope + "; k= " + k);
        while (x < dX) {
            a = Math.floor(LineY(slope, k, x));
            b = Math.floor(LineY(slope, k, x + 1));

            log("a= " + a + "; b= " + b);
            if (obstructionArray[(int) x - 1][(int) a]
                    && obstructionArray[(int) x - 1][(int) b]) {
                // target.setBlockingCoordinate(new Coordinates(a, b));

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
        log("y= " + y);
        return y;
    }

    private void log(String str) {
        if (!isVision()) {
            LogMaster.log(log_priority, str);
        }
    }

    public boolean isVision() {
        return vision;
    }

    public void setVision(boolean vision) {
        this.vision = vision;
    }

}
