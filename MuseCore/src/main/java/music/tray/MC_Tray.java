package music.tray;

import main.data.filesys.PathFinder;
import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.EnumMaster;
import music.PlaylistFinder;
import music.PlaylistHandler;
import music.funcs.MC_Funcs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static music.PlaylistHandler.*;

public class MC_Tray implements MouseListener, ActionListener {

    private TrayIcon trayIcon;
    private PopupMenu popupMenu;
    private String[] McFuncs={
            "History",
            "Show All",
            "Random Mode",
            "Newest First",
            "Set Mode",
            "Reset List",
            "Exit"
    };

    public void displayTray() throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(PathFinder.absoluteResPath + "img/ui/active.png");
        trayIcon = new TrayIcon(image, "MuseCore");
        tray.add(trayIcon);
        popupMenu = createPopup();
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.addMouseListener(this);
    }

    private void resetTooltip() {
        trayIcon.setToolTip("MuseCore, Mode: "+ PlaylistHandler.getPlayMode());
    }


    private PopupMenu createPopup() {
        PopupMenu menu = new PopupMenu("src.main.C3");
        for (String value : McFuncs) {
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
            case "History":
                String toPlay = MC_Funcs.showHistory(10);
                play("", toPlay);
                break;
            case "Show All":
                String path = MC_Funcs.showAll(false, false);
                play(ROOT_PATH_PICK, path);
                break;
            case "Random Mode":
                PlaylistHandler.setPlayMode(PlayMode.Random);
                break;
            case "Newest First":
                PlaylistHandler.setPlayMode(PlayMode.Newest);
                break;
            case "Set Mode":
                PlayMode mode = new EnumMaster<PlayMode>().selectEnum(PlayMode.class);
                PlaylistHandler.setPlayMode(mode );
                break;
            case "Reset List":
                PlaylistHandler.setResetList(true);
                break;
            case "Exit":
                System.exit(0);
                break;
            case "Menu":
                //TODO
                break;
        }
        resetTooltip();
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
