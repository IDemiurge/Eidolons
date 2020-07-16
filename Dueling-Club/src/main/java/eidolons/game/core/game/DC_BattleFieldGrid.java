package eidolons.game.core.game;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.CONTENT_CONSTS;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.system.launch.CoreEngine;
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
    private final Set<Coordinates> coordinates;
    private final Set<DC_Cell> cellsSet;
    DC_Cell[][] cells;
    private final Set<Module> modules = new LinkedHashSet<>();
    private final Boolean[][] wallCache;
    private int x1;
    private int y1;

    public DC_BattleFieldGrid(Module module) {
        game = DC_Game.game;
        this.w = this.game.getDungeonMaster().getFloorWrapper().getWidth();
        this.h = this.game.getDungeonMaster().getFloorWrapper().getHeight();
        coordinates = new LinkedHashSet<>();
        cellsSet = new LinkedHashSet<>();
        cells = new DC_Cell[w][h];
        wallCache = new Boolean[w][h];
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
        if (!CoreEngine.isLevelEditor()) {
            this.w = this.module.getEffectiveWidth(false);
            this.h = this.module.getEffectiveHeight(false);
            this.x1 = this.module.getX();
            this.y1 = this.module.getY();
        }
        Set<Coordinates> inner = module.initCoordinateSet(false);
        if (!modules.contains(module)) {
            for (int i = module.getX(); i-module.getX()  < this.module.getEffectiveWidth(true); i++) {
                for (int j = module.getY(); j-module.getY()  < this.module.getEffectiveHeight(true); j++) {
                    Coordinates o = Coordinates.get(i, j);
                    DC_Cell cell = cells[i][j];
                    cell.setModule(module);
                    if (!inner.contains(o)  ) {
                        cell.setVOID(true);
                    } else
                    if ( cell.getMarks().contains(CONTENT_CONSTS.MARK._void)  ) {
                        cell.setVOID(true);
                    } else
                    if (this.module.getVoidCells().contains(o)) {
                        cell.setVOID(true);
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
        BattleFieldObject[] objects = cells[x_][y_].getObjects(overlayingIncluded_Not_Only);
        if (objects == null) {
            Set<BattleFieldObject> set = DC_Game.game.getObjMaster().getObjectsOnCoordinate(
                    Coordinates.get(x_, y_), overlayingIncluded_Not_Only);
            if (set.isEmpty())
                objects = new BattleFieldObject[0];
            else
                objects = set.toArray(new BattleFieldObject[0]);
            cells[x_][y_].setObjects(objects, overlayingIncluded_Not_Only);
        } else
            return objects;
        return objects;
    }


    public void resetObjCells() {
        for (int i = module.getX(); i-module.getX()  < this.module.getEffectiveWidth(true); i++) {
            for (int j = module.getY(); j-module.getY()  < this.module.getEffectiveHeight(true); j++) {
                    DC_Cell cell = cells[i][j];
                if (cell.isObjectsModified() || (!CoreEngine.isWeakCpu()&&!CoreEngine.isWeakGpu())
                || !ExplorationMaster.isExplorationOn())
                    //TODO CORE Review
                {
                    cell.resetObjectArrays();
                    cell.setObjectsModified(false);
                }
            }
        }
    }


    public boolean isWallCoordinate(Coordinates coordinates) {
        Boolean result = wallCache[coordinates.x][coordinates.y];
        if (result != null) {
            return result;
        }
        result = false;
        for (BattleFieldObject o : getObjects(coordinates.x, coordinates.y)) {
            result = o.isWall();
            wallCache[coordinates.x][coordinates.y] = result;
            if (result)
                break;
        }
        return result;
    }

    public Boolean[][] getWallCache() {
        return wallCache;
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
            BattleFieldObject[] objects = getObjects(X, Y, false);
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
            Set<BattleFieldObject> objects = game.getObjMaster().getObjectsOnCoordinate(c, false );
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
    public int getHeight() {
        return h;
    }

    @Override
    public int getWidth() {
        return w;
    }


    public DC_Game getGame() {
        return game;
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

    public DC_Cell[][] getCells() {
        return cells;
    }

    public Set<DC_Cell> getCellsSet() {
        return cellsSet;
    }

    public Coordinates getModuleCoordinates(int x, int y) {
        return Coordinates.get(x+this.x1, y+this.y1);
    }
}
