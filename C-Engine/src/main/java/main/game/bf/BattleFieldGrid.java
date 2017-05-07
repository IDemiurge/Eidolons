package main.game.bf;

import main.entity.Entity;
import main.entity.obj.Obj;

import java.util.List;
import java.util.Set;

public interface BattleFieldGrid {


    // movement into action?

    void highlightsOff();

    boolean noObstaclesY(int y, int y1, int y2);

    boolean noObstaclesX(int x, int x1, int x2);

    List<Coordinates> getCoordinatesList();

    void highlight(Set<Obj> set);

    boolean noObstaclesOnDiagonal(Coordinates c1, Coordinates c2);

    int getHeight();

    int getWidth();

    Set<Obj> getCells();

    Obj getCell(Coordinates coordinates);

    boolean noObstaclesOnDiagonal(Coordinates c1, Coordinates c2, Obj source);

    boolean noObstaclesX(int x, int x1, int x2, Obj source);

    boolean noObstaclesY(int y, int y1, int y2, Obj source);

    int getZ();

    void wheelRotates(int rotations, boolean alt);

    void manualOffsetReset();

    void setCameraCenterCoordinates(Coordinates coordinates);

    Boolean isOnEdgeX(Coordinates coordinates);

    Boolean isOnEdgeY(Coordinates coordinates);

    Obj getObjOrCell(Coordinates c);

    Obj getTopObj(Coordinates c);

    boolean isOccupied(Coordinates c);

    Obj getObj(Coordinates c1);

    boolean canMoveOnto(Entity obj, Coordinates c);

    void addUnitObj(Obj targetObj);
}
