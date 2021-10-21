package main.gui.components.controls;

import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SmartButton;
import main.swing.generic.components.G_Panel;
import main.system.images.ImageManager;
import main.system.sound.AudioEnums;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AvIconBtn extends G_Panel implements SmartButton, MouseListener {

    Runnable runnable;
    ButtonStyled.STD_BUTTON style;

    JLabel iconLabel;
    JButton button;

    public AvIconBtn(Runnable runnable, ButtonStyled.STD_BUTTON style, String text) throws HeadlessException {
        this.runnable = runnable;
        this.style = style;
        setLayout(new MigLayout("fill"));
        if (isIcon()){
            iconLabel = new JLabel(ImageManager.getIcon(style.getPath()));
            addMouseListener(this);
            add(iconLabel);
        } else {
            button= new JButton(text);
            add(button, "growx, pad -10 5 0 0");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    runnable.run();
                }
            });
        }

        setOpaque(false);
        // addActionListener(e -> runnable.run());
    }

    private boolean isIcon() {
        return false;
    }

    @Override
    public boolean isNoClickCheck() {
        return false;
    }

    @Override
    public Runnable getRunnable() {
        return null;
    }

    @Override
    public AudioEnums.BUTTON_SOUND_MAP getSoundMap() {
        return null;
    }

    @Override
    public void setRunnable(Runnable runnable) {

    }

    @Override
    public boolean isIgnoreConfirmBlock() {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public Runnable getDisabledRunnable() {
        return null;
    }

    @Override
    public void setIgnoreConfirmBlock(boolean ignoreConfirmBlock) {

    }

    @Override
    public void setDisabledRunnable(Runnable disabledRunnable) {

    }

    @Override
    public void setChecked(boolean b) {

    }

    @Override
    public boolean isFlipX() {
        return false;
    }

    @Override
    public boolean isFlipY() {
        return false;
    }

    @Override
    public void setNoClickCheck(boolean b) {

    }

    @Override
    public void setFlipY(boolean b) {

    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        runnable.run();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
