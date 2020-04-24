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

public class LE_SelectionHandler extends LE_Handler implements ISelectionHandler {

    //    LE_Selection previousSelection;
//    LE_Selection selection;
    private SELECTION_MODE mode = SELECTION_MODE.NONE;

    public LE_SelectionHandler(LE_Manager manager) {
        super(manager);
    }

    public Set<Coordinates> selectArea() {
        mode = SELECTION_MODE.AREA;

        EUtils.infoPopup("Select area corner coordinate...");
        Coordinates c1 =
                (Coordinates) WaitMaster.waitForInput(LE_MouseHandler.SELECTION_OPERATION);
        Coordinates c2 = selectCoordinate();
        return new LinkedHashSet<>(CoordinatesMaster.getCoordinatesBetweenWithOffset(c1, c2));
    }

    public LE_Selection getSelection() {
        return getModel().getSelection();
    }

    @Override
    public void count() {

    }

    @Override
    public void selectAll() {

    }

    @Override
    public void selectFilter() {

    }

    public void deselect() {
        getModelManager().modelChanged();
        getModel().setSelection(new LE_Selection());
        getModel().setPaletteSelection(new PaletteSelection());
        mode = SELECTION_MODE.NONE;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }


    public void addToSelected(BattleFieldObject bfObj) {
        Integer id = getIdManager().getId(bfObj);
        if (getModel().getSelection().getIds().contains(id)) {
            getModel().getSelection().getIds().remove(id);
        } else {
            getModel().getSelection().getIds().add(id);
        }
        selectionChanged();
    }

    public void select(BattleFieldObject bfObj) {
        Integer id = getIdManager().getId(bfObj);
        getModel().getSelection().setSingleSelection(id);
        selectionChanged();
    }

    private void selectionChanged() {
        GuiEventManager.trigger(GuiEventType.LE_SELECTION_CHANGED, getModel().getSelection());
    }

    public boolean isSelected(DC_Obj obj) {
        if (obj instanceof DC_Cell) {
            if (getSelection().getCoordinates().contains(obj.getCoordinates())) {
                return true;
            }
        }
        if (!(obj instanceof BattleFieldObject)) {
            return false;
        }
        Integer id = getIdManager().getId((BattleFieldObject) obj);
        if (id != null)
            return getSelection().getIds().contains(id);
        return false;
    }

    public BattleFieldObject getObject() {
        if (getSelection().getIds().isEmpty()) {
            return null;
        }
        Obj obj = getIdManager().getObjectById((Integer) getSelection().getIds().toArray()[0]);
        return (BattleFieldObject) obj;
    }

    public void areaSelected() {
        for (Coordinates c : getSelection().getCoordinates()) {
            for (BattleFieldObject object : getGame().getObjectsAt(c)) {
                getSelection().getIds().add(getIdManager().getId(object));
            }
        }
        getModelManager().modelChanged();
    }

    public void addAreaToSelectedCoordinates(Coordinates c) {
        Coordinates origin = null;
        if ( getSelection().getCoordinates().isEmpty()) {
            origin = getSelectionHandler().getObject().getCoordinates();
        } else {
            origin = getSelection().getCoordinates().iterator().next();
        }
        getSelection().getCoordinates().addAll(CoordinatesMaster.getCoordinatesBetweenWithOffset(c, origin));
        selectionChanged();
        getModelManager().modelChanged();
    }

    public boolean isSelected(Integer id) {
        return getSelection().getIds().contains(id);
    }

    public enum SELECTION_MODE {
        NONE,
        COORDINATE,
        OBJECT,
        AREA,
        ;
    }

    public void selectedCoordinate(Coordinates c) {
        getSelection().getCoordinates().clear();
        addSelectedCoordinate(c);
    }

    public void addSelectedCoordinate(Coordinates c) {
        getSelection().getCoordinates().add(c);
    }

    public Coordinates selectCoordinate() {
        mode = SELECTION_MODE.COORDINATE;
        EUtils.infoPopup("Select coordinate...");
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
