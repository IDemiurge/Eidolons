package main.handlers;

import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.gui.components.table.AvColorHandler;
import main.handlers.control.AvKeyHandler;
import main.handlers.control.AvSelectionHandler;
import main.handlers.control.AvTableHandler;
import main.handlers.func.AvInfoHandler;
import main.handlers.gen.AvGenHandler;
import main.handlers.mod.AvModelHandler;
import main.handlers.mod.AvSaveHandler;
import main.handlers.mod.AvVersionHandler;
import main.handlers.types.AvAssembler;
import main.handlers.types.AvCheckHandler;
import main.handlers.types.AvTypeHandler;
import main.handlers.types.SimulationHandler;
import main.launch.ArcaneVault;
import main.system.util.DialogMaster;

import java.util.List;
import java.util.Set;

public class AvHandler {
    protected    AvManager manager;

    protected ObjType getType(String name, DC_TYPE type) {
        return DataManager.getType(name, type);
    }
    protected boolean confirm(String s) {
        return DialogMaster.confirm(s);
    }

    protected String input(String s) {
        return DialogMaster.inputText(s );
    }
    protected int inputInt(String s) {
        return DialogMaster.inputInt(s, 0);
    }

    public ObjType getSelected(){
        return ArcaneVault.getSelectedType();
    }
    public ObjType getPrevious(){
        return ArcaneVault.getPreviousSelectedType();
    }
    public DC_Game getGame(){
        return ArcaneVault.getGame();
    }
    public List<ObjType> getSelectedTypes(){
        return ArcaneVault.getSelectedTypes();
    }

    public AvHandler(AvManager manager) {
        this.manager = manager;
    }

    public void init(){

    }
    public void afterInit(){

    }

    public void saved(){

    }

    public static void refresh() {
        AvManager.refresh();
    }

    public Set<AvHandler> getHandlers() {
        return manager.getHandlers();
    }

    public AvCheckHandler getCheckHandler() {
        return manager.getCheckHandler();
    }

    public AvTypeHandler getTypeHandler() {
        return manager.getTypeHandler();
    }

    public AvAssembler getAssembler() {
        return manager.getAssembler();
    }

    public SimulationHandler getSimulationHandler() {
        return manager.getSimulationHandler();
    }

    public AvModelHandler getModelHandler() {
        return manager.getModelHandler();
    }

    public AvSelectionHandler getSelectionHandler() {
        return manager.getSelectionHandler();
    }

    public AvGenHandler getGenHandler() {
        return manager.getGenHandler();
    }

    public AvKeyHandler getKeyHandler() {
        return manager.getKeyHandler();
    }

    public AvSaveHandler getSaveHandler() {
        return manager.getSaveHandler();
    }

    public AvVersionHandler getVersionHandler() {
        return manager.getVersionHandler();
    }

    public AvColorHandler getColorHandler() {
        return manager.getColorHandler();
    }
    public AvInfoHandler getInfoHandler() {
        return manager.getInfoHandler();
    }

    public AvTableHandler getTableHandler() {
        return manager.getTableHandler();
    }

    public void loaded() {
    }
}
