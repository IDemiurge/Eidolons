package main.swing.generic.services.listener;

import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.generic.components.list.G_List;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ObjListMouseListener<E> implements MouseListener { // ? extends
    // Entity

    private G_List<E> list;
    private Entity selected;
    private Entity lastSelected;

    public ObjListMouseListener(G_List<E> list) {
        this.list = list;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        E selectedValue = list.getSelectedValue();
        if (selectedValue == null) {
            selectedValue = new ArrayList<>(list.getData())
                    .get(list.locationToIndex(e.getPoint()));
        }
        if (!(selectedValue instanceof Entity)) {
            return;
        }
        lastSelected = selected;
        selected = (Entity) selectedValue;

        if (!selected.getGame().isStarted()) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            list.setSelectedIndex(list.locationToIndex(e.getPoint()));
            if (selectedValue instanceof Obj) {

                ((Obj) selectedValue).invokeRightClicked();
                return;
            }
        }
        try {
            selected.invokeClicked();
        } catch (Exception ex) {
            main.system.ExceptionMaster.printStackTrace(ex);
            return;
        }
        selected.getGame().getManager().refresh(false);
    }

    public G_List<E> getList() {
        return list;
    }

    public Entity getSelected() {
        return selected;
    }

    public Entity getLastSelected() {
        return lastSelected;
    }

    @Override
    public void mousePressed(MouseEvent e) {

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

}
