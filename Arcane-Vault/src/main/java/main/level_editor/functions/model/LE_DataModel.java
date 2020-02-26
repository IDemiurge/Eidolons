package main.level_editor.functions.model;

import main.data.DataManager;
import main.level_editor.functions.brush.LE_Brush;
import main.level_editor.functions.brush.LE_BrushType;
import main.level_editor.functions.display.LE_DisplayMode;
import main.level_editor.functions.mouse.MouseMode;
import main.level_editor.functions.selection.LE_Selection;
import main.level_editor.functions.selection.PaletteSelection;

public class LE_DataModel {

    MouseMode mouseMode;
    LE_DisplayMode displayMode;
    LE_Selection selection;
    LE_Brush brush;
    PaletteSelection paletteSelection;

    public LE_DataModel() {
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
}
