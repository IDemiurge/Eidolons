package main.level_editor.backend.metadata.script;

import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.data.xml.XmlStringBuilder;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.dialog.struct.CellDataEditor;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;

import java.util.function.Function;

public class LE_ScriptHandler extends LE_Handler implements IScriptHandler{
    public LE_ScriptHandler(LE_Manager manager) {
        super(manager);
    }

    public void editScriptData(Coordinates c) {

        CellScriptData data = getFloorWrapper().getTextDataMap().get(c);
        CellScriptData prev = data == null
                ? new CellScriptData("")
                : new CellScriptData(data.getData());
        if (data == null) {
            data = new CellScriptData("");
        }
        editData(data);
        WaitMaster.waitForInputAnew(CellDataEditor.OPERATION);
        String text = data.getData();
        if (!text.equals(prev.getData())) {
            getOperationHandler().execute(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, c, data, prev);
        }
    }

    @Override
    public String getXml(Function<Integer, Boolean> idFilter, Function<Coordinates, Boolean> coordinateFilter) {

        XmlStringBuilder builder = new XmlStringBuilder();
        for (Coordinates coordinates : getFloorWrapper().getTextDataMap().keySet()) {
            if (!coordinateFilter.apply(coordinates)) {
                continue;
            }
            CellScriptData scriptData = getFloorWrapper().getTextDataMap().get(coordinates);
            builder.append(coordinates.toString()).append("=").append(scriptData.getData())
                    .append(StringMaster.AND_SEPARATOR);
        }
        return builder.wrap(FloorLoader.SCRIPT_DATA).toString();
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
    public void afterLoaded() {
        init();
    }

    public void clear(Coordinates c) {

        getOperationHandler().execute(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, c,
                new CellScriptData(""), getScriptData(c));
        // getFloorWrapper().getTextDataMap().remove(c);

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
        for (Coordinates coordinate : getSelectionHandler().getSelection().getCoordinates()) {
            clear(coordinate);
        }
    }

    @Override
    public void copy() {
        getModelManager().copyScriptData(false);
    }

    @Override
    public void paste() {
        getModelManager().pasteData();
    }

    @Override
    public void cut() {
        getModelManager().copyScriptData(true);
    }

    @Override
    public void edit() {
        editScriptData(getSelectionHandler().getSelection().getLastCoordinates());
    }
}
