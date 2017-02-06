package main.system.net.socket;

import main.system.auxiliary.Err;
import main.system.net.Viewer;
import main.system.net.WaitingThread;
import main.system.net.user.User;

import javax.swing.*;
import java.net.Socket;
import java.util.*;

public abstract class ServerConnector {

    protected static String host;
    protected static Socket socket;
    protected static User user;
    protected static ServerConnection handler;

    protected static Viewer viewer;

    protected static Map<NetCode, Boolean> success = new HashMap<NetCode, Boolean>();
    protected static Map<NetCode, Boolean> failure = new HashMap<NetCode, Boolean>();

    protected static Object TempUserName;

    public static boolean requestUserData() {
        send(CODES.USER_DATA_REQUEST);
        send(TempUserName);

        if (launchInputWaitingThread(CODES.USER_DATA_REQUEST)) {
            String userData = WaitingThread.getINPUT(CODES.USER_DATA_REQUEST);

            setUser(new User(userData));
            String string = user.getData();
            if (viewer != null) {
                viewer.info(string);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkUser(String name) {
        handler.send(CODES.USER_VALIDATION);
        handler.send(name);

        return launchWaitingThread(CODES.USER_VALIDATION);
        // JOptionPane.showMessageDialog(null, "Invalid password!",
        // "Sorry!", JOptionPane.ERROR_MESSAGE);
        // return false;

    }

    public static boolean launchWaitingThread(NetCode c) {
        return (new WaitingThread(c)).Wait();

    }

    public static boolean launchInputWaitingThread(NetCode c) {
        return (new WaitingThread(c)).waitForInput();

    }

    public static void handleUserCheckResult(String name) {
        final CODES code = CODES.USER_VALIDATION;
        try {
            Integer i = Integer.valueOf(name);

            switch (i) {
                case 0: {

                    success.put(code, true);

                    return;
                }
                case 1: {
                    failure.put(code, true);
                    Err.error("Invalid user data!");
                    return;
                }
                case 2: {
                    failure.put(code, true);
                    Err.warn("No such user!");
                    return;
                }
                case 3: {
                    failure.put(code, true);
                    Err.warn("Wrong password!");
                    return;
                }
                case 4: {
                    send(CODES.USER_ACTIVATION_CODE_REQUEST);

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if (launchInputWaitingThread(CODES.USER_ACTIVATION_CODE)) {
                                String acode = WaitingThread
                                        .getINPUT(CODES.USER_ACTIVATION_CODE);
                                String input = JOptionPane
                                        .showInputDialog("Enter the code you received in your confirmation email...");
                                if (acode.equals(input)) {
                                    send(CODES.USER_ACTIVATION_SUCCESSFULL);

                                    success.put(code, true);
                                    return;
                                }
                            }
                        }

                    }, "validator").start();

                }
            }

        } catch (NumberFormatException e) {
            Err.error("User data check error - received inconsistent data");
        }
    }

    public static void send(CODES code) {
        handler.send(code);
    }

    public static void send(Object o) {
        handler.send(o);
    }

    public static Boolean isSuccess(NetCode code) {
        Boolean b = success.get(code);
        return (b == null) ? false : b;
    }

    public static void setSuccess(boolean success, NetCode code) {
        ServerConnector.success.put(code, success);
    }

    public static Boolean isFailure(NetCode code) {
        Boolean b = failure.get(code);
        return (b == null) ? false : b;
    }

    public static void setFailure(boolean failure, NetCode code) {
        ServerConnector.failure.put(code, failure);
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ServerConnector.user = user;
    }

    public static void setTempUserName(String username) {
        TempUserName = username;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        ServerConnector.host = host;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        ServerConnector.socket = socket;
    }

    public static ServerConnection getHandler() {
        return handler;
    }

    public static void setHandler(ServerConnection handler) {
        ServerConnector.handler = handler;
    }

    public static Viewer getViewer() {
        return viewer;
    }

    public static void setViewer(Viewer viewer) {
        ServerConnector.viewer = viewer;
    }

    public abstract ServerConnection connectThis(String host1);

    public enum CODES implements NetCode {
        REFRESH_GAME_LIST,
        PARTY_XML_REQUEST,
        PARTY_XML_SENT,
        USER_VALIDATION,
        CANCELLED,
        ABORT,
        NEW_GAME,
        ERROR,
        CANCEL_GAME,
        USER_DATA_REQUEST,
        ONLINE_USERS_DATA,
        USER_REGISTRATION_DATA,
        USER_ACTIVATION_SUCCESSFULL,
        USER_ACTIVATION_CODE_REQUEST,
        USER_REGISTRATION_FAILURE,
        USER_ACTIVATION_CODE,
        ONLINE_USERS_LIST,
        DISCONNECT,
        GAME_LIST_CHANGED(new CODE_PROPS[]{CODE_PROPS.INPUT_IRRELEVANT}),
        USER_LIST_CHANGED(new CODE_PROPS[]{CODE_PROPS.INPUT_IRRELEVANT}),
        PING(new CODE_PROPS[]{CODE_PROPS.INPUT_IRRELEVANT}),
        USER_REGISTRATION_SUCCESS,;

        public List<CODE_PROPS> props = new ArrayList<CODE_PROPS>();

        CODES() {

        }

        CODES(CODE_PROPS[] props) {
            this.props = Arrays.asList(props);
        }

        @Override
        public boolean isInputIrrelevant() {
            return (props.contains(CODE_PROPS.INPUT_IRRELEVANT));
        }

        @Override
        public String toString() {
            return this.name();
        }
    }

    public enum CODE_PROPS {
        INPUT_IRRELEVANT
    }

    public interface NetCode {

        String name();

        boolean isInputIrrelevant();

    }

}
