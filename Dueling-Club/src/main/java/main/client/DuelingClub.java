package main.client;

import main.client.game.TestMode;
import main.client.lobby.DC_Lobby;
import main.client.net.DC_GameClient;
import main.client.net.DC_GameHost;
import main.client.net.DC_ServerConnector;
import main.data.filesys.PathFinder;
import main.system.auxiliary.GuiManager;
import main.system.net.socket.ServerConnection;
import main.system.net.socket.ServerConnector;
import main.system.net.user.User;

import java.awt.*;

public class DuelingClub {

    public static final String GAME_TITLE = "Death Combat";
    public final static boolean ADMIN_MODE = true;
    public final static boolean PRESENTATION_MODE = false;
    public final static boolean TEST_MODE = true;
    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final String ADMIN_LOGIN = "sdf";
    public static final String ADMIN_PASSWORD = "sdf";
    private static ServerConnection handler;
    private static boolean arcaneVaultRunning = false;
    private static DC_MainMenu menu;
    private static User USER;

    public static void main(String[] args) {
        PathFinder.setPresentationMode(PRESENTATION_MODE);
        DC_Engine.systemInit();

        if (TEST_MODE) {
            Boolean host = null;
            if (args != null) {
                if (args.length >= 1) {
                    if (args[0].equals("client")) {
                        host = false;
                    }
                    if (args[0].equals("host")) {
                        host = true;
                    }

                }
            }
            TestMode.launch(host);

        } else {
            menu = new DC_MainMenu();

            menu.createAndShowGUI();
            DC_Lobby.setSIZE(new Dimension(GuiManager.getScreenSize().width / 2, GuiManager
                    .getScreenSize().height / 2));
        }
    }

    public static void login(DC_MainMenu menu, String data) {
        if (ServerConnector.getSocket() == null) {
            main.system.auxiliary.LogMaster.log(1, "login connect + " + SERVER_ADDRESS);
            handler = DC_ServerConnector.connect(SERVER_ADDRESS);
            if (handler != null) {
                main.system.auxiliary.LogMaster.log(1, "CONNECTED");
            } else {
                return;
            }
        }

        if (ServerConnector.checkUser(data)) {
            main.system.auxiliary.LogMaster.log(1, "USER CHECK SUCCESSFUL");
            if (ServerConnector.requestUserData())

            {
                main.system.auxiliary.LogMaster.log(1, "USER DATA RECEIVED");

                DC_MainMenu.setHandler(handler);
                menu.setMainView();
            }
        }
    }

    public static void setArcaneVaultRunning(boolean b) {
        arcaneVaultRunning = b;
    }

    public static void newGameLobby(DC_GameClient client) {
        if (TEST_MODE) {
            client.getTestGame().clientProceed();
        }
        new DC_Lobby(client).createAndShowGUI();

    }

    public static void newGameLobby(DC_GameHost host) {

        new DC_Lobby(host).createAndShowGUI();

    }

}
