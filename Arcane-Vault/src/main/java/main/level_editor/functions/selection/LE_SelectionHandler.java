package main.level_editor.functions.selection;

import main.game.bf.Coordinates;
import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;
import main.level_editor.functions.mouse.LE_MouseHandler;
import main.system.threading.WaitMaster;

import java.util.Set;

public class LE_SelectionHandler extends LE_Handler {

    LE_Selection previousSelection;
    LE_Selection selection;
    private SELECTION_MODE mode;

    public LE_SelectionHandler(LE_Manager manager) {
        super(manager);
    }

    public void previousSelection(){
        if (previousSelection == null) {
            return;
        }
        setSelection(getPreviousSelection());
        previousSelection=null;
    }

    public LE_Selection getPreviousSelection() {
        return previousSelection;
    }

    public LE_Selection getSelection() {
        return selection;
    }

    public void setSelection(LE_Selection selection) {
        previousSelection = this.selection;
        this.selection = selection;
    }

    public Set<Coordinates> selectArea() {
        return null;
    }

    public enum SELECTION_MODE{
        NONE,
        COORDINATE,
        OBJECT,
        AREA,
        ;
    }

    public Coordinates selectCoordinate() {
        mode=SELECTION_MODE.COORDINATE;
        Coordinates  c = (Coordinates) WaitMaster.waitForInput(LE_MouseHandler.SELECTION_OPERATION);
        downgradeMode();
        return c;
    }

    private void downgradeMode() {
        if (mode == null) {
            return;
        }
        if (mode== SELECTION_MODE.NONE) {
            return;
        }

        switch (mode) {
            case OBJECT:
            case COORDINATE:
                mode = SELECTION_MODE.NONE;
                return;
            case AREA:
                mode = SELECTION_MODE.COORDINATE;
                return;
        }

    }

    public SELECTION_MODE getMode() {
        return mode;
    }
}
