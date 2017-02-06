package main.swing.components.buttons;

import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.ComponentVisuals;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public abstract class CustomButton extends TextCompDC implements MouseListener {
    public static final VISUALS BUTTON_VISUALS = VISUALS.BUTTON;
    public static final long serialVersionUID = 1L;
    public static final int DEFAULT_BUTTON_FONT_SIZE = 16;
    /**
     * mouse listening: hover highlight; mouse pressed -> new img ;click sound
     */

    protected boolean pressed;
    protected boolean highlighted;

    // TODO WHY DOESN'T MOUSE LISTENER WORK WITH STRING-BASED COMPVISUALS??
    public CustomButton(ComponentVisuals v) {
        this(v, null);
    }

    public CustomButton(ComponentVisuals v, String text) {
        super(v, text);
        addMouseListener(this);

    }

    public CustomButton(String text, Image image) {
        this(new CompVisuals(image), text);
        // background = new JLabel(new ImageIcon(image));
    }

    public CustomButton(String text) {
        this(BUTTON_VISUALS, text);
    }

    @Override
    protected int getDefaultFontSize() {
        return DEFAULT_BUTTON_FONT_SIZE;
    }

    public void clicked() {
        if (!isEnabled()) {
            playDisabledSound();
            return;
        }
        playClickSound();
        try {
            handleClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void handleClick();

    public void handleAltClick() {
        handleClick();
    }

    protected void playDisabledSound() {
        SoundMaster.playStandardSound(getDisabledClickSound());
    }

    protected STD_SOUNDS getDisabledClickSound() {
        return (STD_SOUNDS.CLICK_BLOCKED);
    }

    protected void playClickSound() {
        SoundMaster.playStandardSound(getClickSound());

    }

    protected STD_SOUNDS getClickSound() {
        return (STD_SOUNDS.MOVE);
    }

    @Override
    protected int getDefaultY() {
        if (isMoreY()) {
            return 3 * super.getDefaultY() / 2;
        }
        return super.getDefaultY();
    }

    protected boolean isMoreY() {
        return true;
    }

    @Override
    protected int getDefaultX() {
        if (!StringMaster.isEmpty(text)) {
            return getCenteredX(text);
        }
        return super.getDefaultY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            handleRightClick();
        } else if (e.isAltDown()) {
            altClicked();
        } else {
            clicked();
        }

    }

    protected void handleRightClick() {
        // TODO Auto-generated method stub

    }

    private void altClicked() {
        handleAltClick();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    public void activateMouseListener() {
        if (Arrays.asList(getMouseListeners()).isEmpty()) {
            addMouseListener(this);
        }

    }
}
