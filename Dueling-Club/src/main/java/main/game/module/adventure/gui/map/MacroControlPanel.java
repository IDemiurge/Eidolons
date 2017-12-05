package main.game.module.adventure.gui.map;

import main.client.cc.gui.neo.top.HC_ControlButton;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroManager;
import main.swing.generic.components.G_Panel;
import main.swing.listeners.ButtonHandler;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;

public class MacroControlPanel extends G_Panel implements ButtonHandler {
    private static final String TOWN = "Town";
    private static final String CAMP = "Camp";
    private boolean processing;

    public MacroControlPanel() {
        initButtons();
    }

    public void refresh() {
        initButtons();
    }

    private void initButtons() {
        removeAll();
        int i = 0;
        for (MACRO_CONTROLS command : MACRO_CONTROLS.values()) {
            String pos = "growx 100";
            add(getButton(StringMaster.getWellFormattedString(command
                    .toString())), pos);
            i++;
            if (i == 2) {
                i++;
            }
        }
        if (MacroGame.getGame().getPlayerParty().getTown() != null) {
            add(getButton(TOWN));
        } else {
            add(getButton(CAMP));
        }
        revalidate();

    }

    private Component getButton(String command) {
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
            MACRO_CONTROLS cmd = new EnumMaster<MACRO_CONTROLS>()
                    .retrieveEnumConst(MACRO_CONTROLS.class, command);
            if (cmd == null) {
                if (command.equals(TOWN)) {
                    Launcher.resetView(VIEWS.HC);// town mode?
                } else {
                    Launcher.resetView(VIEWS.HC);
                }
                return;
            }
            switch (cmd) {
                case FACTIONS:
                    break;
                case JOURNAL:
                    // update all logs in a special big window
                    break;
                case MENU:
                    Launcher.getMainManager().exitToMainMenu();
                    // ++ Continue Adventure button for quick return? Save game?
                    break;
                case WORLD:
                    MacroManager.refreshGui();
                    break;

            }

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            processing = false;
        }

    }

    @Override
    public void handleClick(String command, boolean alt) {

    }

    public enum MACRO_CONTROLS {
        MENU, JOURNAL, WORLD, FACTIONS // STATE-RESET?
    }
}
