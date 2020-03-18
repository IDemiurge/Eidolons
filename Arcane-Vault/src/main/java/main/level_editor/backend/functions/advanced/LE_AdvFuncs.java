package main.level_editor.backend.functions.advanced;

import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.auxiliary.data.ListMaster;

public class LE_AdvFuncs extends LE_Handler implements IAdvFuncs{

    public LE_AdvFuncs(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void fill() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void setVoid() {

    }

    public void mirror( ) {
        LE_Selection selection = getModel().getSelection();
        if (!ListMaster.isNotEmpty(selection.getCoordinates())) {
            getSelectionHandler().selectCoordinate();
            getSelectionHandler().selectArea();
        }

    }

    @Override
    public void rotate() {

    }

    @Override
    public void repeat() {

    }
}
