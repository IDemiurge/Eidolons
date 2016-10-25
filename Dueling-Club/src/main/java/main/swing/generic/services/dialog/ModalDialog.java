package main.swing.generic.services.dialog;

import main.swing.generic.components.G_Dialog;
import main.system.auxiliary.GuiManager;
import main.system.threading.WaitMaster;

import javax.swing.*;
import java.awt.*;

public abstract class ModalDialog extends G_Dialog {
    @Override
    protected boolean isAlwaysOnTop() {
        return true;
    }

    @Override
    public void show() {
        super.show();
        if (isAutoClose())
            new Thread(new Runnable() {
                public void run() {
                    WaitMaster.WAIT(50);
                    if (isCloseConditionMet())
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                close();
                            }
                        });

                }
            }, " thread").start();
    }

    protected boolean isAutoClose() {
        return false;
    }

    protected boolean isCloseConditionMet() {
        return false;
    }

    @Override
    protected boolean isReady() {
        return true;
    }

    @Override
    public boolean isCentered() {
        return true;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(GuiManager.getScreenWidthInt() / 3,
                GuiManager.getScreenHeightInt() / 2);
    }

    @Override
    public String getTitle() {
        return "Waiting for Game...";
    }

}
