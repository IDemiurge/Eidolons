package main.client.net;

import main.client.DuelingClub;
import main.client.game.NetGame;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.core.game.HostedGame;
import main.game.logic.arena.UnitGroupMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.net.WaitingThread;
import main.system.net.chat.ChatConnector;
import main.system.net.socket.PORTS;
import main.system.net.user.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class DC_GameClient implements Runnable {
    private ChatConnector gameChatManager;
    private HostClientConnection connection;
    private GameConnector gameConnector;
    private String host;
    private String hostData;
    private String gameOptions;
    private NetGame testGame;

    public DC_GameClient(NetGame netGame, HostedGame game) {
        setTestGame(netGame);
        this.setGameConnector(new GameConnector(game));
        getGameConnector().setClient(this);
        this.setHost(game.getHostIP());
        Socket socket = new Socket();

        try {
            socket = new Socket(getHost(), PORTS.MAIN_GAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connection = new HostClientConnection(socket, getGameConnector());

        new WaitingThread(HOST_CLIENT_CODES.RANDOM).waitForInput();
        String input = WaitingThread.getINPUT(HOST_CLIENT_CODES.RANDOM);
        RandomWizard.setRandomGenerator(new Random(new Long(input)));
        connection.send(HOST_CLIENT_CODES.POWER_LEVEL);
        if (new WaitingThread(HOST_CLIENT_CODES.POWER_LEVEL).waitForInput()) {
            Integer powerLevel = StringMaster.getInteger(WaitingThread
                    .getINPUT(HOST_CLIENT_CODES.POWER_LEVEL));
            if (powerLevel > 0) {
                UnitGroupMaster.setFactionMode(true);
                if (powerLevel > UnitGroupMaster.LEADER_REQUIRED) {
                    UnitGroupMaster.setFactionLeaderRequired(true);
                    powerLevel -= UnitGroupMaster.LEADER_REQUIRED;
                }
                UnitGroupMaster.setPowerLevel(powerLevel);
            }
        }
        connection.send(HOST_CLIENT_CODES.FACING_MAP);
        new WaitingThread(HOST_CLIENT_CODES.FACING_MAP).waitForInput();
        input = WaitingThread.getINPUT(HOST_CLIENT_CODES.FACING_MAP);

        getGameConnector().setFacingMap(
                new MapMaster<Integer, FACING_DIRECTION>().getIdMapFromNetString(input,
                        FACING_DIRECTION.class));
    }

    public ChatConnector getGameChatManager() {
        return gameChatManager;
    }

    public void setGameChatManager(ChatConnector gameChatManager) {
        this.gameChatManager = gameChatManager;
    }

    public HostClientConnection getConnection() {
        return connection;
    }

    public void setConnection(HostClientConnection connection) {
        this.connection = connection;
    }

    public GameConnector getGameConnector() {
        return gameConnector;
    }

    public void setGameConnector(GameConnector gameConnector) {
        this.gameConnector = gameConnector;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void run() {
        this.setConnection(connection);
        this.setGameChatManager(ChatConnector.newInstance());
        getGameChatManager().initChatClient(getHost());

        getGameConnector().setConnection(connection);
        // getTestGame().getHostedGame().getGame().playerJoined();
        if (getTestGame().isFastMode()) {
            return;
        }

        gameConnector.send(HOST_CLIENT_CODES.HOST_USER_DATA_REQUEST);
        if (new WaitingThread(HOST_CLIENT_CODES.HOST_USER_DATA_REQUEST).waitForInput()) {
            this.hostData = WaitingThread.getINPUT(HOST_CLIENT_CODES.HOST_USER_DATA_REQUEST);
            initHostUser();
        }
        if (testGame != null) {
            this.gameOptions = testGame.getTestingOptions();
        } else {
            gameConnector.send(HOST_CLIENT_CODES.GAME_OPTIONS_REQUEST);
            if (new WaitingThread(HOST_CLIENT_CODES.GAME_OPTIONS_REQUEST).waitForInput()) {
                this.gameOptions = WaitingThread.getINPUT(HOST_CLIENT_CODES.GAME_OPTIONS_REQUEST);
            }
        }
        DuelingClub.newGameLobby(this);
    }

    private void initHostUser() {
        gameConnector.getHostedGame().setHost(new User(hostData));
    }

    public void connect() {
        new Thread(this, "Client Connect").start();
    }

    public NetGame getTestGame() {
        return testGame;
    }

    public void setTestGame(NetGame testGame) {
        this.testGame = testGame;
    }
}
