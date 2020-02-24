package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.grid.*;
import main.system.datatypes.DequeImpl;

public class LE_BfGrid extends GridPanel {


    public LE_BfGrid(int cols, int rows) {
        super(cols, rows);
    }

    protected GridUnitView doCreateUnitView(BattleFieldObject battleFieldObject) {
        return LE_UnitViewFactory.doCreate(battleFieldObject);
    }

    protected OverlayView doCreateOverlay(BattleFieldObject battleFieldObject) {
        return LE_UnitViewFactory.doCreateOverlay(battleFieldObject);
    }

    @Override
    protected GridCellContainer createGridCell(TextureRegion emptyImage, int x, int y) {
        return new LE_GridCell(emptyImage, x, y);
    }

    @Override
    public GridPanel init(DequeImpl<BattleFieldObject> objects) {
        return super.init(objects);
    }

}
