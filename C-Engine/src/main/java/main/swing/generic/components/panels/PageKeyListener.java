package main.swing.generic.components.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class PageKeyListener implements KeyListener {

    List<Component> comps = new ArrayList<>();

    public PageKeyListener(G_PagePanel<?> pagedListPanel) {
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
