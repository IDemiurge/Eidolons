package main.utilities.workspace;

import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.launch.ArcaneVault;
import main.swing.generic.services.DialogMaster;

public class WsHandler extends AvHandler {
    WorkspaceManager wsManager;

    public WsHandler(AvManager manager) {
        super(manager);
    }

    public void addToCustomWorkspace() {
         Workspace ws = pickWs();
        wsManager.addToWorkspace(ws, ArcaneVault.getSelectedTypes());
    }

    public void removeWorkspace() {
    }
    private Workspace pickWs() {
        DialogMaster.optionChoice("Which WS?", wsManager.getActiveWorkspaces().toArray());

        return null;
    }
}
