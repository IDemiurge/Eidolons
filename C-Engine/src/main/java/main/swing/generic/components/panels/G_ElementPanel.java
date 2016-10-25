package main.swing.generic.components.panels;

import main.swing.generic.components.G_Panel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public abstract class G_ElementPanel<T extends Enum<T>> extends G_Panel
        implements ActionListener, ItemListener {

    private static final int HEIGHT = 64;
    private int y = 0;

    public G_ElementPanel() {

    }

    public void addCheckBoxElement() {
        // add(booleanElement);
    }

    public JComboBox<?> addDropBoxPanel(String name, Object[] options) {

        JComboBox<?> box = new JComboBox<>(options);
        box.addActionListener(this);
        box.setActionCommand(name);
        // box.setSelectedIndex(db_index);
        add(box, "pos " + "0 " +
                // "visual.x2/4*3 " +
                y + ", h " + HEIGHT);
        y += HEIGHT;
        return box;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub

    }

    public abstract void addElements();

    public void resetElements() {
        removeAll();
        addElements();
        revalidate();
        repaint();

    }

}
