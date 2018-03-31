package main.game.logic.dungeon.editor.gui;

import main.game.logic.dungeon.editor.LE_DataMaster;
import main.game.logic.dungeon.editor.LE_MapMaster;
import main.game.logic.dungeon.editor.LE_ObjMaster;
import main.game.logic.dungeon.editor.LevelEditor;
import eidolons.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;

public class LE_ControlPanel extends G_Panel {
    public LE_ControlPanel() {
        super("fill");
        for (LE_CONTROLS c : LE_CONTROLS.values()) {
            add(getControlButton(c));
        }
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    protected void controlClicked(boolean alt, LE_CONTROLS c) {
        switch (c) {
            case UNDO:
                LE_ObjMaster.undo();
                // LevelEditor.getCurrentLevel().getDungeon().getMinimap().getGrid().resetZoom();
                // LevelEditor.getCurrentLevel().getDungeon().getMinimap().getGrid().resetOffset();
                break;
            case TRANSFORM:
                LE_MapMaster.transform();
                break;
            case REMOVE:
                LE_DataMaster.removeSelected();
                break;
            case GENERATE:
                LE_MapMaster.generateNew(LevelEditor.getCurrentMission(), LevelEditor
                        .getCurrentLevel(), !alt);
                break;
            case LOAD_LEVEL:
                LE_DataMaster.loadLevel();
                break;
            case LOAD_MISSION:
                LE_DataMaster.loadMission();
                break;
            case NEW_LEVEL:
                LevelEditor.newLevel(!alt);
                break;
            case NEW_MISSION:
                LevelEditor.newMission();
                break;
            case SAVE_LEVEL:
                LE_DataMaster.levelSaved(LevelEditor.getCurrentLevel());
                break;
            case SAVE_MISSION:
                LE_DataMaster.missionSaved(LevelEditor.getCurrentMission());
                break;
            default:
                break;

        }
    }

    private Component getControlButton(final LE_CONTROLS c) {
        return new CustomButton(StringMaster.getWellFormattedString(c.toString())) {
            public void handleAltClick() {
                SoundMaster.playStandardSound(STD_SOUNDS.MOVE);
                new Thread(new Runnable() {
                    public void run() {
                        controlClicked(true, c);
                    }
                }).start();
            }

            public void handleClick() {
                new Thread(new Runnable() {
                    public void run() {
                        controlClicked(false, c);
                    }
                }).start();
            }
        };
    }

    public enum LE_CONTROLS {
        UNDO,

        SAVE_LEVEL, LOAD_LEVEL, SAVE_MISSION, LOAD_MISSION, NEW_LEVEL, NEW_MISSION,

        GENERATE, REMOVE, TRANSFORM,
        // WORKSPACE
    }

}
