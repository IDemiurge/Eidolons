package main.level_editor.gui.panels.control.structure;

import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.structure.IStructureManager;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlStructurePanel extends LE_ControlPanel<IStructureManager> {


    public CtrlStructurePanel(  ) {
        super(IStructureManager.class,
                LevelEditor.getCurrent().getManager().getStructureManager());
    }

    @Override
    protected int getWrap() {
        return 4;
    }

    @Override
    protected float getSpace() {
        return 5;
    }
}
