package main.client.net;

import main.client.gui.main.GameListPanel;
import main.game.core.game.HostedGame;
import main.swing.generic.components.list.GenericList;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.net.WaitingThread;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.CODES;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Vector;

public class GameList extends GenericList<HostedGame> {

    private GameListPanel gameListPanel;

    public GameList() {
        this.setCellRenderer(this);

    }

    public GameList(GameListPanel gameListPanel) {
        this();
        this.gameListPanel = gameListPanel;
        addMouseListener(gameListPanel);
        refresh();
    }

    private void setData(String input) {

        Vector<HostedGame> v = stringToVector(input);

        // this.setListData(v);

        setList(v);
        setListData(list);
        LogMaster.log(0, getModel().getSize()
                + " Games in List: " + v);
        gameListPanel.refresh();
        this.getParent().revalidate();
        this.getParent().repaint();
        this.repaint();
        this.revalidate();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // update game description etc?...

    }

    public Vector<HostedGame> stringToVector(String input) {

        Vector<HostedGame> v = new Vector<>();
        if (input == null || input == "") {
            return v;
        }

        String[] games = input.split(StringMaster.getVarSeparator());
        for (String gameData : games) {
            boolean started = gameData.startsWith("!");
            gameData = (started) ? gameData.substring(1) : gameData;
            v.add(new HostedGame(gameData, started));
        }
        return v;
    }

    @Override
    public void refresh() {

        ServerConnector.send(CODES.REFRESH_GAME_LIST);

        if (ServerConnector.launchInputWaitingThread(CODES.REFRESH_GAME_LIST)) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setData(WaitingThread.getINPUT(CODES.REFRESH_GAME_LIST));
                }

            });
        }

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HostedGame> list, HostedGame value, int index, boolean isSelected, boolean cellHasFocus) {
        HostedGame game = value;
        JLabel lbl = new JLabel(game.getTitle());
        if (game.isStarted()) {
            lbl.setEnabled(false);
        }
        return lbl;
    }

}
