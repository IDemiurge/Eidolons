package main.level_editor.backend.metadata.script;

import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.data.xml.XmlStringBuilder;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.function.Function;

public class LE_ScriptHandler extends LE_Handler {
    public LE_ScriptHandler(LE_Manager manager) {
        super(manager);
    }

    public void editScriptData(Coordinates c) {

        CellScriptData data = getFloorWrapper().getTextDataMap().get(c);
        String prev = data == null ? "" : data.getData();
        if (data == null) {
            data = new CellScriptData("");
        }
        editData(data);
        String text = data.getData();
        if (!text.equals(prev)) {
            getOperationHandler().execute(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, c, text, prev);
        }
        if (!text.isEmpty()) {
            getFloorWrapper().getTextDataMap().put(c, data);
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
            builder.append(coordinates.toString()).append("=").append(scriptData.getData());
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


    public String getDisplayedScriptData(Coordinates c) {
        CellScriptData data = getFloorWrapper().getTextDataMap().get(c);
        if (data == null) {
            return "";
        }
        String text = data.getData();
        return text;
    }

}
