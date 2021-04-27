package main.v2_0;

import main.gui.builders.MainBuilder;
import main.gui.components.table.AvColorHandler;
import main.gui.tree.AvTreeBuilder;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.handlers.control.AvButtonHandler;
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
import main.utilities.workspace.WorkspaceManager;
import main.utilities.workspace.WsHandler;

import java.util.Set;

public class AV2 {
    private static final AvManager manager = new AvManager();

    public static void init() {
        manager.init();
    }

    public static AvManager getManager() {
        return manager;
    }

    public static AvButtonHandler getButtonHandler() {
        return getManager().getButtonHandler();
    }

    public static AvTreeBuilder getTreeBuilder() {
        return getManager().getTreeBuilder();
    }

    public static Set<AvHandler> getHandlers() {
        return getManager().getHandlers();
    }

    public static AvCheckHandler getCheckHandler() {
        return getManager().getCheckHandler();
    }

    public static AvTypeHandler getTypeHandler() {
        return getManager().getTypeHandler();
    }

    public static AvAssembler getAssembler() {
        return getManager().getAssembler();
    }

    public static SimulationHandler getSimulationHandler() {
        return getManager().getSimulationHandler();
    }

    public static AvModelHandler getModelHandler() {
        return getManager().getModelHandler();
    }

    public static AvSelectionHandler getSelectionHandler() {
        return getManager().getSelectionHandler();
    }

    public static AvGenHandler getGenHandler() {
        return getManager().getGenHandler();
    }

    public static AvKeyHandler getKeyHandler() {
        return getManager().getKeyHandler();
    }

    public static AvSaveHandler getSaveHandler() {
        return getManager().getSaveHandler();
    }

    public static AvVersionHandler getVersionHandler() {
        return getManager().getVersionHandler();
    }

    public static AvInfoHandler getInfoHandler() {
        return getManager().getInfoHandler();
    }

    public static AvTableHandler getTableHandler() {
        return getManager().getTableHandler();
    }

    public static void setMainBuilder(MainBuilder mainBuilder) {
        getManager().setMainBuilder(mainBuilder);
    }

    public static MainBuilder getMainBuilder() {
        return getManager().getMainBuilder();
    }
    public static AvColorHandler getColorHandler() {
        return getManager().getColorHandler();
    }

    public static WorkspaceManager getWorkspaceManager() {
        return getManager().getWorkspaceManager();
    }
    public static WsHandler getWorkspaceHandler() {
        return getManager().getWorkspaceHandler();
    }
}
