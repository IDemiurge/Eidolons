package libgdx.bf.grid.handlers;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.puzzle.gridobj.GridObject;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.GridCell;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.bf.grid.cell.UnitView;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

public abstract class GridHandler {

    protected GridPanel grid;

    public GridHandler(GridPanel grid) {
        this.grid = grid;
        bindEvents();
    }

    public GridManager getManager() {
        return grid.getGridManager();
    }

    protected void bindEvents() {
    }

    public ObjectMap<Obj, BaseView> getViewMap() {
        return grid.getViewMap();
    }

    protected UnitView getUnitView(BattleFieldObject unit) {
        return (UnitView) getViewMap().get(unit);
    }

    protected BaseView getView(Obj unit) {
        return getViewMap().get(unit);
    }

    protected GridObject findGridObj(String k, Coordinates c) {
        return grid.findGridObj(k, c);
    }

    public GridCellContainer[][] getCells() {
        return grid.getCells();
    }

    public DC_Cell getCell(int i, int i1) {
        return grid.getCell(i, i1);
    }

    public int getFullCols() {
        return grid.getFullCols();
    }

    public int getFullRows() {
        return grid.getFullRows();
    }

    public int getGdxY_ForModule(int y) {
        return grid.getGdxY_ForModule(y);
    }

    public GridCellContainer getGridCell(int x, int y) {
        return grid.getGridCell(x, y);
    }

    public GridCell getGridCell(Coordinates coordinate) {
        return grid.getGridCell(coordinate);
    }

    public int getX1() {
        return grid.getX1();
    }

    public int getX2() {
        return grid.getX2();
    }

    public int getY1() {
        return grid.getY1();
    }

    public int getY2() {
        return grid.getY2();
    }

    public void afterLoaded() {

    }
}
