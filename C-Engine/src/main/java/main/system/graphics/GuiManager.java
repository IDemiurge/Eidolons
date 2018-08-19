package main.system.graphics;

import main.swing.generic.components.Builder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public class GuiManager {
    public static final int SCROLL_BAR_WIDTH = 12;
    public static final int HERO_X1 = 4;
    public static final int HERO_Y1 = 4;
    public static final int HERO_Y2 = 0;
    public static final int HERO_X2 = 4;
    public static final Dimension DEF_DIMENSION = new Dimension(1680, 1050);
    public static final int PANEL_FRAME_WIDTH = 22;
    public static final int PANEL_FRAME_HEIGHT = 12;
    private static final double DEFAULT_WIDTH = 1680;
    private static int cellHeight;
    private static int resolutionType;
    private static int objSize;
    private static Dimension size;
    private static int battleFieldWidth;
    private static int battleFieldHeight;
    private static int smallObjSize;
    // depends on font!
    private static int infoNamesColumnMinWidth = 100;
    private static int infoNamesColumnMaxWidth = 200;
    private static int battleFieldCellsX = 9;
    private static int battleFieldCellsY = 7;
    private static boolean fullscreen;
    private static KeyListener keyListener;
    private static DISPLAY_MODE displayMode;
    private static int bfObjSize;
    private static int tinyObjSize;
    private static Dimension portraitSize;
    private static int currentLevelCellsX;
    private static int currentLevelCellsY;
    private static int squareCellSize;
    private static int cellWidth;
    private static int bfCellsVersion = 1;
    private static Dimension overlayingHugeObjSize;
    private static Dimension overlayingObjSize;
    private static boolean guiDebug;

    public static void init() {

        UIManager.put("ScrollBar.width", SCROLL_BAR_WIDTH);

//        FontMaster.setUIFont();
        size = Toolkit.getDefaultToolkit().getScreenSize();
        Builder.setScreenSize(size);

        initDisplayMode();

        if (size.getWidth() > 1280) {
            squareCellSize = 132;
            setCellWidth(132);
            cellHeight = 113;
            objSize = 96;
            bfObjSize = 128;
        } else {
            // TODO update
            cellHeight = 99;
            objSize = 64;
            bfObjSize = 96;
            smallObjSize = 64;
        }
        smallObjSize = bfObjSize / 2;
        tinyObjSize = smallObjSize / 2;

        overlayingObjSize = new Dimension(getSmallObjSize() * 6 / 5, getSmallObjSize() * 6 / 5);
        overlayingHugeObjSize = new Dimension(getSmallObjSize() * 7 / 6, getSmallObjSize());

        setBattleFieldHeight(getBF_CompDisplayedCellsY() * cellHeight);
        setBattleFieldWidth(getBF_CompDisplayedCellsX() * cellHeight);
        // if (!isWide())
        try {
            UIManager.setLookAndFeel("" + "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"
             // "main.system.auxiliary.DarkNimbus"
             // "com.jtattoo.plaf.noire.NoireLookAndFeel"
            );

            FontMaster.setUIFont();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private static void initDisplayMode() {
        if (size.getWidth() >= 1920) {
            displayMode = (size.getHeight() > 1080) ? DISPLAY_MODE._1920x1200_
             : DISPLAY_MODE._1920x1080_;
        } else {
            displayMode = DISPLAY_MODE._1680x1050_;
        }

    }

    public static Dimension getPortraitSize() {
        if (portraitSize == null) {
            portraitSize = new Dimension(bfObjSize, bfObjSize);
        }
        return portraitSize;
    }

    public static int getFullObjSize() {
        return getSmallObjSize() * 2;
    }

    public static int getSquareCellSize() {
        return squareCellSize;
    }

    public static int getCellHeight() {
        return cellHeight;
    }

    public static void setCellSize(int cellSize) {
        GuiManager.cellHeight = cellSize;
    }

    public static int getResolutionType() {
        return resolutionType;
    }

    public static void setResolutionType(int resolutionType) {
        GuiManager.resolutionType = resolutionType;
    }

    public static int getObjSize() {
        return objSize;
    }

    public static void setObjSize(int objSize) {
        GuiManager.objSize = objSize;
    }

    public static Dimension getScreenSize() {
        return size;
    }

    public static void setSize(Dimension size) {
        GuiManager.size = size;
    }

    public static int getBattleFieldWidth() {
        return battleFieldWidth;
    }

    public static void setBattleFieldWidth(int battleFieldWidth) {
        GuiManager.battleFieldWidth = battleFieldWidth;
    }

    public static int getBattleFieldHeight() {
        return battleFieldHeight;
    }

    public static void setBattleFieldHeight(int battleFieldHeight) {
        GuiManager.battleFieldHeight = battleFieldHeight;
    }

    public static double getScreenWidth() {
        return size.getWidth();
    }

    public static double getScreenHeight() {
        return size.getHeight();
    }

    public static int getScreenWidthInt() {
        return (int) size.getWidth();
    }

    public static int getScreenHeightInt() {
        return (int) size.getHeight();
    }

    public static int getSmallObjSize() {
        return smallObjSize;
    }

    public static void setSmallObjSize(int smallObjSize) {
        GuiManager.smallObjSize = smallObjSize;
    }

    public static Dimension getOverlayingHugeObjSize() {
        return overlayingHugeObjSize;
    }

    public static Dimension getOverlayingObjSize() {
        return overlayingObjSize;
    }

    public static int getInfoNamesColumnMinWidth() {
        return infoNamesColumnMinWidth;
    }

    public static int getInfoNamesColumnMaxWidth() {
        return infoNamesColumnMaxWidth;
    }

    public static JFrame inNewWindow(JComponent comp, Dimension SIZE) {
        JFrame window = inNewWindow(false, comp, "nnm", SIZE);
        if (!window.isDisplayable()) {
            window.setUndecorated(true);
        }
        return window;
    }

    public static JFrame inNewWindow(JComponent comp, String title, Dimension SIZE) {
        return inNewWindow(false, comp, title, SIZE);
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

    public static JFrame inNewWindow(boolean undecorated, JComponent comp, String title,
                                     Dimension SIZE) {
        JFrame window = new JFrame(title);

        window.setSize(SIZE);
        // window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        window.add(comp);
        if (undecorated) {
            window.setUndecorated(true);
        }
        window.setVisible(true);
        return window;
    }

    public static void toggleFocus(JFrame window) {
        window.setAlwaysOnTop(!window.isAlwaysOnTop());
        if (window.isAlwaysOnTop()) {
            window.requestFocus();
        }

    }

    public static void toggleVisible(JFrame window) {
        window.setVisible(window.isVisible());
    }

    public static int getBF_CompDisplayedCellsX() {
        return battleFieldCellsX;
    }

    public static void setBF_CompDisplayedCellsX(int battleFieldCellsX) {
        GuiManager.battleFieldCellsX = battleFieldCellsX;
    }

    public static int getBF_CompDisplayedCellsY() {
        return battleFieldCellsY;
    }

    public static int getBfGridHeight() {
        return getCellHeight() * getBF_CompDisplayedCellsY();
    }

    public static int getBfGridWidth() {
        return getCellWidth() * getBF_CompDisplayedCellsX();
    }

    public static void setBattleFieldCellsY(int battleFieldCellsY) {
        GuiManager.battleFieldCellsY = battleFieldCellsY;
    }

    public static void setBattleFieldCellsX(int battleFieldCellsX) {
        GuiManager.battleFieldCellsX = battleFieldCellsX;
    }

    public static boolean isFullScreen() {
        return isFullscreen();
    }

    public static boolean isFullscreen() {
        return fullscreen;
        // return true;
    }

    public static void setFullscreen(boolean fullscreen) {
        GuiManager.fullscreen = fullscreen;
    }

    public static void setWindowToFullscreen(JFrame frame) {
        GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment()
         .getDefaultScreenDevice();
        try {
            myDevice.setFullScreenWindow(frame);
            setFullscreen(true);
        } finally {
            setFullscreen(false);
            myDevice.setFullScreenWindow(null);
        }
    }

    public static int getCurrentLevelCellsX() {
        return currentLevelCellsX;
    }

    public static void setCurrentLevelCellsX(int currentLevelCellsX) {
        GuiManager.currentLevelCellsX = currentLevelCellsX;
    }

    public static int getCurrentLevelCellsY() {
        return currentLevelCellsY;
    }

    public static void setCurrentLevelCellsY(int currentLevelCellsY) {
        GuiManager.currentLevelCellsY = currentLevelCellsY;
    }

    public static boolean isWide() {
        return getScreenWidth() > DEFAULT_WIDTH;
    }

    public static KeyListener getKeyManager() {
        return getKeyListener();
    }

    public static KeyListener getKeyListener() {
        return keyListener;
    }

    public static void setKeyListener(KeyListener keyListener) {
        GuiManager.keyListener = keyListener;
    }

    public static Point getCenterPoint(Dimension size) {
        return new Point(MigMaster.getCenteredPosition(getScreenWidthInt(), size.width), MigMaster
         .getCenteredPosition(getScreenHeightInt(), size.height));
    }

    public static DISPLAY_MODE getDisplayMode() {
        return displayMode;
    }

    public static int getCellNumber() {
        return battleFieldCellsX * battleFieldCellsY;
    }

    public static int getBfObjSize() {
        return bfObjSize;
    }

    public static int getTinyObjSize() {
        return tinyObjSize;
    }

    public static int getCellWidth() {
        return cellWidth;
    }

    public static void setCellWidth(int cellWidth) {
        GuiManager.cellWidth = cellWidth;
    }

    public static boolean isTall() {
        return getBF_CompDisplayedCellsY() > 6;
    }

    public static int getBfCellsVersion() {
        return bfCellsVersion;
    }

    public static boolean isGuiDebug() {
//        return true;
        return guiDebug;
    }

    public static void setGuiDebug(boolean guiDebug) {
        GuiManager.guiDebug = guiDebug;
    }

    public enum DISPLAY_MODE {
        _1920x1200_, _1920x1080_, _1680x1050_,;
    }

}
