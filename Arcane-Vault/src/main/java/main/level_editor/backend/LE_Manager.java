package main.level_editor.backend;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.backend.functions.advanced.LE_AdvFuncs;
import main.level_editor.backend.functions.io.LE_DataHandler;
import main.level_editor.backend.functions.mapping.LE_ModuleHandler;
import main.level_editor.backend.functions.mouse.LE_MouseHandler;
import main.level_editor.backend.functions.palette.PaletteHandlerImpl;
import main.level_editor.backend.handlers.LE_EditHandler;
import main.level_editor.backend.handlers.LE_MenuHandler;
import main.level_editor.backend.handlers.ai.LE_AiHandler;
import main.level_editor.backend.handlers.dialog.LE_DialogHandler;
import main.level_editor.backend.handlers.model.LE_ModelManager;
import main.level_editor.backend.handlers.operation.OperationHandler;
import main.level_editor.backend.handlers.operation.obj.LE_ObjHandler;
import main.level_editor.backend.handlers.selection.LE_SelectionHandler;
import main.level_editor.backend.handlers.structure.LE_StructureHandler;
import main.level_editor.backend.handlers.structure.layer.LayerHandlerImpl;
import main.level_editor.backend.metadata.script.LE_ScriptHandler;
import main.level_editor.backend.sim.LE_GameSim;
import main.level_editor.backend.sim.LE_IdManager;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.grid.LE_CameraHandler;
import main.level_editor.gui.stage.LE_KeyHandler;

public class LE_Manager {

    private final LE_GameSim game;
    private final LE_IdManager idManager;
    private final OperationHandler operationHandler;
    private final LE_ObjHandler objHandler;
    private final LE_CameraHandler cameraHandler;
    private final LE_EditHandler editHandler;
    private final LE_KeyHandler keyHandler;
    private final LE_AiHandler aiHandler;
    private final LE_DialogHandler dialogHandler;
    private final LE_ScriptHandler scriptHandler;
    private final LayerHandlerImpl layerHandler;
    private Floor floor;
    private LE_SelectionHandler selectionHandler;
    private LE_ModelManager modelManager;
    private LE_DataHandler dataHandler;
    private LE_MouseHandler mouseHandler;
    private LE_AdvFuncs advFuncs;
    private LE_MenuHandler menuHandler;
    private LE_StructureHandler structureManager;
    private LE_ModuleHandler moduleHandler;
    private PaletteHandlerImpl paletteHandler;

    public LE_Manager(Floor floor) {
        this.floor = floor;
        mouseHandler = new LE_MouseHandler(this);
        menuHandler = new LE_MenuHandler(this);
        selectionHandler = new LE_SelectionHandler(this);
        modelManager = new LE_ModelManager(this);
        dataHandler = new LE_DataHandler(this);
        game = floor.getGame();
        idManager = game.getSimIdManager();
        structureManager = new LE_StructureHandler(this);
        moduleHandler = new LE_ModuleHandler(this);
        operationHandler = new OperationHandler(this);
        objHandler = new LE_ObjHandler(this);
        paletteHandler = new PaletteHandlerImpl(this);
        cameraHandler = new LE_CameraHandler(this);
        editHandler = new LE_EditHandler(this);
        keyHandler = new LE_KeyHandler(this);
        aiHandler = new LE_AiHandler(this);
        dialogHandler = new LE_DialogHandler(this);
        scriptHandler = new LE_ScriptHandler(this);
        layerHandler = new LayerHandlerImpl(this);
//        layerHandler = new IRngHandler(this);
    }

    public LE_ScriptHandler getScriptHandler() {
        return scriptHandler;
    }

    public LE_DialogHandler getDialogHandler() {
        return dialogHandler;
    }

    public LE_AiHandler getAiHandler() {
        return aiHandler;
    }

    public LE_KeyHandler getKeyHandler() {
        return keyHandler;
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

    public LE_CameraHandler getCameraHandler() {
        return cameraHandler;
    }

    public LE_EditHandler getEditHandler() {
        return editHandler;
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

    public LE_StructureHandler getStructureManager() {
        return structureManager;
    }

    public LE_ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    public OperationHandler getOperationHandler() {
        return operationHandler;
    }

    public LE_ObjHandler getObjHandler() {
        return objHandler;
    }

    public PaletteHandlerImpl getPaletteHandler() {
        return paletteHandler;
    }

    public LayerHandlerImpl getLayerHandler() {
        return layerHandler;
    }
}
