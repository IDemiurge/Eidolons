package main.client.game;

import main.client.lobby.DC_Lobby;
import main.client.net.DC_GameClient;
import main.client.net.DC_GameHost;
import main.client.net.GameConnector;
import main.client.net.HostClientConnection;
import main.data.filesys.PathFinder;
import main.game.core.game.HostedGame;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.data.FileManager;
import main.system.net.user.User;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.frontend.FAST_DC;

public class NetGame {
    // ip,
    // etc
    private static final String READY = "rdy boyz";
    private static final String DEFAULT_HOST_IP = "109.111.179.1";
    // TODO init user in Server Connector? GET RID OF ALL STATICS!!!
    private static String HOST_DATA = "HOST_NAME:" + User.HOST + ";HOST_IP:" + User.LOCAL_HOST
            + ";TITLE:Game1;"; // host
    private HostedGame hostGame;
    private HostedGame clientGame;
    private DC_GameHost gameHost;
    private DC_GameClient gameClient;
    private DC_Lobby clientLobby;
    private DC_Lobby hostLobby;
    private boolean host;
    private boolean testMode;
    private boolean fastMode;
    private boolean localhost;
    private String lastIpPath = PathFinder.getTextPath() + "net\\" + "last ip.txt";

    public NetGame(boolean host) {
        this.host = host;
        localhost = FAST_DC.LOCALHOST;
        fastMode = FAST_DC.NET_FAST_MODE;
    }

    public static String getGameData() {
        return HOST_DATA;
    }

    public static String getReady() {
        return READY;
    }

    public void init() {
        if (host) {
            hostInit();
        } else {
            clientInit();
        }
    }

    public String getTestingOptions() {
        return "";
    }

    private void hostInit() {
        hostGame = new HostedGame(HOST_DATA, false, true);
        gameHost = new DC_GameHost(new GameConnector(hostGame));
        if (testMode) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.TEST_MODE, READY);
        }
        gameHost.setTestGame(this);
    }

    private void clientInit() {
        if (testMode) {
            WaitMaster.waitForInput(WAIT_OPERATIONS.TEST_MODE);
        }
        if (!localhost) {

            String lastIp = FileManager.readFile(lastIpPath);
            if (lastIp.isEmpty()) {
                lastIp = DEFAULT_HOST_IP;
            }
            String ip = DialogMaster.inputText("ip?", lastIp);
            HOST_DATA = "HOST_NAME:" + User.HOST + ";HOST_IP:" + ip + ";TITLE:Game1;";
            FileManager.write(ip, lastIpPath);
        }
        this.clientGame = new HostedGame(HOST_DATA, false, false);

        clientGame.setNetGame(this);
        gameClient = clientGame.join();
        gameClient.connect();
    }

    public HostClientConnection getConnection() {
        if (gameHost != null) {
            return gameHost.getGameConnector().getConnection();
        }
        if (gameClient != null) {
            return gameClient.getGameConnector().getConnection();
        }
        return null;
    }

    public void hostProceed() {
        hostLobby = new DC_Lobby(gameHost);
        WaitMaster.receiveInput(WAIT_OPERATIONS.TEST_MODE, READY);
        hostLobby.getGameStarter().hostInit();
        WaitMaster.receiveInput(WAIT_OPERATIONS.TEST_GAME_STARTED, READY);

    }

    public void clientProceed() {
        WaitMaster.waitForInput(WAIT_OPERATIONS.TEST_MODE);
        clientLobby = new DC_Lobby(gameClient);
        WaitMaster.waitForInput(WAIT_OPERATIONS.TEST_GAME_STARTED);
        clientLobby.getGameStarter().clientInit();
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public HostedGame getHostedGame() {
        if (hostGame == null) {
            return clientGame;
        }
        return hostGame;
    }

    public HostedGame getClientGame() {
        return clientGame;
    }

    public DC_GameHost getGameHost() {
        return gameHost;
    }

    public DC_GameClient getGameClient() {
        return gameClient;
    }

    public DC_Lobby getClientLobby() {
        return clientLobby;
    }

    public DC_Lobby getHostLobby() {
        return hostLobby;
    }

    public boolean isHost() {
        return host;
    }

    public boolean isFastMode() {
        return fastMode;
    }

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

}
