package main.level_editor.gui.grid;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.bf.grid.UnitViewFactory;
import main.system.datatypes.DequeImpl;

public class LE_BfGrid extends GridPanel {
    
    public LE_BfGrid(int rows, int cols) {
        super(rows, cols);
    }
    protected GridUnitView doCreateUnitView(BattleFieldObject battleFieldObject) {
        return LE_UnitViewFactory.doCreate(battleFieldObject);
    }

    protected OverlayView doCreateOverlay(BattleFieldObject battleFieldObject) {
        return LE_UnitViewFactory.doCreateOverlay(battleFieldObject);
    }

    @Override
    public GridPanel init(DequeImpl<BattleFieldObject> objects) {
        return super.init(objects);
    }

}
