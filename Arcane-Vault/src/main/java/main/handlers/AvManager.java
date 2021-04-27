package main.handlers;

import eidolons.game.module.herocreator.CharacterCreator;
import main.entity.type.ObjType;
import main.gui.builders.MainBuilder;
import main.gui.components.table.AvColorHandler;
import main.gui.tree.AvTreeBuilder;
import main.handlers.control.AvButtonHandler;
import main.handlers.control.AvKeyHandler;
import main.handlers.control.AvSelectionHandler;
import main.handlers.control.AvTableHandler;
import main.handlers.func.AvInfoHandler;
import main.handlers.gen.AvGenHandler;
import main.handlers.mod.AvAdjuster;
import main.handlers.mod.AvModelHandler;
import main.handlers.mod.AvSaveHandler;
import main.handlers.mod.AvVersionHandler;
import main.handlers.types.AvAssembler;
import main.handlers.types.AvCheckHandler;
import main.handlers.types.AvTypeHandler;
import main.handlers.types.SimulationHandler;
import main.utilities.workspace.WsHandler;
import main.v2_0.AV2;
import main.launch.ArcaneVault;
import main.utilities.workspace.WorkspaceManager;

import java.util.LinkedHashSet;
import java.util.Set;

public class AvManager {
    /*
    All the stupid flags here please... accessible
     */
    Set<AvHandler> handlers = new LinkedHashSet<>();
    AvCheckHandler checkHandler;
    AvTypeHandler typeHandler;
    AvAssembler assembler;
    SimulationHandler simulationHandler;
    AvModelHandler modelHandler;
    AvSelectionHandler selectionHandler;
    AvGenHandler genHandler;
    AvKeyHandler keyHandler;
    AvSaveHandler saveHandler;
    AvVersionHandler versionHandler;
    AvInfoHandler infoHandler;
    AvTableHandler tableHandler;
    private MainBuilder mainBuilder;
    AvTreeBuilder treeBuilder;
    AvButtonHandler buttonHandler;
    private AvColorHandler colorHandler;
    private WorkspaceManager workspaceManager;
    private WsHandler workspaceHandler;

    public void init() {
        handlers.add(checkHandler = new AvCheckHandler(this));
        // handlers.add(assembler = new AvAssembler(this));
        handlers.add(simulationHandler = new SimulationHandler(this));
        handlers.add(typeHandler = new AvTypeHandler(this));
        handlers.add(modelHandler = new AvModelHandler(this));
        // handlers.add(selectionHandler = new AvSelectionHandler(this));
        // handlers.add(genHandler = new AvGenHandler(this));
        handlers.add(keyHandler = new AvKeyHandler(this));
        handlers.add(saveHandler = new AvSaveHandler(this));
        handlers.add(versionHandler = new AvVersionHandler(this));
        // handlers.add(infoHandler = new AvInfoHandler(this));
        // handlers.add(tableHandler = new AvTableHandler(this));
        handlers.add(treeBuilder = new AvTreeBuilder(this));
        handlers.add(buttonHandler = new AvButtonHandler(this));
        handlers.add(colorHandler = new AvColorHandler(this));
        handlers.add(workspaceManager = new WorkspaceManager(this));
        handlers.add(workspaceHandler = new WsHandler(this));

        handlers.forEach(handler -> handler.init());
        handlers.forEach(handler -> handler.afterInit());
    }

    public static void toggle() {
        AvAdjuster.autoAdjust = !AvAdjuster.autoAdjust;
        AvSaveHandler.setAutoSaveOff(!AvSaveHandler.isAutoSaveOff());
        ArcaneVault.setSimulationOn(!ArcaneVault.isSimulationOn());
        ArcaneVault.getMainBuilder().getEditViewPanel().AE_VIEW_TOGGLING = !ArcaneVault
                .getMainBuilder().getEditViewPanel().AE_VIEW_TOGGLING;

    }

    public static void edit() {

        AvSaveHandler.setAutoSaveOff(!AvSaveHandler.isAutoSaveOff());

        if (!ArcaneVault.isSimulationOn()) {
            ArcaneVault.setSimulationOn(true);
            return;
        }

        CharacterCreator.addHero(AV2.getSimulationHandler().getUnit(new ObjType(ArcaneVault
                .getSelectedType())));
        ArcaneVault.setSimulationOn(false);

    }

    public static void refresh() {
        ArcaneVault.getMainBuilder().getTree().getTreeSelectionListeners()[0].valueChanged(null);
    }

    public AvButtonHandler getButtonHandler() {
        return buttonHandler;
    }

    public AvTreeBuilder getTreeBuilder() {
        return treeBuilder;
    }

    public Set<AvHandler> getHandlers() {
        return handlers;
    }

    public AvCheckHandler getCheckHandler() {
        return checkHandler;
    }

    public AvTypeHandler getTypeHandler() {
        return typeHandler;
    }

    public AvAssembler getAssembler() {
        return assembler;
    }

    public SimulationHandler getSimulationHandler() {
        return simulationHandler;
    }

    public AvModelHandler getModelHandler() {
        return modelHandler;
    }

    public AvSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    public AvGenHandler getGenHandler() {
        return genHandler;
    }

    public AvKeyHandler getKeyHandler() {
        return keyHandler;
    }

    public AvSaveHandler getSaveHandler() {
        return saveHandler;
    }

    public AvVersionHandler getVersionHandler() {
        return versionHandler;
    }

    public AvInfoHandler getInfoHandler() {
        return infoHandler;
    }

    public AvTableHandler getTableHandler() {
        return tableHandler;
    }

    public void setMainBuilder(MainBuilder mainBuilder) {
        this.mainBuilder = mainBuilder;
    }

    public MainBuilder getMainBuilder() {
        return mainBuilder;
    }

    public void loaded() {
        for (AvHandler handler : handlers) {
            handler.loaded();
        }
    }

    public AvColorHandler getColorHandler() {
        return colorHandler;
    }

    public WsHandler getWorkspaceHandler() {
        return workspaceHandler;
    }

    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }
}
