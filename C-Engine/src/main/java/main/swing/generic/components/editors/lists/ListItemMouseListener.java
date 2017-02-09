package main.swing.generic.components.editors.lists;

import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.list.G_List;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ListItemMouseListener<E> implements MouseListener {

    private ListControlPanel<E> buttonPanel;
    private SELECTION_MODE mode;
    private G_List<E> list;
    private G_List<E> secondList;
    private ListInfoPanel ip;
    private OBJ_TYPE TYPE;

    public ListItemMouseListener(OBJ_TYPE TYPE, ListControlPanel<E> buttonPanel,
                                 SELECTION_MODE mode, G_List<E> list) {
        this.list = list;
        this.buttonPanel = buttonPanel;
        this.mode = mode;
        this.TYPE = TYPE;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        JList source = (JList) arg0.getSource();

        if (ip != null) {
            Entity obj = DataManager.getType(source.getSelectedValue().toString(), TYPE);
            ip.setInfoObj(obj);
            ip.refresh();
        }

        if (arg0.isAltDown()) {
            buttonPanel.moveOne(SwingUtilities.isRightMouseButton(arg0));
            return;
        }
        if (SwingUtilities.isRightMouseButton(arg0)) {
            if (arg0.isAltDown()) {
                buttonPanel.edit();
            } else {
                buttonPanel.moveOne(arg0.getClickCount() > 1);
            }
            return;
        }

        if (arg0.getClickCount() > 1) {
            if (mode == SELECTION_MODE.SINGLE) {

            } else {
                if (source == getList()) {
                    buttonPanel.add();
                } else {
                    buttonPanel.removeOrAdd(mode);
                }
            }
        }

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public G_List<E> getList() {
        return list;
    }

    public void setList(G_List<E> list) {
        this.list = list;
    }

    public G_List<E> getSecondList() {
        return secondList;
    }

    public void setSecondList(G_List<E> secondList) {
        this.secondList = secondList;
    }

    public void setInfoPanel(ListInfoPanel ip) {
        this.ip = ip;

    }

}
