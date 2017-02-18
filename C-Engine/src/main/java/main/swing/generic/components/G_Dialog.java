package main.swing.generic.components;

import main.game.core.game.Game;
import main.swing.generic.services.ComponentResizer;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public abstract class G_Dialog {
    protected JInternalFrame internalFrame;
    protected JDialog dialog;

    public G_Dialog() {
        if (isReady()) {
            init();
        }
    }

    protected abstract boolean isReady();

    public void init() {
        Component createComponent = createComponent();
        if (getKeyListener() != null) {
            createComponent.addKeyListener(getKeyListener());
        }

        JPanel p = new G_Panel();
        p.setOpaque(false);
        // p.setLayout(new FlowLayout());
        p.add(createComponent);
        p.setBackground(ColorManager.BACKGROUND);
        createComponent.setBackground(ColorManager.BACKGROUND);

        dialog = new JDialog(Game.game.getWindow(), getTitle(), true);
        dialog.setBackground(ColorManager.BACKGROUND);
        getFrame().setBackground(ColorManager.BACKGROUND);

        dialog.setUndecorated(true);
        dialog.add(p);
        dialog.setSize(getSize());
        dialog.setLocation(getLocation());
        new ComponentResizer(dialog).registerComponent(dialog);
        // if (isBlackBackground())
        // dialog.setOpacity(opacity);
        // dialog.setShape(opacity);
    }

    protected KeyListener getKeyListener() {
        return null;
    }

    protected void ok() {

    }

    protected boolean isAlwaysOnTop() {
        return !Game.game.isDebugMode();
    }

    private boolean isBlackBackground() {
        return true;
    }

    public Point getLocation() {
        if (isOnMousePoint()) {
            int x = (int) (MouseInfo.getPointerInfo().getLocation().x - getSize().getWidth() / 2);
            x = (int) Math.min(GuiManager.getScreenWidthInt() - getSize().getWidth(), x);
            int y = (int) (MouseInfo.getPointerInfo().getLocation().y - getSize().getHeight() / 2);
            y = (int) Math.min(GuiManager.getScreenHeight() - getSize().getHeight(), y);
            return new Point(x, y);
        }

        if (!isCentered()) {
            return new Point(0, 0);
        }

        return new Point((int) (GuiManager.getScreenWidth() - getSize().getWidth()) / 2,
                (int) (GuiManager.getScreenHeight() - getSize().getHeight()) / 2);
    }

    protected boolean isOnMousePoint() {
        return false;
    }

    public abstract boolean isCentered();

    public abstract Dimension getSize();

    public abstract String getTitle();

    protected boolean isUndecorated() {
        return true;
    }

    public abstract Component createComponent();

    public void close() {
        if (getFrame() != null) {
            getFrame().setVisible(false);
        }
    }

    public void show() {
        if (getFrame() != null) {
            getFrame().setVisible(true);
        }

        if (isOnMousePoint()) {
            // new Robot().mouseMove(0, 100);
        }
        // JOptionPane.showMessageDialog(null, frame.getContentPane()); a good
        // joke

    }

    public Window getFrame() {
        return dialog;
    }

}
