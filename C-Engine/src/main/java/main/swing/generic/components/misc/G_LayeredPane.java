package main.swing.generic.components.misc;

import main.swing.generic.components.G_Panel;

import javax.swing.*;

public class G_LayeredPane extends JLayeredPane {

    private JLabel lbl;

    public G_LayeredPane(ImageIcon pic, JComponent c) {
        super();

        lbl = new JLabel(pic);
        setLayer(lbl, Integer.MIN_VALUE);

        setLayer(c, Integer.MAX_VALUE);

    }

    public G_LayeredPane(G_Panel panel, JLabel lbl2) {
        lbl = lbl2;
        setLayer(lbl, Integer.MIN_VALUE);

        setLayer(panel, Integer.MAX_VALUE);
    }

}
