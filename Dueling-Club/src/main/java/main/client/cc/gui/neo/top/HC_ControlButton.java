package main.client.cc.gui.neo.top;

import main.swing.components.buttons.CustomButton;
import main.swing.listeners.ButtonHandler;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;

public class HC_ControlButton extends CustomButton {

    private String command = "";
    private ButtonHandler controls;

    public HC_ControlButton(String command, ButtonHandler controls) {
        super(VISUALS.BUTTON);
        this.command = command;
        this.controls = controls;
        this.text = command;
        x = getDefaultX();
    }

    @Override
    public void handleAltClick() {
        controls.handleClick(command, true);
    }

    @Override
    public void handleClick() {
        controls.handleClick(command, false);
    }

    @Override
    protected boolean isMoreY() {
        return false;
    }

    @Override
    protected int getDefaultY() {
        return visuals.getHeight() / 2 + 5;
    }

    @Override
    protected int getDefaultFontSize() {
        return 17;
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getFont(FONT.AVQ, getDefaultFontSize(), Font.PLAIN);
    }

    @Override
    protected int getDefaultX() {
        if (command != null) {
            return getCenteredX(command) * 10 / 16;
        }
        return 0;
    }

}
