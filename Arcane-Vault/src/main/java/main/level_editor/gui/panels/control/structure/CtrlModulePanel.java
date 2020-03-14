package main.level_editor.gui.panels.control.structure;

import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.mapping.IModuleHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlModulePanel extends LE_ControlPanel<IModuleHandler> {

    public CtrlModulePanel() {
        super(IModuleHandler.class, LevelEditor.getCurrent().getManager().getModuleHandler());
    }

    @Override
    protected int getWrap() {
        return 3;
    }

    @Override
    protected float getSpace() {
        return 2;
    }
}
