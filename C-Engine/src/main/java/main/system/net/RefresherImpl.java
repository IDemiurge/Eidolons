package main.system.net;

import main.swing.generic.components.Refreshable;
import main.system.net.socket.GenericConnection.CONNECTION_STATUS;
import main.system.net.socket.ServerConnector;

import java.util.HashMap;
import java.util.Map;

public class RefresherImpl implements Refresher {
    public static Boolean GAMELIST_SWITCHER = true;
    public static Boolean USERLIST_SWITCHER = true;
    public static Boolean GAMEUSERLIST_SWITCHER = true;
    private static Map<REFRESHER_TYPE, Boolean> SWITCHERS = new HashMap<>();

    static {
        SWITCHERS.put(REFRESHER_TYPE.GAMELIST, GAMELIST_SWITCHER);
        SWITCHERS.put(REFRESHER_TYPE.USERLIST, USERLIST_SWITCHER);
        SWITCHERS.put(REFRESHER_TYPE.USERLIST_GAME, GAMEUSERLIST_SWITCHER);
    }

    private Refreshable rc;
    private boolean enabled = true;
    private REFRESHER_TYPE switcher;

    public RefresherImpl(REFRESHER_TYPE switcher, Refreshable rc) {
        this.rc = rc;
        this.switcher = switcher;
        new Thread(this, "GameListRefresher").start();
    }

    public static void setGAMELIST_SWITCHER(boolean gAMELIST_SWITCHER) {
        GAMELIST_SWITCHER = gAMELIST_SWITCHER;
    }

    public static boolean isUSERLIST_SWITCHER() {
        return USERLIST_SWITCHER;
    }

    public static void setUSERLIST_SWITCHER(boolean uSERLIST_SWITCHER) {
        USERLIST_SWITCHER = uSERLIST_SWITCHER;
    }

    public static Map<REFRESHER_TYPE, Boolean> getSwitchers() {
        return SWITCHERS;
    }

    @Override
    public void run() {

        while (ServerConnector.getHandler().getStatus() == CONNECTION_STATUS.CONNECTED) {
            try {
                Thread.sleep(1250);
            } catch (InterruptedException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (enabled) {

                if (switcherCheck()) {
                    try {
                        rc.refresh();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                        return;
                    }
                }
            }
            // else
            // ServerConnector.getViewer().info("SWITCHER BLOCKED: "
            // + rc.getClass().getSimpleName());
        }
    }

    private boolean switcherCheck() {
        if (getSwitchers().get(switcher) == null) {
            return false;
        }
        if (getSwitchers().get(switcher)) {
            getSwitchers().put(switcher, false);
            return true;
        }
        return false;
    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;

    }

    public enum REFRESHER_TYPE {
        GAMELIST,
        USERLIST,
        USERLIST_GAME,
        GAME_OPTIONS,
    }
}
