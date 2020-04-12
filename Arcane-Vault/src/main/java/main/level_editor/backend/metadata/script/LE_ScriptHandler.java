package main.level_editor.backend.metadata.script;

import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_ScriptHandler extends LE_Handler {
    public LE_ScriptHandler(LE_Manager manager) {
        super(manager);
    }

    public void editScriptData(Coordinates c) {
        String text = getGame().getDungeon().getCustomDataMap().get(c);
        String prev = getGame().getDungeon().getCustomDataMap().get(c);
        text = getDialogHandler().textInput("", text);
        if (text != null && !text.equals(prev)) {
            operation(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, c, text, prev);
        }
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

    public void saved() {
        String text = null;
        //LAYERS - on top of this MAIN?
//        getGame().getDungeon().setProperty(PROPS.COORDINATE_SCRIPTS, text);
    }

    public String getDisplayedScriptData(Coordinates c) {
        String text = getGame().getDungeon().getCustomDataMap().get(c);
        if ( text.isEmpty()) {
            return "";
        }
        CellScriptData data = new CellScriptData(text);
        return data.getRelevantData();
    }

}
