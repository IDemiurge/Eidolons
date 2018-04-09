package main.game.bf;

import main.entity.Entity;
import main.entity.obj.Obj;

import java.util.Set;

public interface BattleFieldGrid {
    int getHeight();

    int getWidth();

    int getZ();

    Obj getTopObj(Coordinates c);

    boolean isOccupied(Coordinates c);

    Obj getObj(Coordinates c1);

    boolean canMoveOnto(Entity obj, Coordinates c);

    void addUnitObj(Obj targetObj);

    boolean noObstaclesY(int y, int y1, int y2);

    boolean noObstaclesX(int x, int x1, int x2);

    boolean noObstaclesOnDiagonal(Coordinates c1, Coordinates c2, Obj source);

    boolean noObstaclesX(int x, int x1, int x2, Obj source);

    boolean noObstaclesY(int y, int y1, int y2, Obj source);

    Set<Coordinates> getCoordinatesList();

    Obj getCell(Coordinates c1);
}
