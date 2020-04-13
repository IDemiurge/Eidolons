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
    SmartButton save;

    SmartButton undo;

    public LE_ButtonStripe() {
        super(10);
        setHeight(80);
        setWidth(900);
        addActor(undo = new SmartButton(ButtonStyled.STD_BUTTON.LE_UNDO, ()->
                LevelEditor.getCurrent().getManager().getOperationHandler().undo()));
        addActor(save = new SmartButton(ButtonStyled.STD_BUTTON.LE_REDO, ()->
                LevelEditor.getCurrent().getManager().getDataHandler().saveFloor()));
        addActor(new TablePanelX<>(40, getHeight()));
        addActor(controlPanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_CTRL, null));
        addActor(palettePanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_PALETTE, null));
        addActor(structurePanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_STRUCT, null));

        addActor(brushes = new SmartButton(ButtonStyled.STD_BUTTON.LE_BRUSH, ()->{
            boolean b = LevelEditor.getModel().isBrushMode();
            LevelEditor.getModel().setBrushMode(!b);
            brushes.setChecked(b);
//            if (getStage() instanceof LE_GuiStage) {
//                ((LE_GuiStage) getStage()).toggleUiVisible();
//            }
        }));
        addActor(viewModes = new SmartButton(ButtonStyled.STD_BUTTON.LE_VIEWS, ()-> {
            LevelEditor.getModel().getDisplayMode().toggleAll();
        }));
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
