package libgdx.bf.datasource;

import eidolons.entity.obj.BattleFieldObject;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.UnitViewFactory;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static main.game.core.game.Game.game;

public class GridCellDataSource implements GraveyardDataSource {
    private final Coordinates cell;

    public GridCellDataSource(Coordinates cell) {
        this.cell = cell;
    }

    @Override
    public List<BaseView> getGraveyard() {
        final List<Obj> deadUnits = game.getGraveyardManager().getDeadUnits(cell);
        if (deadUnits == null) return new ArrayList<>();
        return deadUnits.stream()
         .limit(4)
         .map(el -> UnitViewFactory.createGraveyardView((BattleFieldObject) el))
         .collect(Collectors.toList());
    }

    @Override
    public int getTotalCorpsesCount() {
        return game.getGraveyardManager().getDeadUnits(cell).size();
    }
}
