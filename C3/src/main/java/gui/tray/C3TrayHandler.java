package gui.tray;

import framework.C3Handler;
import framework.C3Manager;
import query.C3_Query;
import task.C3_Task;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class C3TrayHandler extends C3Handler implements MouseListener {

    private TrayIcon trayIcon;
    private PopupMenu popupMenu;

    public C3TrayHandler(C3Manager manager) {
        super(manager);
    }

    public void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("resources/c3.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        trayIcon = new TrayIcon(image, "Chaos Control Companion");
        //Let the system resize the image if needed
        // trayIcon.setImageAutoSize(true);

        // trayIcon.setImage(numberOfMinutesRemaining);
        //Set tooltip text for the tray icon
        tray.add(trayIcon);
        popupMenu = createPopup();
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(this);
        /*
        Useful functions?
        >> Reset/pause/cancel timer

         */
    }

    public void timeElapsed(C3_Task task) {
        trayIcon.displayMessage("Tadan!", "Time elapsed for " + task.getText(), TrayIcon.MessageType.INFO);

    }

    public void secondsRemain(int sec) {
        trayIcon.setToolTip("Query: "); //on timer?
    }

    public void notify(C3_Query query) {
        trayIcon.displayMessage(" minutes elapsed for " +
                query.getText(), "Query elapsed", TrayIcon.MessageType.INFO);
    }

    public void notify(String message, String title) {
        trayIcon.displayMessage(message,title, TrayIcon.MessageType.INFO);
    }
    private PopupMenu createPopup() {
        PopupMenu menu = new PopupMenu("C3");
        MenuItem newQueryItem = new MenuItem("New Query");
        menu.add(newQueryItem);
        return menu;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // manager.getTaskManager().upgradeStatusForCurrentTask();
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
}
