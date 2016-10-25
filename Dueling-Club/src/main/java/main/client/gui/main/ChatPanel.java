package main.client.gui.main;

import main.swing.generic.components.G_Panel;
import main.system.net.chat.ChatBox;
import main.system.net.chat.ChatConnection;
import main.system.net.chat.ChatConnector;
import main.system.net.chat.InputBox;

import javax.swing.*;

public class ChatPanel extends G_Panel {
    private static ChatBox theChatBox;
    InputBox inputBox;
    ChatBox chatBox;
    JTabbedPane tabbedPane;
    private ChatConnection handler;
    private ChatConnector chatConnector;
    private boolean host;
    private boolean mainChat;

    public ChatPanel(boolean host) {
        this.host = host;
        chatBox = new ChatBox();
        this.chatConnector = new ChatConnector();
        chatConnector.setChatBox(chatBox);
        initGameChatInputBox();
        addElements();

    }

    public ChatPanel(String address, boolean b) {
        this.mainChat = b;
        chatBox = new ChatBox();
        this.chatConnector = new ChatConnector();
        chatConnector.setChatBox(chatBox);
        chatConnector.initChatClient(address, mainChat);
        this.handler = chatConnector.getChathandler();
        inputBox = new InputBox(handler);
        addElements();
    }

    public ChatPanel(String address) {
        this(address, false);

    }

    private void initGameChatInputBox() {
        if (!mainChat)
            try {
                chatConnector.initGameChatServer();
            } catch (Exception e) {
                main.system.auxiliary.LogMaster
                        .log(5, "failed to init chat server...");
                return;
            }
        inputBox = new InputBox(host, chatConnector);
    }

    private void addElements() {
        add(chatBox, "id chat, pos 0 0 container.x2 container.y2-150 ");

        if (inputBox != null)
            add(inputBox, "id input, pos 0 chat.y2 container.x2 container.y2 ");

    }

}
