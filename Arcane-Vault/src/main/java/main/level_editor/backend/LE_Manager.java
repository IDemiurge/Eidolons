package main.level_editor.backend;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.backend.functions.advanced.LE_AdvFuncs;
import main.level_editor.backend.functions.mapping.LE_ModuleHandler;
import main.level_editor.backend.functions.structure.LE_StructureManager;
import main.level_editor.backend.io.LE_DataHandler;
import main.level_editor.backend.menu.LE_MenuHandler;
import main.level_editor.backend.model.LE_ModelManager;
import main.level_editor.backend.mouse.LE_MouseHandler;
import main.level_editor.backend.selection.LE_SelectionHandler;
import main.level_editor.metadata.object.LE_IdManager;
import main.level_editor.sim.LE_GameSim;
import main.level_editor.struct.level.Floor;

public class LE_Manager {

    private final LE_GameSim game;
    private final LE_IdManager idManager;
    private Floor floor;
    private LE_SelectionHandler selectionHandler;
    private LE_ModelManager modelManager;
    private LE_DataHandler dataHandler;
    private LE_MouseHandler mouseHandler;
    private LE_AdvFuncs advFuncs;
    private LE_MenuHandler menuHandler;
    private LE_StructureManager structureManager;
    private LE_ModuleHandler moduleHandler;

    public LE_Manager(Floor floor) {
        this.floor = floor;
        mouseHandler = new LE_MouseHandler(this);
        menuHandler = new LE_MenuHandler(this);
        selectionHandler = new LE_SelectionHandler(this);
        modelManager = new LE_ModelManager(this);
        dataHandler = new LE_DataHandler(this);
        game = floor.getGame();
        idManager = game.getSimIdManager();
        structureManager = new LE_StructureManager(this);
        moduleHandler = new LE_ModuleHandler(this);
    }

    public LE_GameSim getGame() {
        return game;
    }

    public LE_IdManager getIdManager() {
        return idManager;
    }

    public LE_SelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    public LE_ModelManager getModelManager() {
        return modelManager;
    }

    public LE_MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public LE_AdvFuncs getAdvFuncs() {
        return advFuncs;
    }

    public LE_DataHandler getDataHandler() {
        return dataHandler;
    }

    public Floor getFloor() {
        return floor;
    }

    public LE_MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public Module getModule(Coordinates c) {
        return getGame().getMetaMaster().getModuleMaster().getModule(c);
    }

    public LE_StructureManager getStructureManager() {
        return structureManager;
    }

    public LE_ModuleHandler getModuleHandler() {
        return moduleHandler;
    }
}
