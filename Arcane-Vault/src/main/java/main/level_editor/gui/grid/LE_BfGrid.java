package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.libgdx.bf.grid.*;
import main.system.GuiEventManager;
import main.system.datatypes.DequeImpl;

import static main.system.GuiEventType.UPDATE_GUI;

public class LE_BfGrid extends GridPanel {


    public LE_BfGrid(int cols, int rows) {
        super(cols, rows);
    }

    @Override
    protected void resetVisible() {
        super.resetVisible();
        for (BattleFieldObject battleFieldObject : viewMap.keySet()) {
            //check layers and modules
        }
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

    @Override
    protected void bindEvents() {
        super.bindEvents();

        GuiEventManager.bind(UPDATE_GUI, obj -> {
            resetVisibleRequired = true;
        });
    }
}
