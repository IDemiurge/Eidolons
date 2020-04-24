package eidolons.game.core.game;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.system.math.PositionMaster;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * pass down BF_GRID to this COMPONENT from Map Generator
 *
 * @author Regulus
 */
public class DC_BattleFieldGrid implements BattleFieldGrid {

    private final DC_Game game;
    private int h;
    private int w;
    private Module module;
    private Set<Coordinates> coordinates;
    private LinkedHashSet<DC_Cell> cellsSet;
    DC_Cell[][] cells;
    private BattleFieldObject[][][] objCellsNoOverlaying;
    private BattleFieldObject[][][] objCellsOverlaying;
    private BattleFieldObject[][][] objCellsAll;
    private Set<Module> modules = new LinkedHashSet<>();

    public DC_BattleFieldGrid(Module module) {
        game = DC_Game.game;
        this.w = this.game.getDungeonMaster().getDungeonWrapper().getWidth();
        this.h = this.game.getDungeonMaster().getDungeonWrapper().getHeight();
        coordinates = new LinkedHashSet<>();
        cellsSet = new LinkedHashSet<>();
        cells = new DC_Cell[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                coordinates.add(Coordinates.get(i, j));
                cellsSet.add(cells[i][j] = new DC_Cell(i, j, game));
            }
        }
        setModule(module);

    }

    public void setModule(Module module) {
        this.module = module;
        this.w = this.module.getWidth();
        this.h = this.module.getHeight();
        Set<Coordinates> inner = module.initCoordinateSet(false);
        if (!modules.contains(module)) {
            for (int i = 0; i < this.module.getEffectiveWidth(true); i++) {
                for (int j = 0; j < this.module.getEffectiveHeight(true); j++) {
                    Coordinates o = Coordinates.get(i, j);
                    if (!inner.contains(o)) {
                        cells[i][j].setVOID(true);
                    } else
                    if (this.module.getVoidCells().contains(o)) {
                        cells[i][j].setVOID(true);
                    }
                }
            }
            modules.add(module);
        }
        resetObjCells();
    }

    public BattleFieldObject[] getObjects(int x_, int y_) {
        return getObjects(x_, y_, true);
    }

    public BattleFieldObject[] getObjects(int x_, int y_, Boolean overlayingIncluded_Not_Only) {
        BattleFieldObject[] array = getObjCells()[x_][y_];
        if (array == null) {
            Set<BattleFieldObject> set = DC_Game.game.getMaster().getObjectsOnCoordinate(
                    Coordinates.get(x_, y_), false);
//            list.addAll(
//            game.getMaster().getObjectsOnCoordinate(
//             Coordinates.getVar(x_, y_), true));

            if (set.isEmpty())
                array = new BattleFieldObject[0];
            else
                array = set.toArray(new BattleFieldObject[0]);
            getObjCells()[x_][y_] = array;
        }
        return array;
    }

    public BattleFieldObject[][][] getObjCells(Boolean overlayingIncluded_Not_Only) {
        if (overlayingIncluded_Not_Only == null)
            return objCellsOverlaying;
        return overlayingIncluded_Not_Only ? objCellsAll : objCellsNoOverlaying;
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
            Coordinates c = Coordinates.get(X, Y);
            Set<BattleFieldObject> objects = game.getMaster().
                    getObjectsOnCoordinate(c, false);
            for (BattleFieldObject obj : objects) {
                if (obj.isObstructing(source, game.getCellByCoordinate(c))) {
                    return false;
                }
            }

        }
        return true;

    }

    /**
     * @param xy     source x or y
     * @param xy1    line start
     * @param xy2    line end
     * @param source obj
     * @param x_y
     * @return
     */
    private boolean noObstacles(int xy, int xy1, int xy2, Obj source, boolean x_y) {

        int max = xy2;
        int min = xy1;
        if (xy1 > xy2) {
            max = xy1;
            min = xy2;
        }
        for (int i = min + 1; i < max; i++) {
            Coordinates c = (x_y) ? Coordinates.get(xy, i) : Coordinates.get(i, xy);
            Set<BattleFieldObject> objects = game.getMaster().getObjectsOnCoordinate(c, false, false, false);
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

    public DC_Game getGame() {
        return game;
    }


    @Override
    public boolean canMoveOnto(Entity obj, Coordinates c) {
        return game.getRules().getStackingRule().canBeMovedOnto(obj,
                c, null);
    }


    public DC_Cell getCell(int x, int y) {
        try {
            return cells[x][y];
        } catch (Exception e) {
            return null;
        }
    }

    public DC_Cell getCell(Coordinates coordinates) {
//        coordinates = coordinates.getOffset(game.getModule().getOrigin().negative());
        try {
            return cells[coordinates.x][coordinates.y];
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return null;
    }

    public Set<Coordinates> getCoordinatesSet() {
        return coordinates;
    }

    public LinkedHashSet<DC_Cell> getCellsSet() {
        return cellsSet;
    }

}
