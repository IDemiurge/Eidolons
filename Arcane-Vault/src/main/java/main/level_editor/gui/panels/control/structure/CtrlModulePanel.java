package main.level_editor.gui.panels.control.structure;

import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.mapping.IModuleHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlModulePanel extends LE_ControlPanel<IModuleHandler> {

    public CtrlModulePanel() {
        super();
    }

    @Override
    protected IModuleHandler getHandler() {
        return LevelEditor.getCurrent().getManager().getModuleHandler();
    }

    @Override
    protected Class<IModuleHandler> getClazz() {
        return IModuleHandler.class;
    }

    @Override
    protected int getWrap() {
        return 5;
    }

    @Override
    protected float getSpace() {
        return 2;
    }
}
