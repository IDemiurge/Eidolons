package main.swing.generic;

import main.swing.generic.components.G_Panel;

import java.awt.*;

public class G_View extends G_Panel {

    public void setView(Component comp, String restraints) {
        removeAll();
        add(comp, restraints);
        setComponentZOrder(comp, 0);
        addBackground(1);
        revalidate();
        repaint();
    }

    protected void addBackground(int i) {

    }
}
