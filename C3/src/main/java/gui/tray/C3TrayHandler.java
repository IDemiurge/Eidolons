package gui.tray;

import framework.C3Handler;
import framework.C3Manager;
import main.data.filesys.PathFinder;
import main.swing.generic.services.DialogMaster;
import query.C3_Query;
import task.C3_Task;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class C3TrayHandler extends C3Handler implements MouseListener, ActionListener {

    private TrayIcon trayIcon;
    private PopupMenu popupMenu;

    public C3TrayHandler(C3Manager manager) {
        super(manager);
    }

    public void displayTray() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage(getImage(TrayIconVariant.normal));
        trayIcon = new TrayIcon(image, "Chaos Control Companion");
        tray.add(trayIcon);

        popupMenu = createPopup();
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(this);
    }


    public void notify(String message, String title) {
        trayIcon.displayMessage(message, title, TrayIcon.MessageType.INFO);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Break":
                manager.getSessionHandler().shiftBreak();
                break;
            case "New_Query":
                manager.getQueryManager().createRandomQuery();
                break;
            case "Reset_Music":
                manager.getSessionHandler().resetMusic( );
                break;
            case "New_Task":
                manager.getSessionHandler().addTask(DialogMaster.confirm("Custom task?"));
                break;
        }
    }

    public enum TrayMenuItem {
        New_Task,
        New_Query,
        Break,
        Reset_Music
        ;
    }

    private PopupMenu createPopup() {
        PopupMenu menu = new PopupMenu("C3");
        for (TrayMenuItem value : TrayMenuItem.values()) {
            MenuItem item = new MenuItem(value.toString());
            item.setActionCommand(value.toString());
            item.addActionListener(this);
            menu.add(item);
        }
        return menu;
    }

    public void setImage(TrayIconVariant variant) {
        Image image = Toolkit.getDefaultToolkit().createImage(getImage(variant));
        trayIcon.setImage(image);
    }

    public void setTooltip(String tooltip) {
        trayIcon.setToolTip(tooltip);
    }

    public void notify(C3_Query query) {
        trayIcon.displayMessage(" minutes elapsed for " +
                query.getText(), "Query elapsed", TrayIcon.MessageType.INFO);
    }

    public void timeElapsed(C3_Task task) {
        trayIcon.displayMessage("Tadan!", "Time elapsed for " + task.getText(), TrayIcon.MessageType.INFO);
        // trayIcon.setToolTip(text);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        manager.getDialogHandler().showOptionsMenu();

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public enum TrayIconVariant {
        normal, active, paused, finished
        //IDEA - support auto-session on TIME - just launch / click + confirm and start!
    }

    private String getImageDefault() {
        return PathFinder.getRootPath() + "resources/chest16.png";
    }

    private String getImageActive() {
        return PathFinder.getRootPath() + "resources/active2.png";
    }

    private String getImagePaused() {
        return PathFinder.getRootPath() + "resources/paused.png";
    }

    private String getImageFinished() {
        return PathFinder.getRootPath() + "resources/success.png";
    }

    private String getImage(TrayIconVariant variant) {
        switch (variant) {
            case normal -> {
                return getImageDefault();
            }
            case active -> {
                return getImageActive();
            }
            case paused -> {
                return getImagePaused();
            }
            case finished -> {
                return getImageFinished();
            }
        }
        return null;
    }
}
