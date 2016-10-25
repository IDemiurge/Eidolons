package main.swing.generic.components;

import java.awt.*;

public class G_CompHolder implements Refreshable, COMPONENT {

    protected G_Component comp;

    public G_CompHolder() {

    }

    public G_Component getComp() {
        return comp;
    }

    @Override
    public void add(Component c, String constraints) {
        comp.add(c, constraints);
    }

    @Override
    public void refresh() {
        comp.refresh();
    }

}
