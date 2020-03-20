package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.grid.*;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.texture.TextureCache;
import main.entity.obj.Obj;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.GuiEventManager;
import main.system.datatypes.DequeImpl;

import static main.system.GuiEventType.LE_SELECTION_CHANGED;
import static main.system.GuiEventType.UPDATE_GUI;

public class LE_BfGrid extends GridPanel {

    private final TextureRegion selectionBorder;

    public LE_BfGrid(int cols, int rows) {
        super(cols, rows);
        selectionBorder = TextureCache.getOrCreateR(CellBorderManager.teamcolorPath);
    }

    @Override
    protected void resetVisible() {
        super.resetVisible();
        for (BattleFieldObject battleFieldObject : viewMap.keySet()) {
            //check layers and modules
        }
    }

    @Override
    protected GridOverlaysManager createOverlays() {
        return new LE_GridOverlays(this);
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
        super.init(objects);
        addActor(overlayManager = createOverlays());
        return this;
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(LE_SELECTION_CHANGED, obj -> {
            LE_Selection selection = (LE_Selection) obj.get();
            for (BaseView value : viewMap.values()) {
                value.setBorder(null);
            }
            for (Integer id : selection.getIds()) {
                Obj object = LevelEditor.getManager().getIdManager().getObjectById(id);
                UnitView view = getUnitView((BattleFieldObject) object);
                view.setBorder(selectionBorder);


            }
        });

        GuiEventManager.bind(UPDATE_GUI, obj -> {
            resetVisibleRequired = true;
        });
    }
}
