package main.level_editor.functions.advanced;

import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;
import main.level_editor.functions.selection.LE_Selection;
import main.system.auxiliary.data.ListMaster;

public class LE_AdvFuncs extends LE_Handler {

    public LE_AdvFuncs(LE_Manager manager) {
        super(manager);
    }

    public enum LE_ADV_FUNCS{
        REPLACE,
        CLEAR,

        MIRROR_VERT,
        MIRROR_HOR,
        MIRROR_AT,

        MIRROR_SQUARE,


    }


    public boolean doMirror(LE_ADV_FUNCS func) {
        LE_Selection selection = getModel().getSelection();
        switch (func) {
            case MIRROR_AT:
            case MIRROR_HOR:
            case MIRROR_VERT:
            case MIRROR_SQUARE:

        }
        if (!ListMaster.isNotEmpty(selection.getCoordinates())) {
            getSelectionHandler().selectCoordinate();
            getSelectionHandler().selectArea();
        }

        return false;
    }
}
