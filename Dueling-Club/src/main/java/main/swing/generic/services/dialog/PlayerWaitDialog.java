package main.swing.generic.services.dialog;

import main.client.net.DC_GameHost;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.client.net.HostClientConnection;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class PlayerWaitDialog extends ModalDialog {

    List<Object> playerList = new LinkedList<>();
    DC_GameHost host;
    private HostClientConnection connection;
    private boolean joined;

    public PlayerWaitDialog(HostClientConnection connection) {
        this.connection = connection;
        host.getGameConnector().getConnections();
    }

    public PlayerWaitDialog(DC_GameHost gameHost) {
        gameHost.setWaitDialog(this);
        this.host = gameHost;
    }

    public void joined() {
        joined = true;

    }

    @Override
    protected boolean isAlwaysOnTop() {
        return false;
    }

    public void playerReady(int n, String data) {
        // player = playerList.getOrCreate(n);
        // player.initData(data);
        // playerList.remove(player);
    }

    protected void abort() {
        // host.getGameConnector().sendToAll(ABORT);

    }

    protected boolean isAutoClose() {
        return true;
    }

    @Override
    protected boolean isCloseConditionMet() {
        // set expected player numbers
        if (host.getGameConnector().getConnection() != null) {
            return true;
        }

        if (!joined) {
            return false;
        }

        return playerList.isEmpty();
    }

    protected void sendReady() {
        connection.send(HOST_CLIENT_CODES.CLIENT_READY);

    }

    @Override
    public Component createComponent() {
        return new TextCompDC(VISUALS.INFO_PANEL_WIDE, "Wait for the opponent!", 20, FONT.AVQ);
        // final CustomButton abortButton = new CustomButton(null, "") {
        // @Override
        // public void handleClick() {
        // abort();
        // }
        // };
        //
        // final CustomButton rdyButton = new CustomButton(null, "") {
        // @Override
        // public void handleClick() {
        // sendReady();
        // }
        // };
        // String text = "Wait for other players...";
        //
        // final TextComp statusPanel = new TextComp(null, text);
        // final G_Panel playersPanel = new G_Panel();
        // final G_Panel panel = new G_Panel() {
        // @Override
        // public void refresh() {
        //
        // super.refresh();
        // }
        // };
        // panel.add(statusPanel);
        // panel.add(playersPanel);
        // panel.add(rdyButton);
        // panel.add(abortButton);
        // // component per player
        // new Thread(new Runnable() {
        // public void run() {
        // while (true) {
        // if (isCloseConditionMet())
        // break;
        // WaitMaster.WAIT(50);
        // panel.refresh();
        // }
        // }
        // }, "refresh thread").start();
        //
        // return panel;
    }

}
