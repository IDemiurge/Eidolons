package main.swing.components.panels.secondary;

import main.swing.components.panels.DC_UnitActionPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ActionModeMouseListener implements MouseListener {

    private DC_UnitActionPanel actionPanel;

    public ActionModeMouseListener(DC_UnitActionPanel actionPanel) {
        this.actionPanel = actionPanel;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        actionPanel.getData().getOrCreate(i);
//        toggleDisplayActionModePanel(e.getPoint(), action);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private boolean checkToggle() {
        return MouseInfo.getPointerInfo().getLocation().y - getLastY() > ActionModePanel.HEIGHT;
    }

    private int getLastY() {
        // TODO Auto-generated method stub
        return 0;
    }

    private void startToggleCheckThread() {
        new Thread(new Runnable() {
            public void run() {
            }
        }, " thread").start();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        startToggleCheckThread();

    }

}
