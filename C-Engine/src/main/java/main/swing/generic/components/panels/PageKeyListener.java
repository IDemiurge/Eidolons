package main.swing.generic.components.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

public class PageKeyListener implements KeyListener {

    List<Component> comps = new LinkedList<>();
    private G_PagePanel<?> pagedListPanel;

    public PageKeyListener(G_PagePanel<?> pagedListPanel) {
        this.pagedListPanel = pagedListPanel;
    }

    public List<Component> getComponents() {
        return comps;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // pagedListPanel.flipPage(arg0.getWheelRotation() < 0);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
