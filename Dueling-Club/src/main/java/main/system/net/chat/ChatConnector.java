package main.system.net.chat;

import main.system.auxiliary.StringMaster;
import main.system.net.socket.PORTS;
import main.system.net.user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatConnector implements Runnable {
    protected boolean rdy = false;
    StringBuffer buffer = new StringBuffer();
    User lastuser;
    private ConcurrentLinkedQueue<ChatConnection> chathandlers = new ConcurrentLinkedQueue<>();
    private ServerSocket chatServer;
    private boolean isHost = false;
    private ChatConnection chathandler;
    private ChatBox chatBox;

    public static ChatConnector newInstance() {
        return new ChatConnector();
    }

    public void appendToChat(String input, User user) {
        Calendar c = Calendar.getInstance();
        // .getTime()..getTime();
        String data = "";
        String time = "" + c.get(Calendar.HOUR_OF_DAY);
        time += ":" + c.get(Calendar.MINUTE);
        time += ":" + c.get(Calendar.SECOND);
        data += time + " ";
        data += user.getName() + StringMaster.getPairSeparator() + input;
        buffer.append(data);
        sendToAll(data);
    }

    public void sendToAll(String s) {
        for (ChatConnection ch : chathandlers) {
            ch.send(s);

        }
    }

    public void initGameChatServer() throws IOException {
        isHost = true;
        chatServer = new ServerSocket(PORTS.GAME_CHAT);

        new Thread(this).start();

    }

    private void listenForChatConnections() {
        final ChatConnector varholder = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    if (rdy) {
                        try {
                            chathandlers.add(new ChatConnection(
                                    varholder, lastuser, isHost, chatBox,
                                    chatServer.accept()));
                            rdy = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }, "ChatServer").start();
    }

    public ConcurrentLinkedQueue<ChatConnection> getChathandlers() {
        return chathandlers;
    }

    public void setChathandlers(ConcurrentLinkedQueue<ChatConnection> chathandlers) {
        this.chathandlers = chathandlers;
    }

    public ServerSocket getChatServer() {
        return chatServer;
    }

    public void initChatClient(String host) {
        try {
            initChatClient(host, false);
        } catch (Exception e) {
            // e.printStackTrace();
            return;
        }
    }

    public void initChatClient(String host, boolean main) {
        // if (chatBox == null)
        // chatBox = lobby.getChatBox();
        Socket socket;
        try {
            socket = new Socket(host,
                    // (host.equals(DuelingClub.SERVER_ADDRESS)
                    (main) ? PORTS.SERVER_MAIN_CHAT : PORTS.GAME_CHAT);
        } catch (UnknownHostException e) {
            // e.printStackTrace();
            return;
        } catch (IOException e) {

            // e.printStackTrace();
            return;
        }

        setChathandler(new ChatConnection(chatBox, socket));

    }

    public void setChatHandler(ChatConnection gamechathandler) {
        this.setChathandler(gamechathandler);
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public User getLastuser() {
        return lastuser;
    }

    public void setLastuser(User lastuser) {
        rdy = true;
        this.lastuser = lastuser;
    }

    public boolean isRdy() {
        return rdy;
    }

    public void setRdy(boolean rdy) {
        this.rdy = rdy;
    }

    public ChatBox getChatBox() {
        return chatBox;
    }

    public void setChatBox(ChatBox chatBox) {
        this.chatBox = chatBox;
    }

    public ChatConnection getChathandler() {
        return chathandler;
    }

    public void setChathandler(ChatConnection chathandler) {
        this.chathandler = chathandler;
    }

    @Override
    public void run() {
        listenForChatConnections();

    }
}
