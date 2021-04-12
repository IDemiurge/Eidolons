package session;

import framework.C3Item;
import framework.C3Manager;
import gui.tray.C3TrayHandler;
import log.C3Logger;

import javax.swing.*;

public class SessionLogger extends C3Logger {

    private static final String TLOG_FILE_PATH = "resources/c3_session_log.txt";

    public SessionLogger(C3Manager manager) {
        super(manager, TLOG_FILE_PATH);
    }

    public void sessionStarted(C3Session session) {
        started(session);
    }

    public void finished(C3Session session) {
        manager.getTrayHandler().setImage(C3TrayHandler.TrayIconVariant.finished);
        String input = JOptionPane.showInputDialog(session + " is done!\n Any comments?");
        done(session, input);
    }

    public void started(C3Item session) {
        manager.getTrayHandler().setImage(C3TrayHandler.TrayIconVariant.active);
        super.started(session);
    }

    public void resumed(C3Session session) {
        manager.getTrayHandler().setImage(C3TrayHandler.TrayIconVariant.active);
    }

    public void paused(C3Session session) {
        manager.getTrayHandler().setImage(C3TrayHandler.TrayIconVariant.paused);
    }
}
