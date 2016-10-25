package main.swing.generic.components;

import javax.swing.*;
import java.awt.*;

public class G_ScrollablePanel extends G_Panel implements Scrollable {

    private Component comp;

    public G_ScrollablePanel(Component comp) {
        super();
        this.comp = comp;
        add(comp, "pos 0 0");
    }

    public Dimension getPreferredScrollableViewportSize() {
        return comp.getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ((orientation == SwingConstants.VERTICAL) ? visibleRect.height
                : visibleRect.width) - 10;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

}
