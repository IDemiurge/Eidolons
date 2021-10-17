package eidolons.ability.conditions.special;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.ClearshotMaster;
import eidolons.game.core.Eidolons;
import eidolons.system.math.DC_PositionMaster;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;

import java.util.ArrayList;
import java.util.List;

import static eidolons.game.battlecraft.logic.battlefield.ClearshotMaster.checkWallObstruction;
import static eidolons.game.battlecraft.logic.battlefield.ClearshotMaster.isOverlayingWithinSightAngle;
import static main.system.math.PositionMaster.*;


public class ClearShotCondition extends MicroCondition {

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


    private boolean isBlocking(DC_Obj source, DC_Obj target,
                               int x_, int y_) {
        for (DC_Obj obj : target.getGame().getObjMaster().getObjects(
                x_, y_, false))
        //         target.getGame().getMaster().getObjects(x_, y_))
        {
            if (!obj.isTransparent()) {
                if (obj.isObstructing(source, target))
                    return true;
            }
        }
        if (checkWallObstruction(source, target, Coordinates.get(x_, y_))) {
            wallObstruction = true;
            return true;
        }
        return false;
    }

    public boolean check(Coordinates c, Coordinates c2) {
        Obj ce = game.getCell(c);
        if (ce == null) {
            return false;
        }
        Obj ce2 = game.getCell(c2);
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
        if (inLine(c1, c2)) {
            //            result = source.getGame().getVisionMaster().getVisionController().getDiagObstructMapper().
            //             getVar(source, target);

            result = noObstaclesInLine(source, target, game
                    .getGrid());
            toCheck = false;
            if (!result)
                return cacheResult(source, target, result);
        } else { // TODO TRANSPARENT FOR VISION!
            if (inLineDiagonally(c1, c2)) {
                result = noObstaclesInDiagonal(c1, c2, game
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
                    if (checkWallObstruction(source, target, c)) {
                        wallObstruction = true;
                        return cacheResult(source, target, false);
                    }
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
                    source.getGame().getCell(target.getCoordinates()), !result);
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

        boolean[][] array =
                new boolean[x - 1][y + 1];
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
                        if (!ClearshotMaster.checkClearShot(x, y, array)) {
                            return false;
                        }
                }
            }
        }
        return true;
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

    public boolean isVision() {
        return vision;
    }

    public void setVision(boolean vision) {
        this.vision = vision;
    }

}