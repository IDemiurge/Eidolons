package main.swing.generic.components.misc;

import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;

public abstract class G_ListMenu extends JList<String> implements
        ListCellRenderer<String>, MouseListener {

    protected DefaultListModel<String> model;

    public G_ListMenu(List<String> items) {
        model = new DefaultListModel<>();
        setModel(model);
        setData(items);
        setCellRenderer(this);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 || e.isAltDown()) {
            handleClick(getSelectedValue());
        } else {
            return;
        }

    }

    public abstract void handleClick(String text);

    public void setData(Collection<String> data) {
        model.removeAllElements();
        for (String element : data) {
            model.addElement(element);
        }
    }

    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

        JButton button = new JButton();
        button.setFont(FontMaster.getDefaultFont(16));
        button.setText(value);
        button.setBackground(ColorManager.getTranslucent(Color.black, 15));
        button.setForeground(Color.white);

        return button;
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

}
