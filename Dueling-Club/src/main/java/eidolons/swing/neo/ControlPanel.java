package eidolons.swing.neo;

import eidolons.client.cc.gui.neo.top.HC_ControlButton;
import main.swing.generic.components.G_Panel;
import main.swing.listeners.ButtonHandler;
import eidolons.system.audio.DC_SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.util.List;

public abstract class ControlPanel extends G_Panel implements ButtonHandler {
    protected static final String ID = "btn";
    protected boolean processing;
    HC_ControlButton buttons;

    public ControlPanel() {
        super("fillx");
        initButtons();
    }

    public void refresh() {
        // setLayout(new MigLayout("fillx"));
        initButtons();
    }

    protected void initButtons() {
        removeAll();

        int i = 0;
        for (String command : getControls()) {
            String pos = "growx 100";
            add(getButton(command), pos);
            i++;
            if (i == 2) {
                i++;
            }
        }
        revalidate();

    }

    protected abstract void handleCommand(String command);

    protected abstract List<String> getControls();

    protected Component getButton(String command) {
        HC_ControlButton button = new HC_ControlButton(command, this) {
            protected void playClickSound() {
            }

        };
        button.activateMouseListener();
        return button;
    }

    public void handleClick(String command) {
        if (processing) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            return;
        }
        processing = true;
        try {
            handleCommand(command);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            processing = false;

            // CharacterCreator.refreshGUI();
        }
    }

}
