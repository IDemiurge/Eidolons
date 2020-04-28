package main.level_editor.backend;


import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
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
import main.level_editor.backend.handlers.model.EditorModel;
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
import main.system.data.DataUnit;

import java.util.function.Function;

public abstract class LE_Handler {

    protected final LE_Manager manager;

    public LE_Handler(LE_Manager manager) {
        this.manager = manager;
    }

    public void operation(Operation.LE_OPERATION operation, Object... args) {
        getOperationHandler().operation(operation, args);
    }

    public void afterLoaded() {
    }

    public void load() {
    }

    public <T extends Enum<T>> void editData(DataUnit<T> data){
        getEditHandler().editDataUnit(data);
    }
    protected boolean isLoaded() {
        return manager.isLoaded();
    }
    public String getDataMapString(Function<Integer, Boolean> idFilter, Function<Coordinates, Boolean> coordinateFilter) {
        return "";
    }

    public String getXml(Function<Integer, Boolean> idFilter, Function<Coordinates, Boolean> coordinateFilter) {
        return "";
    }

    protected StructMaster getStructureMaster() {
        return getGame().getMetaMaster().getDungeonMaster().getStructMaster();
    }

    public LE_EntityHandler getEntityHandler() {
        return manager.getEntityHandler();
    }

    public LE_XmlMaster getXmlMaster() {
        return manager.getXmlMaster();
    }

    protected LevelStruct getDungeonLevel() {
        return getGame().getDungeonMaster().getFloorWrapper();
    }

    public LE_DialogHandler getDialogHandler() {
        return manager.getDialogHandler();
    }

    public LE_AiHandler getAiHandler() {
        return manager.getAiHandler();
    }

    public LE_KeyHandler getKeyHandler() {
        return manager.getKeyHandler();
    }

    public LE_CameraHandler getCameraHandler() {
        return manager.getCameraHandler();
    }

    public LE_EditHandler getEditHandler() {
        return manager.getEditHandler();
    }

    public PaletteHandlerImpl getPaletteHandler() {
        return manager.getPaletteHandler();
    }

    public OperationHandler getOperationHandler() {
        return manager.getOperationHandler();
    }

    public Module getModule(Coordinates c) {
        return manager.getModule(c);
    }

    public LE_StructureHandler getStructureHandler() {
        return manager.getStructureManager();
    }

    public LE_ModuleHandler getModuleHandler() {
        return manager.getModuleHandler();
    }

    public LE_SelectionHandler getSelectionHandler() {
        return manager.getSelectionHandler();
    }

    public LE_ScriptHandler getScriptHandler() {
        return manager.getScriptHandler();
    }

    public LayerHandlerImpl getLayerHandler() {
        return manager.getLayerHandler();
    }

    public LE_ObjHandler getObjHandler() {
        return manager.getObjHandler();
    }

    public LE_ModelManager getModelManager() {
        return manager.getModelManager();
    }

    public LE_DataHandler getDataHandler() {
        return manager.getDataHandler();
    }

    public Location getFloorWrapper() {
        return manager.getFloor().getWrapper();
    }

    public LE_Floor getFloor() {
        return manager.getFloor() ;
    }
    public EditorModel getModel() {
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

    public LE_TransitHandler getTransitHandler() {
        return manager.getTransitHandler();
    }

    public LE_MapHandler getMapHandler() {
        return manager.getMapHandler();
    }

    public LE_MenuHandler getMenuHandler() {
        return manager.getMenuHandler();
    }

    public void saved() {

    }
}
