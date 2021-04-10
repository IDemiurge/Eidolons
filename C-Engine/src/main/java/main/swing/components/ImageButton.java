package main.swing.components;

import main.swing.generic.components.misc.GraphicComponent;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.STD_SOUNDS;
import main.system.sound.SoundMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ImageButton extends GraphicComponent implements MouseListener {

    public ImageButton(STD_COMP_IMAGES template) {
        this(template.getImg());
    }

    public ImageButton(STD_IMAGES template) {
        this(template.getImage());
    }

    public ImageButton(STD_IMAGES template, String tooltip) {
        this(template);
        setToolTipText(tooltip);
    }

    public ImageButton(Image img, String tooltip) {
        this(img);
        setToolTipText(tooltip);
    }

    public ImageButton(Image img) {
        super(img);
        activateMouseListener();
    }

    public ImageButton(String path) {
        this(ImageManager.getImage(path));
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
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void handleClick() {
    }

    public void handleAltClick() {
        handleClick();
    }

    protected void playDisabledSound() {
        SoundMaster.playStandardSound(getDisabledClickSound());
    }

    protected STD_SOUNDS getDisabledClickSound() {
        return (AudioEnums.STD_SOUNDS.CLICK_BLOCKED);
    }

    protected void playClickSound() {
        SoundMaster.playStandardSound(getClickSound());

    }

    protected STD_SOUNDS getClickSound() {
        return (AudioEnums.STD_SOUNDS.MOVE);
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
        addMouseListener(this);

    }

}
