package eidolons.libgdx.gui.menu.selection.difficulty;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import main.system.sound.SoundMaster.BUTTON_SOUND_MAP;

/**
 * Created by JustMe on 2/8/2018.
 */
public class DifficultyListPanel extends ItemListPanel {
    public DifficultyListPanel(DifficultySelectionPanel difficultySelectionPanel) {

    }

    protected BUTTON_SOUND_MAP getButtonSoundMap() {
        return    BUTTON_SOUND_MAP.SELECTION_SHARP;
    } }
