package main.swing.listeners;

import main.entity.obj.Obj;
import main.game.Game;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ObjComponentMouseListener implements MouseListener {

    private Obj obj;

    public ObjComponentMouseListener(Obj obj) {
        this.obj = obj;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            if (SwingUtilities.isRightMouseButton(e)) {
                obj.invokeRightClicked();
                if (e.getClickCount() > 2) {

                }
            } else {
                obj.invokeClicked();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        Game.game.getManager().setHoverObj(obj);

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
