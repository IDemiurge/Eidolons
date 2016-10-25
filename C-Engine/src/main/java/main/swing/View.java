package main.swing;

import main.entity.obj.Obj;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.Refreshable;

import java.awt.*;
import java.util.Set;

public abstract class View implements Refreshable, Painted {
    protected G_Component comp = new G_Panel();
    protected boolean isActive = false;

    public abstract void initView();

    public abstract void activateView();

    @Override
    public void paint(Graphics g) {
        comp.paint(g);
    }

    public G_Component getComp() {
        return comp;
    }

    public void setComp(G_Component comp) {
        this.comp = comp;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void highlight(Set<Obj> set) {

    }

    public void add(Component comp, Object constraints) {
        this.comp.add(comp, constraints);
    }

    public void deactivateView() {

    }

}
