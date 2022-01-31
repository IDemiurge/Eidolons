package main.swing.generic.components.panels;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

public class PageWheelListener implements MouseWheelListener {

    List<Component> comps = new ArrayList<>();
    private G_PagePanel<?> pagedListPanel;

    public PageWheelListener(G_PagePanel<?> pagedListPanel) {
        this.pagedListPanel = pagedListPanel;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {
        for (int i = 0; i < Math.abs(arg0.getWheelRotation()); i++) {
            pagedListPanel.flipPage(arg0.getWheelRotation() < 0);
        }
        // main.system.auxiliary.LogMaster.system.log(1, "" + arg0.getWheelRotation());
    }

    public List<Component> getComponents() {
        return comps;
    }

}
