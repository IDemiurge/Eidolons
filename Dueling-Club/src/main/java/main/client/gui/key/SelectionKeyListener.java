package main.client.gui.key;

import main.client.dc.MainManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SelectionKeyListener implements KeyListener {

    private MainManager manager;

    public SelectionKeyListener(MainManager manager) {
        this.manager = manager;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: {
                ok();
                break;
            }
            case KeyEvent.VK_ESCAPE: {
                cancel();
                break;
            }

        }

    }

    private void cancel() {
        manager.cancelSelection();
    }

    private void ok() {
        // check
        if (!manager.getSequence().getView().isOkBlocked()) {
            manager.doneSelection();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
