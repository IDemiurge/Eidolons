package main.game.logic.dungeon.editor.gui;

import main.content.PROPS;
import main.content.enums.DungeonEnums;
import main.content.values.properties.G_PROPS;
import main.game.logic.dungeon.editor.*;
import main.game.logic.dungeon.generator.LevelGenerator;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.NameMaster;

import java.awt.*;

public class LE_ControlPanel extends G_Panel {
    private final LE_CONTROLS[] controls = LE_CONTROLS.values() ;

    public LE_ControlPanel() {
        super("fill");
        for (LE_CONTROLS c : controls) {
            add(getControlButton(c));
        }
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    protected void controlClicked(boolean alt, LE_CONTROLS c) {
        switch (c) {
            case INFO:
                LE_MouseMaster.tip();
                break;
            case REMOVE:
                LE_DataMaster.removeSelected();
                break;
            case LOAD_LEVEL:
                LE_DataMaster.loadLevel();
                break;
            case NEW_LEVEL:
                LevelEditor.newLevel(!alt);
                break;
            case TOGGLE_INFO:
                LevelEditor.getMainPanel().toggleInfoPanel();
                break;
            case TOGGLE_UI:
                LevelEditor.getMainPanel().toggleUI();
                break;
            case SAVE_LEVEL:
                LE_DataMaster.levelSaved(LevelEditor.getCurrentLevel());
                break;
            case SAVE_ALL:
                for (Level level : LevelEditor.getMainPanel().getLevels()) {
                    LE_DataMaster.levelSaved(level, true);
                }
                break;
            case CLONE:
                LE_DataMaster.loadLevel(LevelEditor.getCurrentLevel().getPath());

                break;
            case RENAME:
                String name = DialogMaster.inputText( LevelEditor.getCurrentLevel().getDungeon().getName());
                LevelEditor.getCurrentLevel().getDungeon().setName(name);
                LE_DataMaster.levelSaved(LevelEditor.getCurrentLevel());
                break;
            case SUBFOLDER:
                String text = ListChooser.chooseEnum(DungeonEnums.DUNGEON_SUBFOLDER.class);
                if (text == null) {
                    text = DialogMaster.inputText();
                }
                LevelEditor.getCurrentLevel().getDungeon().setProperty(G_PROPS.DUNGEON_SUBFOLDER,
                        text);

                LE_DataMaster.levelSaved(LevelEditor.getCurrentLevel());
                break;

//            case UNDO:
//                LE_ObjMaster.undo();
//                // LevelEditor.getCurrentLevel().getDungeon().getMinimap().getGrid().resetZoom();
//                // LevelEditor.getCurrentLevel().getDungeon().getMinimap().getGrid().resetOffset();
//                break;
//            case TRANSFORM:
//                LE_MapMaster.transform();
//                break;
//            case NEW_MISSION:
//                LevelEditor.newMission();
//                break;
//            case SAVE_MISSION:
//                LE_DataMaster.missionSaved(LevelEditor.getCurrentMission());
//                break;
//            case GENERATE:
//                if (!alt){
//                    LevelGenerator.generate(LevelEditor.getCurrentMission(),
//                            LevelEditor
//                                    .getCurrentLevel());
//                    break;
//                }
//                LE_MapMaster.generateNew(LevelEditor.getCurrentMission(),
//                        LevelEditor
//                                .getCurrentLevel(), !alt);
//
//                break;
//
            case DIAGONAL:
                LE_ObjMaster.fillArea(true);
                break;
            case CLEAR:
                LevelEditor.getMapMaster().clearArea();
                break;
            case FILL:
                LE_ObjMaster.fillArea(false);
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

TOGGLE_INFO,
        SAVE_LEVEL, LOAD_LEVEL,  NEW_LEVEL,
        REMOVE, SAVE_ALL,
        CLONE, RENAME, SUBFOLDER, TOGGLE_UI, CLEAR, FILL, DIAGONAL, INFO,
//        GENERATE,
//        SAVE_MISSION, LOAD_MISSION, NEW_MISSION,
//        UNDO,TRANSFORM,

        // WORKSPACE
    }

}
