package main.level_editor.backend;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.backend.functions.advanced.LE_AdvFuncs;
import main.level_editor.backend.functions.io.LE_DataHandler;
import main.level_editor.backend.functions.io.LE_XmlMaster;
import main.level_editor.backend.functions.mapping.LE_TransitHandler;
import main.level_editor.backend.functions.mouse.LE_MouseHandler;
import main.level_editor.backend.functions.palette.PaletteHandlerImpl;
import main.level_editor.backend.handlers.LE_EditHandler;
import main.level_editor.backend.handlers.LE_MenuHandler;
import main.level_editor.backend.handlers.ai.LE_AiHandler;
import main.level_editor.backend.handlers.dialog.LE_DialogHandler;
import main.level_editor.backend.handlers.model.LE_ModelManager;
import main.level_editor.backend.handlers.operation.LE_ObjHandler;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.backend.handlers.operation.OperationHandler;
import main.level_editor.backend.handlers.selection.LE_SelectionHandler;
import main.level_editor.backend.handlers.structure.LE_MapHandler;
import main.level_editor.backend.handlers.structure.LE_ModuleHandler;
import main.level_editor.backend.handlers.structure.LE_StructureHandler;
import main.level_editor.backend.handlers.structure.layer.LayerHandlerImpl;
import main.level_editor.backend.metadata.object.LE_EntityHandler;
import main.level_editor.backend.metadata.script.LE_ScriptHandler;
import main.level_editor.backend.sim.LE_GameSim;
import main.level_editor.backend.sim.LE_IdManager;
import main.level_editor.backend.struct.level.LE_Floor;
import main.level_editor.gui.grid.LE_CameraHandler;
import main.level_editor.gui.stage.LE_KeyHandler;

import java.util.LinkedHashSet;
import java.util.Set;

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
    private final LE_EntityHandler entityHandler;
    private LE_Floor floor;
    private LE_SelectionHandler selectionHandler;
    private LE_ModelManager modelManager;
    private LE_DataHandler dataHandler;
    private LE_MouseHandler mouseHandler;
    private LE_AdvFuncs advFuncs;
    private LE_MenuHandler menuHandler;
    private LE_StructureHandler structureManager;
    private LE_ModuleHandler moduleHandler;
    private PaletteHandlerImpl paletteHandler;
    private LE_MapHandler mapHandler;
    private Set<LE_Handler> handlers= new LinkedHashSet<>();
    private LE_TransitHandler transitHandler;
    private LE_XmlMaster xmlMaster;
    private boolean loaded;

    public LE_Manager(LE_Floor floor) {
        this.floor = floor;        
        
        game = floor.getGame();
        idManager = game.getSimIdManager();
        handlers.add( xmlMaster = new LE_XmlMaster(this));
        handlers.add(  entityHandler = new LE_EntityHandler(this));
        handlers.add(  mouseHandler = new LE_MouseHandler(this));
        handlers.add( menuHandler = new LE_MenuHandler(this));
        handlers.add( selectionHandler = new LE_SelectionHandler(this));
        handlers.add(  modelManager = new LE_ModelManager(this));
        handlers.add( dataHandler = new LE_DataHandler(this));
        handlers.add( structureManager = new LE_StructureHandler(this));
        handlers.add( moduleHandler = new LE_ModuleHandler(this));
        handlers.add( operationHandler = new OperationHandler(this));
        handlers.add( objHandler = new LE_ObjHandler(this));
        handlers.add( paletteHandler = new PaletteHandlerImpl(this));
        handlers.add(  cameraHandler = new LE_CameraHandler(this));
        handlers.add( editHandler = new LE_EditHandler(this));
        handlers.add( keyHandler = new LE_KeyHandler(this));
        handlers.add( aiHandler = new LE_AiHandler(this));
        handlers.add( dialogHandler = new LE_DialogHandler(this));
        handlers.add( scriptHandler = new LE_ScriptHandler(this));
        handlers.add(layerHandler = new LayerHandlerImpl(this));
        handlers.add(  mapHandler = new LE_MapHandler(this));
        handlers.add(  transitHandler = new LE_TransitHandler(this));
        handlers.add(  advFuncs = new LE_AdvFuncs(this));
//        layerHandler = new IRngHandler(this);
    }

    public void load() {
        for (LE_Handler handler : handlers) {
            handler.load();
        }
        loaded=true;
    }

    public void afterLoaded() {
        for (LE_Handler handler : handlers) {
            handler.afterLoaded();
        }
    }
    public LE_MapHandler getMapHandler() {
        return mapHandler;
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

    public LE_Floor getFloor() {
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

    public LE_EntityHandler getEntityHandler() {
        return entityHandler;
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

    public   void operation(Operation.LE_OPERATION operation, Object... args ) {
        getOperationHandler().operation(operation, args);
    }

    public LE_XmlMaster getXmlMaster() {
        return xmlMaster;
    }

    public Set<LE_Handler> getHandlers() {
                return handlers;
    }

    public LE_TransitHandler getTransitHandler() {
        return transitHandler;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
