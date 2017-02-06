package main.swing.generic.windows;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class G_Frame extends JFrame {

    public G_Frame(String string) {
        super(string);
        setLayout(new MigLayout());

    }

    public G_Frame(String mainTitle, boolean main) {
        this(mainTitle);
        if (main) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

    }

}
