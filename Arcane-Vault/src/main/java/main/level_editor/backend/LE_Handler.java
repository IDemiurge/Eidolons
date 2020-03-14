package main.level_editor.backend;


import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.backend.functions.advanced.LE_AdvFuncs;
import main.level_editor.backend.functions.mapping.LE_ModuleHandler;
import main.level_editor.backend.functions.structure.LE_StructureManager;
import main.level_editor.backend.io.LE_DataHandler;
import main.level_editor.backend.menu.LE_MenuHandler;
import main.level_editor.backend.model.LE_DataModel;
import main.level_editor.backend.model.LE_ModelManager;
import main.level_editor.backend.mouse.LE_MouseHandler;
import main.level_editor.backend.selection.LE_SelectionHandler;
import main.level_editor.metadata.object.LE_IdManager;
import main.level_editor.sim.LE_GameSim;
import main.level_editor.struct.level.Floor;

public class LE_Handler {

    protected final LE_Manager manager;

    public LE_Handler(LE_Manager manager) {
        this.manager = manager;
    }

    public Module getModule(Coordinates c) {
        return manager.getModule(c);
    }

    public LE_StructureManager getStructureManager() {
        return manager.getStructureManager();
    }

    public LE_ModuleHandler getModuleHandler() {
        return manager.getModuleHandler();
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
