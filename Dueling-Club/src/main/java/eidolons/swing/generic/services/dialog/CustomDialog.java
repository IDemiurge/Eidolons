package eidolons.swing.generic.services.dialog;

import eidolons.entity.obj.DC_Obj;
import main.swing.generic.components.G_Component;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class CustomDialog<T> {
    G_Component panel;
    DC_Obj target;
    List<T> data;
    private boolean visible;

    public CustomDialog() {
        panel = createPanel();
        // TODO scroll visuals?
    }

    protected abstract G_Component createPanel();

    public void refresh() {
        panel.refresh();
    }

    public void draw(Graphics g) {
        // instead of adding?..
//        panel.setLocation(portrait);
        // buffer image?
        panel.paint(g);

    }

    public boolean checkClick(MouseEvent e) {
        Point point = e.getLocationOnScreen();
        if (contains(point)) {
            click(e);
            return true;
        }
        return false;
    }

    public boolean isInfoClickAllowed() {
        return true;

    }

    public boolean isActionClickAllowed() {
        return false;

    }

    public void click(MouseEvent e) {

    }

    public boolean contains(Point point) {
        return panel.contains(point);
    }

    public void close() {
        visible = false;
    }

    public void show() {
        visible = true;
        refresh();
    }

    public boolean isVisible() {
        return visible;
    }

    public G_Component getPanel() {
        return panel;
    }

    public List<T> getData() {
        return data;
    }

    public boolean isInfoSelectionOn() {
        return true;
    }

    public DC_Obj getTarget() {
        return target;
    }
}
