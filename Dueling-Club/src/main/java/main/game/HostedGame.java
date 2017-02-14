package main.game;

import main.client.game.NetGame;
import main.client.game.options.GameOptions;
import main.client.lobby.DC_Lobby;
import main.client.net.DC_GameClient;
import main.system.auxiliary.log.LogMaster;
import main.system.net.game.G_HostedGame;
import main.system.net.user.User;

public class HostedGame extends G_HostedGame {

    private GameOptions options;
    private User host;
    private DC_Lobby lobby;
    private NetGame netGame;

    public HostedGame(String string, boolean started) {
        super(string, started);
    }

    public HostedGame(String string, boolean started, boolean host) {
        super(string, started, host);
    }

    public DC_GameClient join() {
        LogMaster.log(1, "joining game " + getTitle());
        if (!started) {
            if (!isFull()) {
                this.joined = true;

                return new DC_GameClient(netGame, this);
            }
        }
        return null;
    }

    public GameOptions getOptions() {
        return options;
    }

    public void setOptions(GameOptions gameOptions) {
        this.options = gameOptions;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
        LogMaster.log(1, "game host: " + host.getData());
    }

    public DC_Lobby getLobby() {
        return lobby;
    }

    public void setLobby(DC_Lobby lobby) {
        this.lobby = lobby;
    }

    public void setNetGame(NetGame netGame) {
        this.netGame = netGame;

    }

}
