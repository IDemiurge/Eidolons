package main.system.net.chat;

import main.client.DuelingClub;
import main.system.auxiliary.Err;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatManager {

    private static int chatPort = 7755;
    private static ChatConnection chathandler;

    public static void initChat() {
        initChat(null);
    }

    public static void initChat(ChatBox chatBox) {
        if (chathandler != null) {
            // return;
        }
        Socket socket = null;
        try {
            socket = new Socket(DuelingClub.SERVER_ADDRESS, chatPort);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (socket == null)
            Err.error("Failed to init chat connection!");
        else {
            chathandler = new ChatConnection(chatBox, socket);
        }
    }

    public static ChatConnection getChathandler() {
        return chathandler;
    }

    public static void setChathandler(ChatConnection chathandler) {
        ChatManager.chathandler = chathandler;
    }
}
