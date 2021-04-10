package main.system.graphics;

import main.swing.generic.components.Builder;
import main.system.launch.CoreEngine;

import javax.swing.*;
import java.awt.*;

public class GuiManager {
    public static final int SCROLL_BAR_WIDTH = 12;
    private static int cellHeight;
    private static int objSize;
    private static Dimension size;
    private static int smallObjSize;
    private static boolean fullscreen;
    private static int bfObjSize;
    private static int tinyObjSize;

    public static void init() {
        // DO NOT REMOVE THIS - OPEN GL RESOLUTION WILL BREAK!!!
        UIManager.put("ScrollBar.width", SCROLL_BAR_WIDTH);

        cellHeight = 99;
        objSize = 64;
        bfObjSize = 96;
        smallObjSize = 64;
        smallObjSize = bfObjSize / 2;
        tinyObjSize = smallObjSize / 2;

        if (CoreEngine.isSwingOn()) {
            size = Toolkit.getDefaultToolkit().getScreenSize();
            Builder.setScreenSize(size);
            try {
                UIManager.setLookAndFeel(
                        "javax.swing.plaf.nimbus.NimbusLookAndFeel"
                );
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(
                            "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                main.system.ExceptionMaster.printStackTrace(e);
            }
            FontMaster.setUIFont();
        }
    }

    public static int getScreenWidthInt() {
        return size.width;
    }

    public static int getScreenHeightInt() {
        return size.height;
    }

    public static int getFullObjSize() {
        return getSmallObjSize() * 2;
    }

    public static int getCellHeight() {
        return cellHeight;
    }

    public static int getObjSize() {
        return objSize;
    }

    public static Dimension getScreenSize() {
        return size;
    }

    public static void setSize(Dimension size) {
        GuiManager.size = size;
    }

    public static int getSmallObjSize() {
        return smallObjSize;
    }

    public static int getInfoNamesColumnMaxWidth() {
        int infoNamesColumnMaxWidth = 200;
        return infoNamesColumnMaxWidth;
    }

    public static JDialog inModalWindow(JComponent comp, String title,
                                        Dimension SIZE) {
        final JDialog frame = new JDialog();
        frame.setTitle(title);
        frame.setSize(SIZE);
        frame.getContentPane().add(comp);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public static boolean isFullScreen() {
        return isFullscreen();
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void setFullscreen(boolean fullscreen) {
        GuiManager.fullscreen = fullscreen;
    }

    public static int getBfObjSize() {
        return bfObjSize;
    }

    public static int getTinyObjSize() {
        return tinyObjSize;
    }

}
