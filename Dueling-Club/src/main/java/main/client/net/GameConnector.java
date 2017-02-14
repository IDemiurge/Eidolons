package main.client.net;

import main.client.DuelingClub;
import main.client.game.GameStarter;
import main.client.lobby.DC_Lobby;
import main.game.DC_Game;
import main.game.HostedGame;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.event.MessageManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.net.Communicator;
import main.system.net.WaitingThread;
import main.system.net.data.PlayerData;
import main.system.net.socket.Connector;
import main.system.net.socket.ServerConnector.NetCode;
import main.system.net.user.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameConnector implements Connector {

    private static final String HERO_CHOICE = "Are you ready to choose your hero?";
    HostClientConnection connection;
    ConcurrentHashMap<User, HostClientConnection> connections = new ConcurrentHashMap<>();
    private boolean host = false;
    private HostedGame hostedGame;
    private DC_Game game;

    private String playerData;
    private boolean ready = false;
    private DC_Lobby lobby;
    private DC_GameClient client;
    private Communicator communicator;
    private DC_GameHost gameHost;
    private Map<Integer, FACING_DIRECTION> facingMap;

    public GameConnector(HostedGame game) {
        this.setGame(game);
        if (game.getHost() != null) {
            playerData = game.getHost().getData();
        }
    }

    public void send(HOST_CLIENT_CODES code, String data) {
        if (!host) {
            connection.send(code, data);
        } else {
            for (HostClientConnection c : connections.values()) {
                c.send(code, data);
            }
        }
    }

    public void send(NetCode code) {
        if (!host) {
            connection.send(code);
        } else {
            for (HostClientConnection c : connections.values()) {
                c.send(code);
            }
        }
    }

    public void send(Object o) {
        if (!host) {
            connection.send(o);
        } else {
            for (HostClientConnection c : connections.values()) {
                c.send(o);
            }
        }
    }

    public void checkReady() {
        if (ready) {
            sendReady();
            return;
        }
        if (promptReady()) {
            setReady(true);
            sendReady();
        } else {
            setReady(false);
            sendNotReady();
        }
    }

    public boolean promptReady() {
        if (DuelingClub.TEST_MODE) {
            return makeReady();
        }
        if (MessageManager.confirm(HERO_CHOICE)) {
            return makeReady();
        }
        return false;
    }

    public boolean chooseHero() {
        if (lobby == null) {
            return true;
        }
        int result = lobby.getGameStarter().chooseHero(hostedGame.getOptions());
        if (result == GameStarter.HERO_SELECTION_CANCELLED) {
            return false;
        } else {
            return true;
        }

    }

    public void sendMapData() {
        send(HOST_CLIENT_CODES.MAP_DATA_REQUEST);
        send(lobby.getGameStarter().getMapData().getData());
        LogMaster.log(4, "MAP_DATA_REQUEST "
                + lobby.getGameStarter().getMapData().getData());

    }

    public void sendPartyData() {
        if (isHost()) {
            send(HOST_CLIENT_CODES.HOST_PARTY_DATA);
        } else {
            send(HOST_CLIENT_CODES.CLIENT_PARTY_DATA_REQUEST);
        }
        send(lobby.getGameStarter().getMyPartyData().getData());
    }

    private void sendReady() {
        send(HOST_CLIENT_CODES.CHECK_READY);
        send(GameStarter.READY);
    }

    private void sendNotReady() {
        send(HOST_CLIENT_CODES.CHECK_READY);
        send(GameStarter.NOT_READY);
    }

    public void addConnection(User user, HostClientConnection hostClientConnection) {
        connections.put(user, hostClientConnection);
        playerData += user.getRelevantData() + StringMaster.getDataUnitSeparator();
        if (lobby == null) {
            return;
        }
        lobby.getUserList().setData(playerData);
        LogMaster.log(4, "====>> User connected " + user.getData()
                + " player data = " + playerData);

        lobby.getGameStarter().setPlayerData(new PlayerData(user.getRelevantData()));

    }

    public void sendReply() {
        connection.sendReply();
    }

    public void setLobby(DC_Lobby lobby) {
        this.lobby = lobby;
    }

    public HostClientConnection getConnection() {
        return connection;
    }

    // TODO ++ remove connection
    public void setConnection(HostClientConnection hostClientConnection) {

        connection = hostClientConnection;

    }

    public ConcurrentHashMap<User, HostClientConnection> getConnections() {
        return connections;
    }

    public void setConnections(ConcurrentHashMap<User, HostClientConnection> connections) {
        this.connections = connections;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean isHost) {
        this.host = isHost;
    }

    public String getPlayerData() {
        return playerData;
    }

    public void setPlayerData(String userdata) {
        this.playerData = userdata;
    }

    public HostedGame getHostedGame() {
        return hostedGame;
    }

    public void setGame(HostedGame game) {
        this.hostedGame = game;
    }

    public boolean isReady() {
        return ready;

    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public DC_GameHost getGameHost() {
        return gameHost;
    }

    public void setGameHost(DC_GameHost dc_GameHost) {
        gameHost = dc_GameHost;
    }

    public DC_GameClient getClient() {
        return client;
    }

    public void setClient(DC_GameClient client) {
        this.client = client;
    }

    public boolean isWaiting() {
        return StringMaster.isEmpty(WaitingThread.getInputMap().get(HOST_CLIENT_CODES.CHECK_READY));
        // return getConnections().isEmpty();
    }

    public boolean makeReady() {
        boolean b = chooseHero();
        setReady(b);
        return b;
    }

    public void awaitCommand(NetCode code) {
        game.getCommunicator().awaitCommand(code);
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public Map<Integer, FACING_DIRECTION> getFacingMap() {
        return facingMap;
    }

    public void setFacingMap(Map<Integer, FACING_DIRECTION> idMapFromNetString) {
        facingMap = idMapFromNetString;

    }

    public enum HOST_CLIENT_CODES implements NetCode {
        CLIENT_PARTY_DATA_REQUEST,
        NEW_USER_DATA,
        HOST_USER_DATA_REQUEST,
        GAME_USERS_LIST_CHANGED,
        GAME_OPTIONS_CHANGED,
        GAME_OPTIONS_REQUEST,
        CHECK_READY,
        NEW_USER_DATA_REQUEST,
        HOST_READY,
        HOST_PARTY_DATA,
        GAME_JOIN_FAILED,
        GAME_JOINED,
        MAP_DATA_REQUEST,
        CLIENT_READY,
        GAME_STARTED,
        ID_REQUEST,
        COMMAND,
        PRECOMBAT_DATA,
        GAME_DATA_REQUEST,
        CLIENT_DATA_REQUEST,
        RANDOM,
        FACING_MAP,
        CUSTOM_PICK,
        POWER_LEVEL,;

        @Override
        public boolean isInputIrrelevant() {
            return false;
        }

    }

}
