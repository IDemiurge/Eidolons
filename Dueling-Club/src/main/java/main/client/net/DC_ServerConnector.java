package main.client.net;

import main.system.net.socket.PORTS;
import main.system.net.socket.ServerConnection;
import main.system.net.socket.ServerConnector;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class DC_ServerConnector extends ServerConnector {

    public static boolean checkUser(String name) {
        handler.send(CODES.USER_VALIDATION);
        handler.send(name);

        return launchWaitingThread(CODES.USER_VALIDATION);
        // JOptionPane.showMessageDialog(null, "Invalid password!",
        // "Sorry!", JOptionPane.ERROR_MESSAGE);
        // return false;

    }

    public static ServerConnection connect(String serverAddress) {
        return new DC_ServerConnector().connectThis(serverAddress);
    }

    public ServerConnection connectThis(String host1) {

        host = host1;
        try {
            // System.out.println("new socket");
            socket = new Socket(host, PORTS.MAIN);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socket != null) {

            handler = new DC_ServerConnectionHandler(socket);
            main.system.auxiliary.LogMaster.log(4, "new handler!");
            return handler;

        } else {
            int retry = JOptionPane
                    .showConfirmDialog(null, "Should I keep trying to connect?", "Failed to connect to the server! ("
                            + host + ")", JOptionPane.ERROR_MESSAGE);
            if (retry != JOptionPane.YES_OPTION)
                return null;
            main.system.auxiliary.LogMaster
                    .log(4, "Retrying every 5 seconds...");
            while (socket == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectThis(host);
            }
        }
        return null;
    }

}
