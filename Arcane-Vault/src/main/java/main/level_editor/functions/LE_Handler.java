package main.level_editor.functions;


import main.level_editor.functions.advanced.LE_AdvFuncs;
import main.level_editor.functions.io.LE_DataHandler;
import main.level_editor.functions.menu.LE_MenuHandler;
import main.level_editor.functions.model.LE_DataModel;
import main.level_editor.functions.model.LE_ModelManager;
import main.level_editor.functions.mouse.LE_MouseHandler;
import main.level_editor.functions.selection.LE_SelectionHandler;
import main.level_editor.metadata.object.LE_IdManager;
import main.level_editor.sim.LE_GameSim;
import main.level_editor.struct.level.Floor;

public class LE_Handler {

    protected final LE_Manager manager;

    public LE_Handler(LE_Manager manager) {
        this.manager = manager;
    }

    public LE_SelectionHandler getSelectionHandler() {
        return manager.getSelectionHandler();
    }

    public LE_ModelManager getModelManager() {
        return manager.getModelManager();
    }

    public LE_DataHandler getDataHandler() {
        return manager.getDataHandler();
    }

    public Floor getFloor() {
        return manager.getFloor();
    }

    public LE_DataModel getModel() {
        return getModelManager().getModel();
    }

    public LE_GameSim getGame() {
        return manager.getGame();
    }

    public LE_IdManager getIdManager() {
        return manager.getIdManager();
    }

    public LE_MouseHandler getMouseHandler() {
        return manager.getMouseHandler();
    }

    public LE_AdvFuncs getAdvFuncs() {
        return manager.getAdvFuncs();
    }

    public LE_MenuHandler getMenuHandler() {
        return manager.getMenuHandler();
    }
}
