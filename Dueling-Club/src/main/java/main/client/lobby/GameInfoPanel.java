package main.client.lobby;

import main.client.game.options.GAME_OPTIONS;
import main.client.game.options.GameOptions;
import main.client.net.GameConnector;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.game.HostedGame;
import main.swing.generic.components.Refreshable;
import main.swing.generic.components.panels.G_ElementPanel;
import main.system.auxiliary.log.LogMaster;
import main.system.net.RefresherImpl;
import main.system.net.RefresherImpl.REFRESHER_TYPE;
import main.system.net.WaitingThread;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.NetCode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * option menu - drop boxes or?
 * <p>
 * unblocked game info panel
 *
 * @author JustMe
 */
public class GameInfoPanel extends G_ElementPanel<GAME_OPTIONS> implements
        Refreshable {

    private GameOptions gameOptions;
    // private Map<String, String> map;
    private boolean host;
    private GameConnector connector;
    private RefresherImpl refresher;
    private NetCode code = HOST_CLIENT_CODES.GAME_OPTIONS_REQUEST;
    private HostedGame game;

    public GameInfoPanel(boolean host1, HostedGame hostedGame) {
        this.game = hostedGame;
        this.host = host1;
        this.setGameOptions(gameOptions);
        this.refresher = new RefresherImpl(REFRESHER_TYPE.GAME_OPTIONS, this);
        RefresherImpl.getSwitchers().put(REFRESHER_TYPE.GAMELIST, true);
    }

    public GameInfoPanel(GameOptions gameOptions) {
        this.host = false;
        this.setGameOptions(gameOptions);
        this.refresher = new RefresherImpl(REFRESHER_TYPE.GAME_OPTIONS, this);

    }

    @Override
    public void refresh() {
        if (connector != null) {
            connector.send(code);
        } else {
            ServerConnector.send(code);
        }
        if (ServerConnector.launchInputWaitingThread(code)) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setGameOptions(new GameOptions(WaitingThread.getINPUT(code)));

                }

            });
        }
    }

    @Override
    public void addElements() {

        for (String name : this.gameOptions.getValues().keySet()) {
            GAME_OPTIONS CONST = getGameOptions().getEnumConst(name);

            switch (CONST) {

                default: {
                    addDropBoxPanel(CONST.name(), CONST.getOptions());
                    break;
                }
            }
        }
    }

    @Override
    public JComboBox<?> addDropBoxPanel(String name, Object[] options) {
        JComboBox<?> box = super.addDropBoxPanel(name, options);
        int index = Arrays.asList(options).indexOf(gameOptions.getValue(name));
        LogMaster.log(2, name
                + " setting game options db index to " + index);
        setDropBoxIndexQuietly(box, index);

        box.setEnabled(host);
        return box;
    }

    public void setDropBoxIndexQuietly(JComboBox<?> box, int index) {
        box.removeActionListener(box.getActionListeners()[0]);
        box.setSelectedIndex(index);
        box.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String value = ((JComboBox<String>) e.getSource()).getSelectedItem()
                .toString();
        ;
        String name = ((JComboBox<String>) e.getSource()).getActionCommand();
        connector.send(HOST_CLIENT_CODES.GAME_OPTIONS_CHANGED);
        // connector.send(name + StringMaster.getPairSeparator() + value);

        getGameOptions().setValue(name, value);
    }

    public GameConnector getConnector() {
        return connector;
    }

    public void setConnector(GameConnector connector) {
        this.connector = connector;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public void setGameOptions(GameOptions gameOption) {

        if (gameOptions == null) {
            this.gameOptions = new GameOptions();
        } else {
            this.gameOptions = gameOption;
        }
        game.setOptions(gameOptions);
        super.resetElements();
        LogMaster.log(2, "game options set: "
                + gameOptions.getData());
    }

}
