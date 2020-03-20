package main.level_editor.backend.handlers.selection;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.EUtils;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.functions.mouse.LE_MouseHandler;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.LinkedHashSet;
import java.util.Set;

public class LE_SelectionHandler extends LE_Handler {

    LE_Selection previousSelection;
    LE_Selection selection;
    private SELECTION_MODE mode = SELECTION_MODE.NONE;

    public LE_SelectionHandler(LE_Manager manager) {
        super(manager);
        selection = new LE_Selection();
    }

    public void previousSelection() {
        if (previousSelection == null) {
            return;
        }
        setSelection(getPreviousSelection());
        previousSelection = null;
    }

    public Set<Coordinates> selectArea() {
        mode = SELECTION_MODE.AREA;

        EUtils.info("Select area corner coordinate...");
        Coordinates c1 =
                (Coordinates) WaitMaster.waitForInput(LE_MouseHandler.SELECTION_OPERATION);
        Coordinates c2 = selectCoordinate();
        return new LinkedHashSet<>(CoordinatesMaster.getCoordinatesBetween(c1, c2));
    }

    public LE_Selection getPreviousSelection() {
        return previousSelection;
    }

    public LE_Selection getSelection() {
        return selection;
    }

    public void deselect() {
        setSelection(new LE_Selection());
        mode = SELECTION_MODE.NONE;
    }

    public void setSelection(LE_Selection selection) {
        previousSelection = this.selection;
        this.selection = selection;
    }


    public void addToSelected(BattleFieldObject bfObj) {
        Integer id = getIdManager().getId(bfObj);
        if (getModel().getSelection().getIds().contains(id)) {
            getModel().getSelection().getIds().remove(id);
        } else {
            getModel().getSelection().getIds().add(id);
        }
        GuiEventManager.trigger(GuiEventType.LE_SELECTION_CHANGED, getModel().getSelection());
    }

    public void select(BattleFieldObject bfObj) {
        Integer id = getIdManager().getId(bfObj);
        getModel().getSelection().setSingleSelection(id);
        GuiEventManager.trigger(GuiEventType.LE_SELECTION_CHANGED, getModel().getSelection());
    }

    public boolean isSelected(DC_Obj obj) {
        if (obj instanceof DC_Cell) {
            if (selection.getCoordinates().contains(obj.getCoordinates())) {
                return true;
            }
        }
        if (!(obj instanceof BattleFieldObject)) {
            return false;
        }
        Integer id = getIdManager().getId((BattleFieldObject) obj);
        if (id != null)
            return selection.getIds().contains(id);
        return false;
    }

    public BattleFieldObject getObject() {
        if (getSelection().getIds().isEmpty()) {
            return null;
        }
        Obj obj = getIdManager().getObjectById(getSelection().getIds().iterator().next());
        return (BattleFieldObject) obj;
    }

    public enum SELECTION_MODE {
        NONE,
        COORDINATE,
        OBJECT,
        AREA,
        ;
    }

    public Coordinates selectCoordinate() {
        mode = SELECTION_MODE.COORDINATE;
        EUtils.info("Select coordinate...");
        Coordinates c = (Coordinates) WaitMaster.waitForInput(LE_MouseHandler.SELECTION_OPERATION);
        downgradeMode();
        return c;
    }

    private void downgradeMode() {
        if (mode == null) {
            return;
        }
        if (mode == SELECTION_MODE.NONE) {
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
        if (mode == null) {
            mode = SELECTION_MODE.NONE;
        }
        return mode;
    }
}
