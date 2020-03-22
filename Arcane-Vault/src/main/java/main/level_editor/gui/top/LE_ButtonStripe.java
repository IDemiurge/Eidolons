package main.level_editor.gui.top;

import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.level_editor.LevelEditor;

public class LE_ButtonStripe extends HorizontalFlowGroup {

    SmartButton controlPanel;
    SmartButton palettePanel;
    SmartButton structurePanel;
    SmartButton brushes;
    SmartButton viewModes;

    SmartButton undo;

    public LE_ButtonStripe() {
        super(10);
        setHeight(80);
        setWidth(900);
        addActor(undo = new SmartButton(ButtonStyled.STD_BUTTON.LE_UNDO, ()->
                LevelEditor.getCurrent().getManager().getOperationHandler().undo()));
//        addActor(redo = new SmartButton(ButtonStyled.STD_BUTTON.LE_REDO, ()->
//                LevelEditor.getCurrent().getManager().getOperationHandler().redo()));
        addActor(new TablePanelX<>(40, getHeight()));
        addActor(controlPanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_CTRL, null));
        addActor(palettePanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_PALETTE, null));
        addActor(structurePanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_STRUCT, null));
        addActor(brushes = new SmartButton(ButtonStyled.STD_BUTTON.LE_BRUSH, null));
        addActor(viewModes = new SmartButton(ButtonStyled.STD_BUTTON.LE_VIEWS, null));
    }

    public SmartButton getControlPanel() {
        return controlPanel;
    }

    public SmartButton getPalettePanel() {
        return palettePanel;
    }

    public SmartButton getStructurePanel() {
        return structurePanel;
    }

    public SmartButton getBrushes() {
        return brushes;
    }

    public SmartButton getViewModes() {
        return viewModes;
    }
}
