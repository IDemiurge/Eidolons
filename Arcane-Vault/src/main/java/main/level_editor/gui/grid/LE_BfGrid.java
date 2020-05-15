package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.GridSubParts;
import eidolons.libgdx.bf.grid.GridViewAnimator;
import eidolons.libgdx.bf.grid.cell.*;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.texture.TextureCache;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.advanced.LE_GridAnimTester;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.GuiEventManager;
import main.system.datatypes.DequeImpl;

import java.util.List;

import static main.system.GuiEventType.*;

public class LE_BfGrid extends GridPanel {

    private final TextureRegion selectionBorder;
    GridViewAnimator gridViewAnimator = new LE_GridAnimTester(this);

    public LE_BfGrid(int cols, int rows, int moduleCols, int moduleRows) {
        super(cols, rows, moduleCols, moduleRows);
        selectionBorder = TextureCache.getOrCreateR(CellBorderManager.teamcolorPath);

    }

    @Override
    public void setVoid(int x, int y, boolean animated) {
        super.setVoid(x, y, true); //can't have visible==false
    }

    @Override
    protected boolean isShadowMapOn() {
        return false;
    }

    @Override
    protected void resetVisible() {
        super.resetVisible();
    }

    @Override
    protected void addVoidDecorators(boolean hasVoid) {
        super.addVoidDecorators(false);
    }

    @Override
    public void setModule(Module module) {
        x2 = full_cols;
        y2 = full_rows;
        GridSubParts container = new GridSubParts();
        viewMap = container.viewMap;
        customOverlayingObjectsUnder = container.customOverlayingObjects;
        customOverlayingObjectsTop = container.customOverlayingObjectsTop;
        customOverlayingObjectsUnder = container.customOverlayingObjectsUnder;
        emitterGroups = container.emitterGroups;
        gridObjects = container.gridObjects;
        manipulators = container.manipulators;
        overlays = container.overlays;
        //for others too?

        initModuleGrid();

        //visual split
//        for (int x = 0; x <  getWidth(); x++) {
//            for (int y = 0; y < getHeight(); y++) {
//                checkAddBorder(x, y);
//            }
//        }
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
    public GridPanel initObjects(DequeImpl<BattleFieldObject> objects) {
        super.initObjects(objects);
        addActor(overlayManager = createOverlays());
        return this;
    }

    @Override
    protected boolean isVisibleByDefault(BattleFieldObject battleFieldObject) {
        return true;
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(LE_SELECTION_CHANGED, obj -> {
            LE_Selection selection = (LE_Selection) obj.get();
            for (BaseView value : viewMap.values()) {
                value.setBorder(TextureCache.getOrCreateR(TextureCache.getEmptyPath()));
            }
            for (Integer id : selection.getIds()) {
                Obj object = LevelEditor.getManager().getIdManager().getObjectById(id);
                BaseView view = getUnitView((BattleFieldObject) object);
                if (view == null) {
                    view = getOverlay(object);
                }
                view.setBorder(selectionBorder);


            }
        });

        GuiEventManager.bind(UPDATE_GUI, obj -> {
            resetVisibleRequired = true;
        });
        GuiEventManager.bind(LE_AI_DATA_UPDATE, obj -> {
            List list = (List) obj.get();
            UnitView v = getUnitView((BattleFieldObject) list.get(0));
            if (v instanceof LE_UnitView) {
                ((LE_UnitView) v).getAiLabel().setText((String) list.get(1) );
            }
        });
        GuiEventManager.bind(LE_CELL_SCRIPTS_LABEL_UPDATE, obj -> {
            updateCellLabel( (List) obj.get(), false);
        });
        GuiEventManager.bind(LE_CELL_AI_LABEL_UPDATE, obj -> {
            updateCellLabel( (List) obj.get(), true);
        });
            GuiEventManager.bind(LE_DISPLAY_MODE_UPDATE, obj -> {
            for (GridCellContainer[] col : cells) {
                for (GridCellContainer container : col) {
                    if (container instanceof LE_GridCell) {
                        ((LE_GridCell) container).displayModeUpdated();

                    }
                }
            }
        });
    }


    private void updateCellLabel(List list, boolean aiOrScripts) {
        Coordinates c = (Coordinates) list.get(0);
        String data = (String) list.get(1);
        GridCellContainer container = cells[c.x][ (c.y)];
        if (aiOrScripts) {
            ((LE_GridCell) container).getAiLabel().setText(data);
        } else {
            ((LE_GridCell) container).getScriptsLabel().setText(data);
        }

    }
}
