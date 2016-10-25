package main.client.lobby;

import main.swing.generic.components.panels.G_ButtonPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LobbyControlPanel extends G_ButtonPanel {

    private static final String[] host_items = {"Start", "Choose Hero"};
    private static final String[] items = {"Choose Hero"};
    private DC_Lobby lobby;

    public LobbyControlPanel(DC_Lobby lobby, boolean host) {
        super(host_items);
        this.lobby = lobby;
    }

    public LobbyControlPanel(DC_Lobby lobby) {
        super(items);
        this.lobby = lobby;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        lobby.handleCommand(((JButton) e.getSource()).getActionCommand());
    }
}
