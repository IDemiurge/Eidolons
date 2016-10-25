package main.swing;

import main.swing.components.TextComp;
import main.swing.generic.components.G_Panel;
import main.system.math.MathMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

public class SwingMaster {

    public static boolean DEBUG_ON = false;

    public static Component getParentOfClass(Component c, Class<?> CLASS) {
        while (true) {
            c = c.getParent();
            if (c == null)
                break;
            if (c.getClass().equals(CLASS))
                break;
        }
        return c;
    }

    public static int getComponentIndex(Container parent, Component source) {

        int i = 0;
        for (Component c : parent.getComponents()) {
            if (c == source)
                return i;
            i++;
        }
        return -1;
    }

    public static void autoResetZOrder(JComponent comp) {
        int i = comp.getComponents().length - 1;
        for (Component c : comp.getComponents()) {
            comp.setComponentZOrder(c, i);
            i--;
        }

    }

    public static void addMouseListener(Component component, MouseListener mouseListener) {
        component.addMouseListener(mouseListener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component c : container.getComponents())
                addMouseListener(c, mouseListener);
        }

    }

    public static void invokeAndWait(Runnable runnable) {
        if (EventQueue.isDispatchThread())
            runnable.run();
        else
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }

    public static Dimension getMinMaxSize(Dimension preferredSize, int minX, int minY, int maxX,
                                          int maxY) {
        Dimension dimension = new Dimension(MathMaster.getMinMax(preferredSize.width, minX, maxX),
                MathMaster.getMinMax(preferredSize.height, minY, maxY));
        return dimension;

    }

    public static G_Panel decorateWithText(String tooltip, Color c, Component box,
                                           String constraints) {
        G_Panel wrapper = new G_Panel("flowy");
        wrapper.add(new TextComp(tooltip, c));
        wrapper.add(box, constraints);
        return wrapper;
    }

    public static boolean isSizeGreaterThan(Dimension size, Dimension than) {
        if (size != null)
            if (size.width > than.width && size.height > than.height)
                return true;
        return false;
    }

    public static Dimension getModifiedSize(Dimension d, int widthMod, int heightMod) {
        return new Dimension(d.width + widthMod, d.height + heightMod);
    }

    public static void invokeLater(Runnable runnable) {
        if (EventQueue.isDispatchThread())
            runnable.run();
        else
            SwingUtilities.invokeLater(runnable);

    }

    public static Component findComponentUnderGlassPaneAt(Point p, Component top) {
        Component c = null;

        if (top.isShowing()) {
            if (top instanceof RootPaneContainer)
                c = ((RootPaneContainer) top).getLayeredPane().findComponentAt(
                        SwingUtilities.convertPoint(top, p, ((RootPaneContainer) top)
                                .getLayeredPane()));
            else
                c = ((Container) top).findComponentAt(p);
        }

        return c;
    }

}
