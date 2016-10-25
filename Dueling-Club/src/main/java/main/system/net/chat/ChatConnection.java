package main.system.net.chat;

import main.system.net.Codes.CHAT_CODES;
import main.system.net.socket.GenericConnection;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.NetCode;
import main.system.net.user.User;

import java.net.Socket;

public class ChatConnection extends GenericConnection {

    private ChatBox chatBox;
    private boolean isGameHost;
    private User user;
    private ChatConnector gameChatManager;

    public ChatConnection(Socket socket) {
        super(socket, CHAT_CODES.class);
        send(CHAT_CODES.ASSIGN_HANDLER);
        send(ServerConnector.getUser().getName());
        // if (new WaitingThread(
        // CHAT_CODES.CHAT_ARCHIVE_REQUEST).WaitForInput()) {

    }

    public ChatConnection(ChatBox chatBox, Socket socket) {
        this(socket);
        this.chatBox = chatBox;
    }

    public ChatConnection(ChatConnector gameChatManager,
                          User lastuser, boolean isHost, ChatBox chatBox2, Socket socket) {
        this(chatBox2, socket);
        this.isGameHost = isHost;
        this.user = lastuser;
        this.gameChatManager = gameChatManager;
    }

    public ChatConnection(Runnable runnable, User lastuser,
                          boolean isHost, ChatBox chatBox2, Socket accept) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handleInput(String input) {
        if (getLastReceivedCode() == CHAT_CODES.MESSAGE) {

            if (isGameHost)
                handleInputHost(input, user);
            else
                appendToChat(input);
        } else {
            CHAT_CODES code = (CHAT_CODES) getLastReceivedCode();
            setLastReceivedCode(CHAT_CODES.MESSAGE);
            switch (code) {
                case ASSIGN_HANDLER: {
                    handleInput(input);
                }
                default: {
                    break;
                }
            }
        }
    }

    public void handleInputHost(String input, User user2) {
        appendToChat(input);
        gameChatManager.appendToChat(input, user2);
    }

    // @Override
    // public void send(Object o) {
    //
    // }
    private void appendToChat(String input) {
        // if (chatBox == null)
        // chatBox = Menu.getLobby().getChatBox();
        chatBox.appendToChat(input);

    }

    @Override
    public void handleInputCode(NetCode codes) {
        // TODO Auto-generated method stub

    }

    public boolean isGameHost() {
        return isGameHost;
    }

    public void setGameHost(boolean isGameHost) {
        this.isGameHost = isGameHost;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatConnector getGameChatManager() {
        return gameChatManager;
    }

    public void setGameChatManager(ChatConnector gameChatManager) {
        this.gameChatManager = gameChatManager;
    }

    public ChatBox getChatBox() {
        return chatBox;
    }

    public void setChatBox(ChatBox chatBox) {
        this.chatBox = chatBox;
    }

}
