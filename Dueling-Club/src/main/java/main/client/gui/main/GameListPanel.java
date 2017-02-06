package main.client.gui.main;

import main.client.net.GameList;
import main.swing.generic.components.G_Panel;
import main.system.net.RefresherImpl;
import main.system.net.RefresherImpl.REFRESHER_TYPE;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameListPanel extends G_Panel implements MouseListener {
    private GameList list;
    private RefresherImpl refresher;

    public GameListPanel() {
        list = new GameList(this);
        this.refresher = new RefresherImpl(REFRESHER_TYPE.GAMELIST, list);

        refresh();
    }

    public void refresh() {
        removeAll();
        // add(new JLabel("Available games: "), " pos 0 0 container.x2 50");
        // JScrollPane scr = new JScrollPane(list);
        add(list, " pos 0 50 container.x2 container.y2");
        this.repaint();
        this.revalidate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 || e.isAltDown()) {
            doubleClick(e);
        } else {
            return;
        }

    }

    public void doubleClick(MouseEvent e) {
        list.getList().get(list.getSelectedIndex()).join();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    ;
}
