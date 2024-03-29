package main.level_editor.backend.handlers.model;

import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.game.exploration.dungeon.struct.LevelZone;
import main.data.tree.LayeredData;
import main.data.tree.StructNode;
import main.data.tree.StructTreeBuilder;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.level_editor.backend.brush.BrushShape;
import main.level_editor.backend.brush.LE_Brush;
import main.level_editor.backend.brush.LE_BrushType;
import main.level_editor.backend.display.LE_DisplayMode;
import main.level_editor.backend.functions.mouse.MouseMode;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.level_editor.backend.handlers.selection.PaletteSelection;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.CloneMaster;

public class EditorModel {

    private MouseMode mouseMode;
    private LE_DisplayMode displayMode;
    private LE_Selection selection;
    private LE_Brush brush;
    private PaletteSelection paletteSelection;

    private StructNode treeModel;
    private LevelZone zone;
    private Module module;
    private LevelBlock block;
    private Layer layer;
    private ObjType defaultWallType;
    private boolean brushMode;
    private LevelStruct lastSelectedStruct;
    private boolean appendMode;

    public EditorModel() {
        selection = new LE_Selection();
        displayMode = new LE_DisplayMode();
        brush = new LE_Brush(BrushShape.single, LE_BrushType.none);
    }

    public EditorModel(EditorModel model) {
        copy(model);
    }


    private void copy(EditorModel model) {
        selection = (LE_Selection) CloneMaster.deepCopy(model.getSelection());
        if (selection == null) {
            selection = new LE_Selection();
        }
        displayMode = (LE_DisplayMode) CloneMaster.deepCopy(model.getDisplayMode());
        paletteSelection = (PaletteSelection) CloneMaster.deepCopy(model.getPaletteSelection());
        brush = model.getBrush();
        brushMode = model.brushMode ;
        appendMode = model.appendMode ;
    }

    public StructNode getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(LayeredData data) {
        this.treeModel = new StructTreeBuilder(data).getRoot();

        GuiEventManager.trigger(GuiEventType.LE_TREE_RESET, treeModel);
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public LE_Brush getBrush() {
        return brush;
    }

    public void setMouseMode(MouseMode mouseMode) {
        this.mouseMode = mouseMode;
    }

    public LE_DisplayMode getDisplayMode() {
        if (displayMode == null) {
            displayMode = new LE_DisplayMode();
        }
        return displayMode;
    }

    public LE_Selection getSelection() {
        return selection;
    }

    public void setSelection(LE_Selection selection) {
        if (selection == null) {
            return ;
        }
        this.selection = selection;
    }

    public PaletteSelection getPaletteSelection() {
        return PaletteSelection.getInstance();
    }

    public void setPaletteSelection(PaletteSelection paletteSelection) {
        this.paletteSelection = paletteSelection;
    }

    public LevelZone getZone() {
        if (zone == null) {
            return getModule().getZones().get(0);
        }
        return zone;
    }

    public void setZone(LevelZone zone) {
        this.zone = zone;
    }

    public Module getModule() {
        if (module == null) {
            if (getLastSelectedStruct() == null) {
                return LevelEditor.getCurrent().getDefaultModule();
            }
            return getLastSelectedStruct().getModule();
        }
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public LevelBlock getBlock() {
        return block;
    }

    public void setBlock(LevelBlock block) {
        this.block = block;
    }

    public boolean isBrushMode() {
        return brushMode;
    }

    public void setBrushMode(boolean brushMode) {
        this.brushMode = brushMode;
    }

    public void setLastSelectedStruct(LevelStruct lastSelectedStruct) {
        this.lastSelectedStruct = lastSelectedStruct;
    }

    public LevelStruct getLastSelectedStruct() {
        return lastSelectedStruct;
    }


    public boolean isAppendMode() {
        return appendMode;
    }

    public void setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
    }

    public void toggleAppend() {
        appendMode = !appendMode;
    }
}
