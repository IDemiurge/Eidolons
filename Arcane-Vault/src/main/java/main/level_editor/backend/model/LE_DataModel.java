package main.level_editor.backend.model;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.level_editor.backend.brush.LE_Brush;
import main.level_editor.backend.display.LE_DisplayMode;
import main.level_editor.backend.mouse.MouseMode;
import main.level_editor.backend.selection.LE_Selection;
import main.level_editor.backend.selection.PaletteSelection;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_DataModel {

    MouseMode mouseMode;
    LE_DisplayMode displayMode;
    LE_Selection selection;
    LE_Brush brush;
    PaletteSelection paletteSelection;

    LE_TreeModel treeModel;
    private LevelZone currentZone;
    private Module module;
    private LevelBlock block;

    public LE_DataModel() {
    }

    public LE_TreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(LE_TreeModel treeModel) {
        this.treeModel = treeModel;
        GuiEventManager.trigger(GuiEventType.LE_TREE_RESET, treeModel);
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(MouseMode mouseMode) {
        this.mouseMode = mouseMode;
    }

    public LE_DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(LE_DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public LE_Selection getSelection() {
        return selection;
    }

    public void setSelection(LE_Selection selection) {
        this.selection = selection;
    }

    public PaletteSelection getPaletteSelection() {
        return paletteSelection;
    }

    public void setPaletteSelection(PaletteSelection paletteSelection) {
        this.paletteSelection = paletteSelection;
    }

    public LevelZone getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(LevelZone currentZone) {
        this.currentZone = currentZone;
    }

    public Module getModule() {
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
}
