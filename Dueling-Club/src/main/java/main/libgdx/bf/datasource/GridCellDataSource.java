package main.libgdx.bf.datasource;

import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.UnitViewFactory;

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
                .map(el -> UnitViewFactory.createBaseView((BattleFieldObject) el))
                .collect(Collectors.toList());
    }

    @Override
    public int getTotalCorpsesCount() {
        return game.getGraveyardManager().getDeadUnits(cell).size();
    }
}
