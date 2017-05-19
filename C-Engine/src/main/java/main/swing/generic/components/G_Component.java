package main.swing.generic.components;

import main.swing.Painted;
import main.swing.PointX;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * superclass for other Gs? extends or aggregates Component? Yeah, that could be
 * it - let's aggregate and delegate! Problem: how to delegate specific methods
 * if $component is not specific? Casting?
 *
 * @author JustMe
 */

public abstract class G_Component extends JPanel implements Refreshable

{

    // public G_Component(JComponent component){
    // this.component=component;
    // }

    protected boolean info_selected;
    protected boolean active_selected;
    private boolean validated = false;
    private Painted paintManager;
    private KeyListener keyManager;
    private boolean autoZOrder;

    public void refresh() {

    }

    protected boolean isAutoZOrder() {
        return autoZOrder;
    }

    public void setAutoZOrder(boolean b) {
        autoZOrder = b;

    }

    @Override
    public void add(Component comp, Object constraints) {

        super.add(comp, constraints);
        // if (getKeyManager() != null) {
        // if (comp instanceof G_Component) {
        // // ((G_Component) comp).setKeyManager(getKeyManager());
        // } else
        // comp.addKeyListener(getKeyManager());
        // }
    }

    public boolean isInitialized() {
        return true;
    }

    public boolean isInfo_selected() {
        return info_selected;
    }

    public void setInfoSelected(boolean info_selected) {
        this.info_selected = info_selected;
        refresh();
    }

    public boolean isActive_selected() {
        return active_selected;
    }

    public void setActive_selected(boolean active_selected) {
        this.active_selected = active_selected;
        refresh();
    }

    public void dataChanged() {
        refresh();
    }

    public Painted getPaintManager() {
        return paintManager;
    }

    public void setPaintManager(Painted paintManager) {
        this.paintManager = paintManager;
    }

    @Override
    public void paint(Graphics g) {
        if (paintManager != null) {
            paintManager.paint(g);
        } else {
            super.paint(g);
        }
    }

    // DELEGATES
    public KeyListener getKeyManager() {
        return keyManager;
    }

    public void setKeyManager(KeyListener keyManager) {
        removeKeyListener(this.keyManager);
        this.keyManager = keyManager;
        addKeyListener(keyManager);
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getComponentString() {
        String string = "";
        for (Component c : getComponents()) {
            string += c.getName() + " at " + new PointX(c.getX(), c.getY());

        }

        return "comps: " + string;
    }

    public void refreshComponents() {
        for (Component c : getComponents()) {
            if (c instanceof G_Component) {
                G_Component g_Component = (G_Component) c;
                g_Component.refresh();
            }
        }
    }
}
