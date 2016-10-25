package main.client.lobby;

import main.client.gui.main.ChatPanel;
import main.client.gui.main.UserListPanel;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.system.net.RefresherImpl.REFRESHER_TYPE;
import main.system.net.user.UserList;

public class DC_LobbyBuilder extends Builder {
    GameInfoPanel gameInfoPanel;
    UserListPanel userListPanel;
    ChatPanel chatPanel;
    LobbyControlPanel controlPanel;
    private DC_Lobby lobby;

    public DC_LobbyBuilder(DC_Lobby lobby) {
        this.lobby = lobby;

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        gameInfoPanel = new GameInfoPanel(lobby.isHost(), lobby.getGame());
        gameInfoPanel.setConnector(lobby.getConnector());
        UserList userList = new UserList(lobby.isHost(), lobby.getConnector());
        // I am host, so w.t.f?...
        if (!lobby.isHost()) {
            userList.setCode(HOST_CLIENT_CODES.HOST_USER_DATA_REQUEST);
            userList.setType(REFRESHER_TYPE.USERLIST_GAME);
            userList.launchUserListRefreshingThread();
        }
        userListPanel = new UserListPanel(userList);

        if (lobby.isHost()) {
            chatPanel = new ChatPanel(true);
            controlPanel = new LobbyControlPanel(lobby, true);
        } else {
            chatPanel = new ChatPanel(lobby.getClient().getHost());
            controlPanel = new LobbyControlPanel(lobby);
        }

        compArray = new G_Component[]{userListPanel, chatPanel,
                gameInfoPanel, controlPanel};
        cInfoArray = new String[]{

                // "id userListPanel, pos 0 0 container.x2/2 container.y2/2",
                // "id chatPanel, pos userListPanel.x2 0 container.x2-333 container.y2-333,  ",
                // "id gameInfoPanel, pos chatPanel.x2 0,  ",
                //
                // "id controlPanel, pos visual.x2 chatPanel.y2" +
                "", "w 400!, h 300!", "", "",};

    }

    public GameInfoPanel getGameInfoPanel() {
        return gameInfoPanel;
    }

    public void setGameInfoPanel(GameInfoPanel gameInfoPanel) {
        this.gameInfoPanel = gameInfoPanel;
    }

    public UserListPanel getUserListPanel() {
        return userListPanel;
    }

    public void setUserListPanel(UserListPanel userListPanel) {
        this.userListPanel = userListPanel;
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    public DC_Lobby getLobby() {
        return lobby;
    }

    public void setLobby(DC_Lobby lobby) {
        this.lobby = lobby;
    }

    public LobbyControlPanel getControlPanel() {
        return controlPanel;
    }

    public void setControlPanel(LobbyControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }
}
