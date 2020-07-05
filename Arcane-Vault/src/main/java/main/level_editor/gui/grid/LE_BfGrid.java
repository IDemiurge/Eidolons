package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.libgdx.bf.decor.DecorData;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.GridSubParts;
import eidolons.libgdx.bf.grid.cell.*;
import eidolons.libgdx.bf.grid.handlers.GridAnimHandler;
import eidolons.libgdx.bf.grid.moving.PlatformCell;
import eidolons.libgdx.bf.grid.moving.PlatformData;
import eidolons.libgdx.bf.grid.moving.PlatformDecor;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.texture.TextureCache;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.display.LE_DisplayMode;
import main.level_editor.backend.functions.advanced.LE_GridAnimTester;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.level_editor.backend.metadata.options.LE_Options;
import main.level_editor.backend.metadata.options.LE_OptionsMaster;
import main.system.GuiEventManager;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.MathMaster;

import java.util.List;

import static main.system.GuiEventType.*;

public class LE_BfGrid extends GridPanel {

    private final TextureRegion selectionBorder;
    GridAnimHandler gridAnimHandler = new LE_GridAnimTester(this);

    public LE_BfGrid(int cols, int rows, int moduleCols, int moduleRows) {
        super(cols, rows, moduleCols, moduleRows);
        selectionBorder = TextureCache.getOrCreateR(CellBorderManager.teamcolorPath);
    }

    @Override
    public int getModuleCols() {
        return getFullCols();
    }

    @Override
    public int getModuleRows() {
        return getFullRows();
    }

    @Override
    public int getGdxY_ForModule(int y) {
        return getFullRows() - y; // buffer?
    }

    @Override
    public boolean isDrawn(Coordinates c) {
        return super.isDrawn(c);
    }

    @Override
    protected int getDrawY(int y) {
        // return MathMaster.getMinMax(  y, 0, full_rows - 1);
        return MathMaster.getMinMax(full_rows - y, 0, full_rows - 1);
    }

    @Override
    protected boolean isCustomHit() {
        return false;
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }

    @Override
    public PlatformDecor addPlatform(List<PlatformCell> cells, PlatformData data, PlatformDecor visuals) {
        PlatformDecor platformDecor = super.addPlatform(cells, data, visuals);
        resetZIndices();
        return platformDecor;
    }

    @Override
    public void setVoid(int x, int y, boolean animated) {
        super.setVoid(x, y, animated); //can't have visible==false
    }

    @Override
    protected boolean isShadowMapOn() {
        return LE_OptionsMaster.getOptions_().getBooleanValue(LE_Options.EDITOR_OPTIONS.real_view_enabled);
    }

    @Override
    protected boolean isShardsOn() {
        return LE_OptionsMaster.getOptions_().getBooleanValue(LE_Options.EDITOR_OPTIONS.real_view_enabled);
    }

    @Override
    protected void resetVisible() {
        super.resetVisible();
    }

    @Override
    public void setModule(Module module) {
        x2 = full_cols;
        y2 = full_rows;
        GridSubParts container = new GridSubParts(full_cols, full_rows);
        viewMap = container.viewMap;
        customOverlayingObjects = container.customOverlayingObjects;
        customOverlayingObjectsTop = container.customOverlayingObjectsTop;
        customOverlayingObjectsUnder = container.customOverlayingObjectsUnder;
        emitterGroups = container.emitterGroups;
        gridObjects = container.gridObjects;
        manipulators = container.manipulators;
        overlays = container.overlays;
        //for others too?

        initModuleGrid();
        init = true;
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

    protected UnitGridView doCreateUnitView(BattleFieldObject battleFieldObject) {
        return LE_UnitViewFactory.doCreate(battleFieldObject);
    }

    protected OverlayView doCreateOverlay(BattleFieldObject battleFieldObject) {
        return LE_UnitViewFactory.doCreateOverlay(battleFieldObject, getColorFunction());
    }

    @Override
    protected GridCellContainer createGridCell(TextureRegion emptyImage, int x, int y) {
        return new LE_GridCell(emptyImage, x, y, coord -> getGridManager().getColor(coord));
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
                ((LE_UnitView) v).getAiLabel().setText((String) list.get(1));
            }
        });
        GuiEventManager.bind(LE_CELL_SCRIPTS_LABEL_UPDATE, obj -> {
            updateCellLabel((List) obj.get(), false);
        });
        GuiEventManager.bind(LE_CELL_AI_LABEL_UPDATE, obj -> {
            updateCellLabel((List) obj.get(), true);
        });
        GuiEventManager.bind(LE_DISPLAY_MODE_UPDATE, obj -> {
            LE_DisplayMode mode = (LE_DisplayMode) obj.get();
            for (GridCellContainer[] col : cells) {
                for (GridCellContainer container : col) {
                    if (container instanceof LE_GridCell) {
                        ((LE_GridCell) container).displayModeUpdated(mode);

                    }
                }
            }
        });
    }

    @Override
    protected void createDecor(Coordinates c, DecorData data) {
        super.createDecor(c, data);
        resetZIndices();
        updateCellLabel(c, data.getData(), null);
    }

    protected   EventListener createDecorListener(Coordinates c) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (LevelEditor.getManager().getLayer() == LE_Manager.LE_LAYER.decor) {
                    LevelEditor.getManager().getEditHandler().editCell(c);
                }
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);//TODO
            }
        };
    }

    private void updateCellLabel(List list, Boolean aiOrScriptsOrDecor) {
        Coordinates c = (Coordinates) list.get(0);
        String data = list.get(1).toString();
        updateCellLabel(c, data, aiOrScriptsOrDecor);
    }

    private void updateCellLabel(Coordinates c, String data, Boolean aiOrScriptsOrDecor) {
        GridCellContainer container = cells[c.x][(c.y)];
        if (aiOrScriptsOrDecor == null) {
            data = data.replace(";", StringMaster.NEW_LINE);
            ((LE_GridCell) container).getDecorLabel().setText(data);
        } else if (aiOrScriptsOrDecor) {
            ((LE_GridCell) container).getAiLabel().setText(data);
        } else {
            ((LE_GridCell) container).getScriptsLabel().setText(data);
        }

    }
}
