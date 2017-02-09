package main.client.net;

import main.client.game.NetGame;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.swing.generic.services.dialog.PlayerWaitDialog;
import main.system.auxiliary.RandomWizard;
import main.system.net.GameHost;
import main.system.net.chat.ChatConnector;
import main.system.net.socket.PORTS;
import main.system.net.user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DC_GameHost extends GameHost implements Runnable {

    private GameConnector gameConnector;
    private ChatConnector gameChatManager;
    private NetGame testGame;
    private PlayerWaitDialog waitDialog;

    //
    public DC_GameHost(GameConnector gameConnector) {

        this.gameConnector = gameConnector;
        gameConnector.setHost(true);
        gameConnector.setGameHost(this);
        gameChatManager = new ChatConnector();
        try {
            hostServer = new ServerSocket(PORTS.MAIN_GAME);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // gamechatServer = new ServerSocket(gamechatport);
        // new game lobby (this)

        listenForUserConnection();

    }

    protected void listenForUserConnection() {
        new Thread(this).start();

    }

    public GameConnector getGameConnector() {
        return gameConnector;
    }

    public void setGameConnector(GameConnector gameConnector) {
        this.gameConnector = gameConnector;
    }

    public ChatConnector getGameChatManager() {
        return gameChatManager;
    }

    public void setGameChatManager(ChatConnector gameChatManager) {
        this.gameChatManager = gameChatManager;
    }

    public void setWaitDialog(PlayerWaitDialog playerWaitDialog) {
        waitDialog = playerWaitDialog;

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            Socket socket = null;
            try {
                socket = hostServer.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue; // TODO return?
            }
            joined(socket);
        } // socket1 = gamechatServer.accept();

    }

    private void joined(Socket socket) {
        final HostClientConnection hcc = new HostClientConnection(true, socket, gameConnector);
        User user = hcc.getUser();
        if (user != null) {
            gameChatManager.setLastuser(user);

        }
        // if (!DuelingClub.TEST_MODE)

        // else
        gameConnector.addConnection(hcc.getUser(), hcc);

        // TODO multiple games plz
        getGameConnector().setConnection(hcc);
        if (testGame != null) {
            if (testGame.isTestMode()) {
                testGame.hostProceed();
            }
        }
        new Thread(new Runnable() {
            public void run() {
                String data = null;

                if (!testGame.isFastMode()) {
                    data = hcc.sendAndWaitForResponse(HOST_CLIENT_CODES.CLIENT_DATA_REQUEST, null);
                }
                if (waitDialog != null) {
                    waitDialog.joined();
                }

                gameConnector.getGame().playerJoined(hcc, data);
                gameConnector.send(HOST_CLIENT_CODES.RANDOM, RandomWizard.seed + "");
                // if (UnitGroupMaster.getPowerLevel() != null)
                // gameConnector.send(HOST_CLIENT_CODES.CUSTOM_PICK,
                // UnitGroupMaster
                // .getPowerLevel()
                // + "");

                // gameConnector.getConnection().send(
                // HOST_CLIENT_CODES.FACING_MAP,
                // MapMaster.getNetStringForMap(gameConnector.getGame().getArenaManager()
                // .getSpawnManager().getMultiplayerFacingMap()));
            }
        }, " thread").start();

    }

    public NetGame getTestGame() {
        return testGame;
    }

    public void setTestGame(NetGame testGame) {
        this.testGame = testGame;
    }

}
