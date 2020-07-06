package main.gui.components.controls;

import main.handlers.control.AvButtonHandler;
import main.launch.ArcaneVault;
import main.swing.generic.components.panels.G_ButtonPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class AV_ButtonPanel extends G_ButtonPanel {


    public AV_ButtonPanel() {
        super(AvButtonHandler.commands);
    }

    @Override
    public int getWrap() {
        return 2;
    }

    @Override
    public boolean isHorizontal() {
        return false;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        new Thread(() -> handleAction(e), e.getActionCommand() + " thread").start();
    }

    public void handleAction(ActionEvent e) {
        boolean alt = ActionEvent.ALT_MASK == (e.getModifiers() & ActionEvent.ALT_MASK);
        String command = ((JButton) e.getSource()).getActionCommand();
        ArcaneVault.getManager().getButtonHandler().
        handleButtonClick(alt, command);
    }



}