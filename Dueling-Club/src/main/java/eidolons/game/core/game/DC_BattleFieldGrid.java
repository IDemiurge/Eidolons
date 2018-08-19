package eidolons.game.core.game;

import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.location.building.DC_Map;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.system.math.PositionMaster;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * pass down BF_GRID to this COMPONENT from Map Generator
 *
 * @author Regulus
 */
public class DC_BattleFieldGrid implements BattleFieldGrid {

    private DC_Game game;
    private DC_Map map;
    private int h;
    private int w;
    private Dungeon dungeon;
    private Set<Coordinates> coordinates;
    private LinkedHashSet<DC_Cell> cellsSet;
    DC_Cell[][] cells;
    private BattleFieldObject[][][] objCellsNoOverlaying;
    private BattleFieldObject[][][] objCellsOverlaying;
    private BattleFieldObject[][][] objCellsAll;

    public DC_BattleFieldGrid(Dungeon dungeon) {
        this.dungeon = dungeon; // TODO

        this.game = dungeon.getGame();
        this.w = dungeon.getWidth();
        this.h = dungeon.getHeight();
        coordinates = new LinkedHashSet<>();
        cellsSet = new LinkedHashSet<>();
        cells = new DC_Cell[w][h];
        resetObjCells();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (game.getMetaMaster().getDungeonMaster().getDungeonLevel() != null) {
                    if (game.getMetaMaster().getDungeonMaster().getDungeonLevel().isVoid(i, j))
                        continue;
                }
                cellsSet.add(cells[i][j] = new DC_Cell( i, j, game));
                coordinates.add(new Coordinates(i, j));
            }
        }

    }



    public BattleFieldObject[] getObjects(int x_, int y_) {
            return getObjects(x_, y_, true);
        }

    public BattleFieldObject[] getObjects(int x_, int y_, Boolean overlayingIncluded_Not_Only) {
        BattleFieldObject[] array = getObjCells( )[x_][y_];
        if (array == null) {
            List<BattleFieldObject> list =game.getMaster().getObjectsOnCoordinate(
             new Coordinates(x_, y_), null );
//            list.addAll(
//            game.getMaster().getObjectsOnCoordinate(
//             new Coordinates(x_, y_), true));

            if (list.isEmpty())
                array = new BattleFieldObject[0];
            else
                array = list.toArray(new BattleFieldObject[list.size()]);
            getObjCells( )[x_][y_] = array;
        }
        return array;
    }
    public BattleFieldObject[][][] getObjCells(Boolean overlayingIncluded_Not_Only) {
        if (overlayingIncluded_Not_Only==null )
            return objCellsOverlaying;
        return overlayingIncluded_Not_Only? objCellsAll:objCellsNoOverlaying;
    }

    public void resetObjCells() {
        objCellsNoOverlaying = new BattleFieldObject[w][h][];
        objCellsOverlaying = new BattleFieldObject[w][h][];
        objCellsAll = new BattleFieldObject[w][h][];
    }
    public BattleFieldObject[][][] getObjCells() {
        return objCellsNoOverlaying;
    }


    @Override
    public int getZ() {
        return 0;
    }

    @Override
    public void addUnitObj(Obj targetObj) {
        //TODO
    }
    @Override
    public Obj getTopObj(Coordinates c) {
        return null;
    }

    @Override
    public boolean isOccupied(Coordinates c) {
        return false;

    }

    public boolean isCoordinateObstructed(Coordinates c) {
        return isOccupied(c);
    }

    public DC_Map getMap() {
        if (map == null) {
            map = new DC_Map();
            map.setBackground(dungeon.getProperty(PROPS.MAP_BACKGROUND));
        }
        return map;
    }

    public void setMap(DC_Map map1) {
        map = map1;
    }

    @Override
    public boolean noObstaclesOnDiagonal(Coordinates c1, Coordinates c2, Obj source) {
        boolean above = PositionMaster.isAbove(c1, c2);
        boolean left = PositionMaster.isToTheLeft(c1, c2);
        int X = c1.x;
        int Y = c1.y;
        while (X != c2.x || Y != c2.y) {

            if (above) {
                Y++;
            } else {
                Y--;
            }
            if (left) {
                X++;
            } else {
                X--;
            }
            if (X == c2.x || Y == c2.y) {
                break;
            }
            Coordinates c = new Coordinates(X, Y);
            List<BattleFieldObject> objects = game.getMaster().
             getObjectsOnCoordinate(c, false);
            for (BattleFieldObject obj : objects) {
                if (obj.isObstructing(source, game.getCellByCoordinate(c))) {
                    return false;
                }
            }

        }
        return true;

    }

    private boolean noObstacles(int xy, int xy1, int xy2, Obj source, boolean x_y) {

        int max = xy2;
        int min = xy1;
        if (xy1 > xy2) {
            max = xy1;
            min = xy2;
        }
        for (int i = min + 1; i < max; i++) {
            Coordinates c = (x_y) ? new Coordinates(xy, i) : new Coordinates(i, xy);
            List<BattleFieldObject> objects = game.getMaster().getObjectsOnCoordinate(getZ(), c, false, false, false);
            for (BattleFieldObject obj : objects) {
                if (obj.isObstructing(source, game.getCellByCoordinate(c))) {
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public boolean noObstaclesX(int x, int x1, int x2, Obj objComponent) {
        return noObstacles(x, x1, x2, objComponent, true);
    }

    @Override
    public boolean noObstaclesY(int y, int y1, int y2, Obj objComponent) {
        return noObstacles(y, y1, y2, objComponent, false);
    }

    @Override
    public Set<Coordinates> getCoordinatesList() {
        return coordinates;
    }

    @Override
    public boolean noObstaclesY(int y, int y1, int y2) {
        return noObstaclesY(y, y1, y2, null);
    }

    @Override
    public boolean noObstaclesX(int x, int x1, int x2) {
        return noObstaclesX(x, x1, x2, null);
    }


    @Override
    public int getHeight() {
        return h;
    }

    @Override
    public int getWidth() {
        return w;
    }

    public Obj getObj(Coordinates c) {
        return getTopObj(c);
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public DC_Game getGame() {
        return game;
    }


    @Override
    public boolean canMoveOnto(Entity obj, Coordinates c) {
        return game.getRules().getStackingRule().canBeMovedOnto(obj,
         c, dungeon.getZ(), null);
    }


    public DC_Cell getCell(Coordinates coordinates) {
        return cells[coordinates.x][coordinates.y];
    }

    public Set<Coordinates> getCoordinatesSet() {
        return coordinates;
    }

    public LinkedHashSet<DC_Cell> getCellsSet() {
        return cellsSet;
    }

}
