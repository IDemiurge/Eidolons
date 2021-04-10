package libgdx.gui.menu.selection.difficulty;

import libgdx.gui.menu.selection.ItemListPanel;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.BUTTON_SOUND_MAP;

/**
 * Created by JustMe on 2/8/2018.
 */
public class DifficultyListPanel extends ItemListPanel {
    public DifficultyListPanel(DifficultySelectionPanel difficultySelectionPanel) {

    }

    protected BUTTON_SOUND_MAP getButtonSoundMap() {
        return    AudioEnums.BUTTON_SOUND_MAP.SELECTION_SHARP;
    } }
