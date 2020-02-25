package main.level_editor.functions;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.functions.advanced.LE_AdvFuncs;
import main.level_editor.functions.io.LE_DataHandler;
import main.level_editor.functions.menu.LE_MenuHandler;
import main.level_editor.functions.model.LE_ModelManager;
import main.level_editor.functions.mouse.LE_MouseHandler;
import main.level_editor.functions.selection.LE_SelectionHandler;
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
    LE_AdvFuncs advFuncs;
    private LE_MenuHandler menuHandler;

    public LE_Manager(Floor floor) {
        this.floor = floor;
        mouseHandler = new LE_MouseHandler(this);
        menuHandler = new LE_MenuHandler(this);
        selectionHandler = new LE_SelectionHandler(this);
        modelManager = new LE_ModelManager(this);
        dataHandler = new LE_DataHandler(this);
        game = floor.getGame();
        idManager = game.getSimIdManager();
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
}
