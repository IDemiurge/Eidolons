package main.level_editor.backend.metadata.script;

import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.dialog.struct.CellDataEditor;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.Map;

import static main.content.CONTENT_CONSTS.MARK;

public class LE_ScriptHandler extends CellDataHandler<CellScriptData> implements IScriptHandler {
    public LE_ScriptHandler(LE_Manager manager) {
        super(manager);
    }


    @Override
    public void mark() {
        MARK mark = LE_Screen.getInstance().getGuiStage().getEnumChooser().chooseEnum(MARK.class);
        if (mark == MARK.entrance) {
            for (Coordinates c : getGame().getCoordinates()) {
                if (getScriptData(c) != null) {
                    unmark(c, MARK.entrance);
                }
            }
        }
        operation(Operation.LE_OPERATION.FILL_START);
        for (Coordinates coordinate : getSelectionHandler().getSelection().getCoordinates()) {
            if (getGame().getCell(coordinate).isVOID()) {
                if (mark == MARK._void) {
                    continue;
                }
            }
            mark(coordinate, mark);
        }
        operation(Operation.LE_OPERATION.FILL_END);
    }


    @Override
    public void togglable() {
        operation(Operation.LE_OPERATION.FILL_START);
        for (Coordinates coordinate : getSelectionHandler().getSelection().getCoordinates()) {
            mark(coordinate, MARK.togglable);
        }
        operation(Operation.LE_OPERATION.FILL_END);

    }

    public void mark(Coordinates c, MARK mark) {
        editData(c, data -> data.addValue(CellScriptData.CELL_SCRIPT_VALUE.marks, mark.toString()), null);
    }

    public void unmark(Coordinates c, MARK mark) {
        editData(c, data -> data.removeFromValue(CellScriptData.CELL_SCRIPT_VALUE.marks, mark.toString()), null);
    }

    public void editScriptData(Coordinates c) {
        editData(c);
    }


    @Override
    public void clearMarks() {
        operation(Operation.LE_OPERATION.CLEAR_START);
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            CellScriptData scriptData = getScriptData(c);
            if (scriptData == null) {
                continue;
            }
            CellScriptData prev = new CellScriptData(scriptData.getData());
            scriptData.removeValue(CellScriptData.CELL_SCRIPT_VALUE.marks);
            getOperationHandler().execute(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, c, scriptData, prev);
        }
        operation(Operation.LE_OPERATION.CLEAR_END);
    }

    @Override
    protected Map<Coordinates, CellScriptData> getMap() {
        return getFloorWrapper().getTextDataMap();
    }

    @Override
    protected Operation.LE_OPERATION getOperation() {
        return Operation.LE_OPERATION.CELL_SCRIPT_CHANGE;
    }

    @Override
    protected WaitMaster.WAIT_OPERATIONS getEditOperation() {
        return CellDataEditor.OPERATION;
    }

    @Override
    protected CellScriptData createData(String s) {
        return new CellScriptData(s);
    }

    public void init() {
        for (Coordinates c : getGame().getCoordinates()) {
            String text = getDisplayedScriptData(c);
            if (!text.isEmpty()) {
                GuiEventManager.triggerWithParams(
                        GuiEventType.LE_CELL_SCRIPTS_LABEL_UPDATE, c, text);
            }
        }

    }

    @Override
    protected CellScriptData append(CellScriptData prev, CellScriptData result) {
        for (String s : prev.getValues().keySet()) {
            result.addValue(s, prev.getValue(s));
        }
        return result;
    }

    @Override
    protected String getXmlNodeName() {
        return FloorLoader.SCRIPT_DATA;
    }

    public void clear(Coordinates c) {
        getOperationHandler().execute(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, c,
                new CellScriptData(""), getScriptData(c));
    }

    @Override
    protected CellScriptData getData(Coordinates c) {
        return getScriptData(c);
    }

    public CellScriptData getScriptData(Coordinates c) {
        return getFloorWrapper().getTextDataMap().get(c);
    }

    public String getDisplayedScriptData(Coordinates c) {
        CellScriptData data = getFloorWrapper().getTextDataMap().get(c);
        if (data == null) {
            return "";
        }
        String text = data.getData();
        return text;
    }

    @Override
    public void clear() {
        operation(Operation.LE_OPERATION.CLEAR_START);
        for (Coordinates coordinate : getSelectionHandler().getSelection().getCoordinates()) {
            clear(coordinate);
        }
        operation(Operation.LE_OPERATION.CLEAR_END);
    }

    @Override
    public void copy() {
        copyData(false);
    }

    @Override
    public void paste() {
        pasteData();
    }

    @Override
    public void cut() {
        copyData(true);
    }

    @Override
    public void edit() {
        editScriptData(getSelectionHandler().getSelection().getLastCoordinates());
    }

}
