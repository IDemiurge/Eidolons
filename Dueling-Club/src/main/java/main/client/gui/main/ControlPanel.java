package main.client.gui.main;

import main.client.DuelingClub;
import main.client.net.DC_GameHost;
import main.client.net.DC_ServerConnector;
import main.client.net.GameConnector;
import main.game.HostedGame;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.Err;
import main.system.net.WaitingThread;
import main.system.net.socket.ServerConnector.CODES;
import main.system.net.user.NameValidator;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends G_Panel implements ActionListener {
    private static final String NEW_GAME = "New Game";
    public static final String[] commands = {NEW_GAME, "New Hero"};
    private int i = 1;

    public ControlPanel() {
        setLayout(new MigLayout("wrap, flowy"));
        JButton newGameButton = new JButton(NEW_GAME);
        newGameButton.addActionListener(this);
        newGameButton.setMnemonic('n');
        add(newGameButton);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // ((JButton) e.getSource()).getActionCommand()
        String gameName =
                // JOptionPane.showInputDialog ("Enter new game's title",
                "Game" + i
                // )
                ;
        i++;
        if (gameName == null)
            return;

        if (NameValidator.checkGameName(gameName)) {
            DC_ServerConnector.send(CODES.NEW_GAME);
            DC_ServerConnector.send(gameName);
            HostedGame game;
            if (new WaitingThread(CODES.NEW_GAME).waitForInput()) {
                game = new HostedGame(WaitingThread.getINPUT(CODES.NEW_GAME),
                        false, true);
                game.setHost(DC_ServerConnector.getUser());
            } else {
                Err.error("Could not create your game, try a different name!");
                return;
            }
            DC_GameHost gameHost = new DC_GameHost(new GameConnector(game));

            if (!DuelingClub.ADMIN_MODE) {
                // MessageManager.prompt(
            }

            DuelingClub.newGameLobby(gameHost);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Game Name!");
        }
    }
}
