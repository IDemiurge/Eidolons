package main.client.lobby;

import main.client.game.GameStarter;
import main.client.net.DC_GameClient;
import main.client.net.DC_GameHost;
import main.client.net.GameConnector;
import main.game.core.game.HostedGame;
import main.system.graphics.GuiManager;
import main.system.net.user.User;
import main.system.net.user.UserList;

import javax.swing.*;
import java.awt.*;

/**
 * @author JustMe
 */
public class DC_Lobby {

    private static Dimension SIZE;
    private HostedGame game;
    private DC_GameHost host;
    private DC_GameClient client;
    private User user;
    private DC_LobbyBuilder builder;
    private JComponent comp;
    private JFrame window;
    private GameStarter gameStarter;
    private GameConnector gameConnector;

    public DC_Lobby(DC_GameHost host) {
        this.gameConnector = host.getGameConnector();
        this.setGame(host.getGameConnector().getHostedGame());
        this.setHost(host);
        this.setUser(host.getGameConnector().getConnection().getUser());
        this.builder = new DC_LobbyBuilder(this);
        gameConnector.setLobby(this);
        this.setGameStarter(new GameStarter(getGame(), gameConnector));

    }

    public DC_Lobby(DC_GameClient client) {
        this.setClient(client);
        this.gameConnector = client.getGameConnector();
        gameConnector.setLobby(this);
        this.setGame(client.getGameConnector().getHostedGame());
        this.setUser(client.getConnection().getUser());
        this.builder = new DC_LobbyBuilder(this);
        this.setGameStarter(new GameStarter(getGame(), gameConnector));

    }

    public static Dimension getSIZE() {
        return SIZE;
    }

    public static void setSIZE(Dimension sIZE) {
        SIZE = sIZE;
    }

    public void createAndShowGUI() {
        if (comp == null) {
            builder.init();
            comp = builder.build();
        }
        window = GuiManager.inNewWindow(comp, getGame().getTitle(), getSIZE());

        // add window listener
    }

    private void startButtonClicked() {
        gameStarter.startButtonClicked();

    }

    public void closeLobby() {
        window.setVisible(false);
        window.dispose();

    }

    public void handleCommand(String command) {
        switch (command) {
            case "Start": {
                startButtonClicked();
                break;
            }
            case "Choose Hero": {
                gameConnector.makeReady();

                break;
            }
        }

    }

    public boolean isHost() {
        return (getHost() != null);
    }

    public DC_GameClient getClient() {
        return client;
    }

    public void setClient(DC_GameClient client) {
        this.client = client;
    }

    public DC_GameHost getHost() {
        return host;
    }

    public void setHost(DC_GameHost host) {
        this.host = host;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameStarter getGameStarter() {
        return gameStarter;
    }

    public void setGameStarter(GameStarter gameStarter) {
        this.gameStarter = gameStarter;
    }

    public GameConnector getConnector() {
        return gameConnector;
    }

    public UserList getUserList() {
        return builder.getUserListPanel().getUserList();
    }

    public HostedGame getGame() {
        return game;
    }

    public void setGame(HostedGame game) {
        this.game = game;
        game.setLobby(this);
    }
}
