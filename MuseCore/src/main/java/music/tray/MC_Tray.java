package music.tray;

import main.data.filesys.PathFinder;
import main.swing.generic.services.DialogMaster;
import music.PlaylistFinder;
import music.PlaylistHandler;
import music.funcs.MC_Funcs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MC_Tray implements MouseListener, ActionListener {

    private TrayIcon trayIcon;
    private PopupMenu popupMenu;

    public void displayTray() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage(PathFinder.absoluteResPath + "img/ui/active.png");
        trayIcon = new TrayIcon(image, "MuseCore");
        tray.add(trayIcon);
        popupMenu = createPopup();
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(this);

    }


    private PopupMenu createPopup() {
        PopupMenu menu = new PopupMenu("src.main.C3");
        for (MC_Funcs.MC_FUNCS value : MC_Funcs.MC_FUNCS.values()) {
            MenuItem item = new MenuItem(value.toString());
            item.setActionCommand(value.toString());
            item.addActionListener(this);
            menu.add(item);
        }
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "history":
                String toPlay = MC_Funcs.showHistory(10);
                PlaylistHandler.play("", toPlay);
                break;
            case "showAll":
                String path = MC_Funcs.showAll(false, false);
                PlaylistHandler.play(PlaylistHandler.ROOT_PATH_PICK, path);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
